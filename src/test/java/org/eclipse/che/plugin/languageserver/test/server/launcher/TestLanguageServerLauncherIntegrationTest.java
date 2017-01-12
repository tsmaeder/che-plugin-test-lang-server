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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.che.api.languageserver.exception.LanguageServerException;
import org.junit.Test;

import io.typefox.lsapi.InitializeResult;
import io.typefox.lsapi.builders.InitializeParamsBuilder;
import io.typefox.lsapi.impl.ClientCapabilitiesImpl;
import io.typefox.lsapi.services.LanguageServer;

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
				.launch(System.getProperty("java.tmp.io"));
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
				.launch(System.getProperty("java.tmp.io"));
		// then
		assertNotNull(testLangServerProcess);
		
		System.out.println("calling initialize");
		CompletableFuture<InitializeResult> initialized = testLangServerProcess.initialize(new InitializeParamsBuilder()
				.capabilities(new ClientCapabilitiesImpl()).clientName("boofl").rootPath("/tmp").build());
		initialized.get(30, TimeUnit.SECONDS);

		testLangServerProcess.exit();

	}
}
