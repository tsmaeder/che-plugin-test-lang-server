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

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Testing the {@link TestLanguageServerLauncher}.
 */
public class TestLanguageServerLauncherTest {

	/** The usual Logger.*/
	private static final Logger LOGGER = LoggerFactory.getLogger(TestLanguageServerLauncherTest.class);
	
	@Test
	public void shouldFindJarFile() throws IOException {
		// given
		final String baseDir = System.getProperty("user.dir") + File.separator + "test-lang-server-lib";
		// when
		final File jarfile = TestLanguageServerLauncher.findJarFile(baseDir, TestLanguageServerLauncher.LSP_LIB_NAME_PATTERN);
		// then
		Assert.assertNotNull("Failed to locate jar file", jarfile);
	}
}
