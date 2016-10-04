/*******************************************************************************
 * Copyright (c) 2012-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.plugin.languageserver.test.server.launcher;

import static java.util.Arrays.asList;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Optional;

import org.eclipse.che.plugin.languageserver.server.exception.LanguageServerException;
import org.eclipse.che.plugin.languageserver.server.launcher.LanguageServerLauncherTemplate;
import org.eclipse.che.plugin.languageserver.shared.model.LanguageDescription;
import org.eclipse.che.plugin.languageserver.shared.model.impl.LanguageDescriptionImpl;
import org.newsclub.net.unix.AFUNIXServerSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.typefox.lsapi.services.LanguageServer;

/**
 * 
 */
public class TestLanguageServerLauncher extends LanguageServerLauncherTemplate {

	private final static Logger LOGGER = LoggerFactory.getLogger(TestLanguageServerLauncher.class);
	private final static String JAVA_EXEC = System.getProperty("java.home") + "/bin/java";

	private static final String LIBS_PATH = "/home/user/che/ws-agent/webapps/wsagent/WEB-INF/lib/";
	private static final String LSP_LIB_NAME_PATTERN = "glob:org.jboss.tools.language-server.test.server*.jar";
	public static final String STDOUT_PIPE_NAME = "STDOUT_PIPE_NAME";
	public static final String STDIN_PIPE_NAME = "STDIN_PIPE_NAME";

	public static final String LANGUAGE_ID = "test";
	public static final String[] EXTENSIONS = new String[] { "test" };
	public static final String[] MIME_TYPES = new String[] { "text/x-test" };

	public static final LanguageDescriptionImpl description;

	static {
		description = new LanguageDescriptionImpl();
		description.setFileExtensions(asList(EXTENSIONS));
		description.setLanguageId(LANGUAGE_ID);
		description.setMimeTypes(Arrays.asList(MIME_TYPES));
	}

	@Override
	public LanguageDescription getLanguageDescription() {
		return description;
	}

	@Override
	public boolean isAbleToLaunch() {
		try {
			final File testLanguageServerJarFile = findJarFile(LIBS_PATH, LSP_LIB_NAME_PATTERN);
			return testLanguageServerJarFile.exists();
		} catch (IOException e) {
			throw new IllegalStateException("Can't check if 'test' language server can start", e);
		}
	}

	@Override
	protected Process startLanguageServerProcess(String projectPath) throws LanguageServerException {
		LOGGER.warn("Starting the 'Test' Language Server Process...");

		try {
			final File testLanguageServerJarFile = findJarFile(LIBS_PATH, LSP_LIB_NAME_PATTERN);
			final String pathToSocketIn = createSockets("junixsocket-test-in.sock");
			final String pathToSocketOut = createSockets("junixsocket-test-out.sock");
			final ProcessBuilder processBuilder = new ProcessBuilder(JAVA_EXEC,
					"-DSTDIN_PIPE_NAME="+pathToSocketIn , 
					"-DSTDOUT_PIPE_NAME="+pathToSocketOut , 
					"-jar",
					testLanguageServerJarFile.toString(),
					"-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=1044", "debug");
			// specify the working directory to load classes from the other jar files 
			processBuilder.directory(new File(LIBS_PATH));
			processBuilder.redirectInput(ProcessBuilder.Redirect.PIPE);
			processBuilder.redirectOutput(ProcessBuilder.Redirect.PIPE);
			final Process process = processBuilder.start();
			final Thread thread = new Thread(new StreamReader(process));
			thread.setDaemon(true);
			thread.start();

			if (!process.isAlive()) {
				LOGGER.error("Couldn't start process : " + processBuilder.command());
			}
			LOGGER.info("'test' language server process started.");
			return process;
		} catch (IOException e) {
			throw new IllegalStateException("Can't start 'test' language server", e);
		}

	}

	private String createSockets(final String fileName) throws IOException {
		final File socketFile = new File(new File(System.getProperty("java.io.tmpdir")), fileName);
		final AFUNIXSocketAddress socketAddress = new AFUNIXSocketAddress(socketFile);
		LOGGER.warn("Created socket at " + socketFile.getAbsolutePath());
		final AFUNIXServerSocket serverSocket = AFUNIXServerSocket.newInstance();
		serverSocket.bind(socketAddress);
		LOGGER.info("Bound socket address {}", socketAddress.getSocketFile());
		return socketFile.getAbsolutePath();
	}

	public static File findJarFile(final String baseDir, final String pathPattern) throws IOException {
		final PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher(pathPattern);
		LOGGER.info("Looking for file matching {} in {}", pathPattern, baseDir);
		final Optional<File> matchFile = Files.find(Paths.get(baseDir), 1, (path,
				basicFileAttributes) -> basicFileAttributes.isRegularFile() && pathMatcher.matches(path.getFileName()))
				.map(path -> path.toFile()).findFirst();
		if (!matchFile.isPresent()) {
			throw new IllegalStateException(MessageFormat
					.format("Damn'it ! Failed to locate jar matching ''{0}'' in ''{1}''", pathPattern, baseDir));
		}
		final File testLanguageServerJarFile = matchFile.get();
		LOGGER.info("Found jar {} for 'test' language support", testLanguageServerJarFile.getAbsolutePath());
		return testLanguageServerJarFile;
	}

	@Override
	protected LanguageServer connectToLanguageServer(final Process languageServerProcess) {
		final TestLanguageServer languageServer = new TestLanguageServer(languageServerProcess);
		languageServer.connect(languageServerProcess.getInputStream(), languageServerProcess.getOutputStream());
		return languageServer;
	}

	private static class StreamReader implements Runnable {
		public Process process;

		public StreamReader(Process process) {
			this.process = process;
		}

		@Override
		public void run() {
			try (BufferedReader reader = new BufferedReader(
					new InputStreamReader(process.getErrorStream(), "UTF-8"));) {
				while (process.isAlive()) {
					try {
						final String errorLine = reader.readLine();
						LOGGER.error("languageserver {} : {}", process, errorLine);
					} catch (IOException e) {
						LOGGER.error(e.getMessage(), e);
					}
				}

			} catch (IOException e) {
				LOGGER.error("Error while opening or closing the process error stream", e);
			}
		}
	}

}
