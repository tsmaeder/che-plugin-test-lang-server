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
import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.Test;

/**
 * Testing the {@link TestLanguageServerLauncher}.
 */
public class TestLanguageServerLauncherTest {

	
	@Test
	public void shouldFindJarFile() throws IOException, URISyntaxException {
		// given
		// when
		final File jarfile = TestLanguageServerLauncher.findJarFile();
		// then
		Assert.assertNotNull("Failed to locate jar file", jarfile);
	}
}
