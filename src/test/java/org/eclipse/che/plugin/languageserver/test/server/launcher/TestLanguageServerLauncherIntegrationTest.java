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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.lucene.analysis.Analyzer.ReuseStrategy;
import org.eclipse.che.api.languageserver.exception.LanguageServerException;
import org.eclipse.lsp4j.ClientCapabilities;
import org.eclipse.lsp4j.DidSaveTextDocumentParams;
import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.MessageActionItem;
import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.MessageType;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.ShowMessageRequestParams;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageServer;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.google.common.io.Files;

/**
 * Testing the {@link TestLanguageServerLauncher}.
 */
public class TestLanguageServerLauncherIntegrationTest {

	@Test
	public void shouldLaunchLanguageServer() throws LanguageServerException {
		// given a test lang server launcher
		final TestLanguageServerLauncher testLanguageServerLauncher = new TestLanguageServerLauncher();
		assertTrue(testLanguageServerLauncher.isAbleToLaunch());
		// when
		final LanguageServer testLangServerProcess = testLanguageServerLauncher
				.launch(System.getProperty("java.tmp.io"), new LanguageClient() {
					
					@Override
					public void telemetryEvent(Object object) {
						
					}
					
					@Override
					public CompletableFuture<MessageActionItem> showMessageRequest(ShowMessageRequestParams requestParams) {
						return null;
					}
					
					@Override
					public void showMessage(MessageParams messageParams) {
						
					}
					
					@Override
					public void publishDiagnostics(PublishDiagnosticsParams diagnostics) {
						
					}
					
					@Override
					public void logMessage(MessageParams message) {
					}
				});
		// then
		assertNotNull(testLangServerProcess);
		testLangServerProcess.exit();

	}

	@Test
	public void shouldReplyToInitialize() throws LanguageServerException, InterruptedException, ExecutionException, TimeoutException {
		// given a test lang server launcher
		final TestLanguageServerLauncher testLanguageServerLauncher = new TestLanguageServerLauncher();
		assertTrue(testLanguageServerLauncher.isAbleToLaunch());
		// when
		final LanguageServer testLangServerProcess = testLanguageServerLauncher
				.launch(System.getProperty("java.tmp.io"), new LanguageClient() {
					
					@Override
					public void telemetryEvent(Object object) {
						
					}
					
					@Override
					public CompletableFuture<MessageActionItem> showMessageRequest(ShowMessageRequestParams requestParams) {
						return null;
					}
					
					@Override
					public void showMessage(MessageParams messageParams) {
					}
					
					@Override
					public void publishDiagnostics(PublishDiagnosticsParams diagnostics) {
					}
					
					@Override
					public void logMessage(MessageParams message) {
					}
				});
		// then
		assertNotNull(testLangServerProcess);
		
		System.out.println("calling initialize");
		InitializeParams initializeParams = new InitializeParams();
		initializeParams.setCapabilities(new ClientCapabilities());
		initializeParams.setRootUri("file:///tmp");
		CompletableFuture<InitializeResult> initialized = testLangServerProcess.initialize(initializeParams);
		initialized.get(30, TimeUnit.SECONDS);

		testLangServerProcess.exit();

	}
	
	@Test
	public void shouldSendShowMessageRequest() throws LanguageServerException, InterruptedException, ExecutionException, TimeoutException, IOException {
		// given a test lang server launcher
		final TestLanguageServerLauncher testLanguageServerLauncher = new TestLanguageServerLauncher();
		assertTrue(testLanguageServerLauncher.isAbleToLaunch());
		File dir = Files.createTempDir();
		File file = File.createTempFile("smr", "", dir);
		LanguageClient client = Mockito.mock(LanguageClient.class);
		Mockito.when(client.showMessageRequest(Mockito.any())).thenReturn(CompletableFuture.completedFuture(new MessageActionItem("foobar")));
		
		// when
		final LanguageServer testLangServer= testLanguageServerLauncher
				.launch(dir.getAbsolutePath(), client);
		// then
		assertNotNull(testLangServer);
		
		Files.append("window/showMessageRequest:Error:Apply:the message\n", file, Charset.forName("utf-8"));
		testLangServer.getTextDocumentService().didSave(new DidSaveTextDocumentParams(new TextDocumentIdentifier(file.toURI().toString()), "window/showMessageRequest:Error:Command: a message"));
		Mockito.verify(client, Mockito.timeout(5000)).showMessageRequest(Matchers.any());
	}
}
