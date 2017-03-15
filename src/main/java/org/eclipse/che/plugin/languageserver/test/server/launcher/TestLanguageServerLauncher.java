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

import com.google.inject.Singleton;
import io.typefox.lsapi.services.LanguageServer;
import io.typefox.lsapi.services.json.JsonBasedLanguageServer;
import jnr.enxio.channels.NativeSelectorProvider;
import jnr.unixsocket.UnixServerSocketChannel;
import jnr.unixsocket.UnixSocketAddress;
import jnr.unixsocket.UnixSocketChannel;
import org.eclipse.che.api.languageserver.exception.LanguageServerException;
import org.eclipse.che.api.languageserver.launcher.LanguageServerLauncherTemplate;
import org.eclipse.che.api.languageserver.server.dto.DtoServerImpls.LanguageDescriptionDTOImpl;
import org.eclipse.che.api.languageserver.shared.lsapi.LanguageDescriptionDTO;
import org.eclipse.che.api.languageserver.shared.model.LanguageDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
 
/**
 * 
 */
@Singleton
public class TestLanguageServerLauncher extends LanguageServerLauncherTemplate {

	private final static Logger LOGGER = LoggerFactory.getLogger(TestLanguageServerLauncher.class);
	private final static String JAVA_EXEC = System.getProperty("java.home") + "/bin/java";

	private static File launcherJar;

	private static final String LANGUAGE_ID = "test";
	private static final String[] EXTENSIONS = new String[] { "test" };
	private static final String[] MIME_TYPES = new String[] { "text/x-test" };

	private static final LanguageDescriptionDTO description;

	static {  
		description = new LanguageDescriptionDTOImpl();
		description.setFileExtensions(asList(EXTENSIONS));
		description.setLanguageId(LANGUAGE_ID);
		description.setMimeTypes(Arrays.asList(MIME_TYPES));
	}

	private UnixServerSocketChannel serverSocketInChannel;
	private UnixServerSocketChannel serverSocketOutChannel;
	private UnixSocketChannel socketInChannel;
	private UnixSocketChannel socketOutChannel;
    private Process process;
 
	/**
	 * Default constructor.
	 */
	public TestLanguageServerLauncher() {
	}
	
	@Override
	public LanguageDescription getLanguageDescription() {
		return description;
	}

	@Override
	public boolean isAbleToLaunch() {
		try {
			final File testLanguageServerJarFile = findJarFile();
			return testLanguageServerJarFile.exists();
		} catch (IOException e) {
			throw new IllegalStateException("Can't check if 'test' language server can start", e);
		}
	}
	
	public Process getProcess() {
        return process;
    }

