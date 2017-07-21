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

import com.google.common.io.Files;
import org.eclipse.che.api.languageserver.exception.LanguageServerException;
import org.eclipse.lsp4j.ClientCapabilities;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.DidSaveTextDocumentParams;
import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.ShowMessageRequestParams;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.TextDocumentItem;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageServer;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Testing the {@link TestLanguageServerLauncher}.
 */
public class TestLanguageServerLauncherIntegrationTest {

    @Test
    public void shouldLaunchLanguageServer() throws LanguageServerException, InterruptedException {
        // given a test lang server launcher
        final TestLanguageServerLauncher testLanguageServerLauncher = new TestLanguageServerLauncher();
        assertTrue(testLanguageServerLauncher.isAbleToLaunch());
        // when
        final LanguageServer testLangServerProcess = testLanguageServerLauncher.launch(System.getProperty("java.tmp.io"),
                                                                                       new LanguageClient() {

                                                                                           @Override
                                                                                           public void telemetryEvent(Object object) {

                                                                                           }

                                                                                           @Override
                                                                                           public CompletableFuture<Void> showMessageRequest(ShowMessageRequestParams requestParams) {
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
        try {
            // then
            assertNotNull(testLangServerProcess);
        } finally {
            testLangServerProcess.exit();
            testLanguageServerLauncher.getProcess().waitFor(5000, TimeUnit.MILLISECONDS);
        }

    }

    @Test
    public void shouldReplyToInitialize() throws LanguageServerException, InterruptedException, ExecutionException, TimeoutException {
        // given a test lang server launcher
        final TestLanguageServerLauncher testLanguageServerLauncher = new TestLanguageServerLauncher();
        assertTrue(testLanguageServerLauncher.isAbleToLaunch());
        // when
        final LanguageServer testLangServerProcess = testLanguageServerLauncher.launch(System.getProperty("java.tmp.io"),
                                                                                       new LanguageClient() {

                                                                                           @Override
                                                                                           public void telemetryEvent(Object object) {

                                                                                           }

                                                                                           @Override
                                                                                           public CompletableFuture<Void> showMessageRequest(ShowMessageRequestParams requestParams) {
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
        try {
            // then
            assertNotNull(testLangServerProcess);

            System.out.println("calling initialize");
            InitializeParams initializeParams = new InitializeParams();
            initializeParams.setCapabilities(new ClientCapabilities());
            initializeParams.setRootPath("file:///tmp");
            CompletableFuture<InitializeResult> initialized = testLangServerProcess.initialize(initializeParams);
            initialized.get(30, TimeUnit.SECONDS);

        } finally {
            testLangServerProcess.exit();
            testLanguageServerLauncher.getProcess().waitFor(5000, TimeUnit.MILLISECONDS);
       }

    }

    @Test
    public void shouldSendShowMessageRequest()
                    throws LanguageServerException, InterruptedException, ExecutionException, TimeoutException, IOException {
        // given a test lang server launcher
        final TestLanguageServerLauncher testLanguageServerLauncher = new TestLanguageServerLauncher();
        assertTrue(testLanguageServerLauncher.isAbleToLaunch());
        LanguageClient client = Mockito.mock(LanguageClient.class);
        Mockito.when(client.showMessageRequest(Mockito.any())).thenReturn(CompletableFuture.completedFuture(null));

        // when
        final LanguageServer testLangServer = testLanguageServerLauncher.launch("", client);
        try {
            // then
            assertNotNull(testLangServer);

            TextDocumentItem item = new TextDocumentItem();
            item.setText("window/showMessageRequest:Error:Apply:the message\n");
            item.setUri("file:///foo");

            testLangServer.getTextDocumentService().didOpen(new DidOpenTextDocumentParams(item, item.getText()));
            testLangServer.getTextDocumentService().didSave(new DidSaveTextDocumentParams(new TextDocumentIdentifier("file:///foo")));
            Mockito.verify(client, Mockito.timeout(5000)).showMessageRequest(Matchers.any());
        } finally {
            testLangServer.exit();
            testLanguageServerLauncher.getProcess().waitFor(5000, TimeUnit.MILLISECONDS);
        }
    }
}
