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

import org.eclipse.che.api.languageserver.exception.LanguageServerException;
import org.junit.Test;

import io.typefox.lsapi.services.LanguageServer;

/**
 * Testing the {@link TestLanguageServerLauncher}.
 */
public class TestLanguageServerLauncherIntegrationTest {

	@Test
	public void shouldLaunchLanguageServer() throws LanguageServerException {
		// given a test lang server launcher
		final String libsPath = System.getProperty("user.dir") + File.separator + "test-lang-server-lib";
		assertTrue(new File(libsPath).exists());
		final TestLanguageServerLauncher testLanguageServerLauncher = new TestLanguageServerLauncher(libsPath);
		assertTrue(testLanguageServerLauncher.isAbleToLaunch());
		// when
		final LanguageServer testLangServerProcess = testLanguageServerLauncher
				.launch(System.getProperty("java.tmp.io"));
		// then
		assertNotNull(testLangServerProcess);
		testLangServerProcess.exit();
		
	}
}
