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

/**
 * Testing the {@link TestLanguageServerLauncher}.
 */
public class TestLanguageServerLauncherTest {

	@Test
	public void shouldFindJarFile() throws IOException {
		// given
		final String baseDir = System.getProperty("LIBS_BASE_DIR");
		Assert.assertNotNull("Failed to LIBS_BASE_DIR system property", baseDir);
		final String pathPattern = System.getProperty("PATH_PATTERN");
		Assert.assertNotNull("Failed to PATH_PATTERN system property", pathPattern);
		// when
		final File jarfile = TestLanguageServerLauncher.findJarFile(baseDir, pathPattern);
		// then
		Assert.assertNotNull("Failed to locate jar file", jarfile);
	}
}