	@Override
	protected Process startLanguageServerProcess(String projectPath) throws LanguageServerException {
		LOGGER.warn("Starting the 'Test' Language Server Process...");
		try {
			// creates Unix sockets and set system properties so the 'test lang'
			// server can look-up the Unix socket location 
			final File socketInFile = getSocketFile("che-testlang-in.sock");
			this.serverSocketInChannel = createServerSocketChannel(socketInFile);
			final File socketOutFile = getSocketFile("che-testlang-out.sock");
			this.serverSocketOutChannel = createServerSocketChannel(socketOutFile);
			final ExecutorService pool = Executors.newFixedThreadPool(2);
			final Future<UnixSocketChannel> socketInChannelFuture = pool.submit(() -> {
				return getSocketChannel(this.serverSocketInChannel);
			});
			final Future<UnixSocketChannel> socketOutChannelFuture = pool.submit(() -> {
				return getSocketChannel(this.serverSocketOutChannel);
			});
			// locates language server jar file
			final File testLanguageServerJarFile = findJarFile();
			// starts the Java process
			final ProcessBuilder processBuilder = new ProcessBuilder(JAVA_EXEC,
			        "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=1044",
					"-DSTDIN_PIPE_NAME=" + socketOutFile.getAbsolutePath(),
					"-DSTDOUT_PIPE_NAME=" + socketInFile.getAbsolutePath(), "-jar",
					testLanguageServerJarFile.toString(),
					 "debug");
			// specify the working directory to load classes from the other jar
			// files
			LOGGER.info("Launching 'test-lang' server with command line:\n{}",
					processBuilder.command().stream().collect(Collectors.joining(" ")));
			processBuilder.redirectError(Redirect.INHERIT);
			processBuilder.redirectOutput(Redirect.INHERIT);
			this.process = processBuilder.start();
			if (!process.isAlive()) {
				LOGGER.error("Couldn't start process : " + processBuilder.command());
			}
			// now, wait for the process to connect to the sockets
			this.socketInChannel = socketInChannelFuture.get(30, TimeUnit.SECONDS);
			LOGGER.info("New connection established on the 'IN' channel: {}", socketInChannel.getRemoteSocketAddress());
			this.socketOutChannel = socketOutChannelFuture.get(30, TimeUnit.SECONDS);
			LOGGER.info("New connection established on the 'OUT' channel: {}",
					socketOutChannel.getRemoteSocketAddress());
			LOGGER.info("'test-lang' server process started and connected to sockets.");
			return process;
		} catch (IOException | ExecutionException e) {
			throw new IllegalStateException("Can't start 'test-lang' server", e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return null;
		} catch (TimeoutException e) {
			throw new IllegalStateException("'test-lang' server did not connect to the sockets in time.", e);
		}

	}

	private static UnixServerSocketChannel createServerSocketChannel(final File socketFile) throws IOException {
		if (socketFile.exists()) {
			LOGGER.debug("Removing previous {} file", socketFile);
			socketFile.delete();
		}
		final UnixSocketAddress address = new UnixSocketAddress(socketFile);
		final UnixServerSocketChannel channel = UnixServerSocketChannel.open();
		channel.configureBlocking(false);
		channel.socket().bind(address);
		LOGGER.info("Created socket address at " + socketFile.getAbsolutePath());
		return channel;
	}

	private static UnixSocketChannel getSocketChannel(final UnixServerSocketChannel serverSocketChannel) {
		try (final Selector selector = NativeSelectorProvider.getInstance().openSelector()) {
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT, null);
			// new SocketActor(this.serverSocketInChannel));
			while (selector.select() > 0) {
				final Set<SelectionKey> keys = selector.selectedKeys();
				final Iterator<SelectionKey> iterator = keys.iterator();
				while (iterator.hasNext()) {
					// final SelectionKey selectionKey = iterator.next();
					// final SocketActor socketActor = (SocketActor)
					// selectionKey.attachment();
					final UnixSocketChannel socketInChannel = serverSocketChannel.accept();
					// iterator.remove();
					return socketInChannel;
				}
			}
		} catch (IOException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}

	private static File getSocketFile(final String fileName) {
		return new File(new File(System.getProperty("java.io.tmpdir")), fileName);
	}

	/**
	 * Finds the file matching the given {@code pathPattern}
	 * 
	 * @param baseDir
	 *            the base directory to start searching for the file
	 * @param pathPattern
	 *            the pattern to match the file
	 * @return the first matching file
	 * @throws IOException
	 *             if something went wrong while searching
	 * @throws URISyntaxException
	 * @throws IllegalStateException
	 *             if no matching file was found
	 */
	public static File findJarFile() throws IOException {
		if (launcherJar == null) {
			Path tmpFile = Files.createTempFile("launcher", ".jar");
			URL jarUrl = TestLanguageServerLauncher.class.getClassLoader().getResource("testls.jar");
			Files.copy(jarUrl.openStream(), tmpFile, StandardCopyOption.REPLACE_EXISTING);
			launcherJar= tmpFile.toFile();
			launcherJar.deleteOnExit();
		}
		return launcherJar;

	}

	@Override
	protected LanguageServer connectToLanguageServer(final Process languageServerProcess) {
        JsonBasedLanguageServer languageServer = new JsonBasedLanguageServer();
        languageServer.connect(Channels.newInputStream(this.socketInChannel), Channels.newOutputStream(this.socketOutChannel));
        return languageServer;
	}
}
