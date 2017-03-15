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

import io.typefox.lsapi.InitializeResult;
import io.typefox.lsapi.impl.ClientCapabilitiesImpl;
import io.typefox.lsapi.impl.InitializeParamsImpl;
import io.typefox.lsapi.services.LanguageServer;
import org.eclipse.che.api.languageserver.exception.LanguageServerException;
import org.junit.Test;

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
        final LanguageServer testLangServerProcess = testLanguageServerLauncher.launch(System.getProperty("java.tmp.io"));
        try {
            // then
            assertNotNull(testLangServerProcess);
        } finally {
            testLangServerProcess.exit();
            testLanguageServerLauncher.getProcess().waitFor();
        }

    }

    @Test
    public void shouldReplyToInitialize() throws LanguageServerException, InterruptedException, ExecutionException, TimeoutException {
        // given a test lang server launcher
        final TestLanguageServerLauncher testLanguageServerLauncher = new TestLanguageServerLauncher();
        assertTrue(testLanguageServerLauncher.isAbleToLaunch());
        // when
        final LanguageServer testLangServerProcess = testLanguageServerLauncher.launch(System.getProperty("java.tmp.io"));
        try {
            // then

            // then
            assertNotNull(testLangServerProcess);

            System.out.println("calling initialize");
            InitializeParamsImpl initializeParams = new InitializeParamsImpl();
            initializeParams.setCapabilities(new ClientCapabilitiesImpl());
            initializeParams.setRootPath("/tmp");
            CompletableFuture<InitializeResult> initialized = testLangServerProcess.initialize(initializeParams);
            initialized.get(30, TimeUnit.SECONDS);
        } finally {
            testLangServerProcess.exit();
            testLanguageServerLauncher.getProcess().waitFor();
        }

    }
}
