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
package org.eclipse.che.plugin.languageserver.test;

import org.eclipse.che.inject.DynaModule;
import org.eclipse.che.plugin.languageserver.server.launcher.LanguageServerLauncher;
import org.eclipse.che.plugin.languageserver.test.server.launcher.TestLanguageServerLauncher;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;


/**
 * 'Test' Language Server Module
 */
@DynaModule
public class TestLanguageServerModule extends AbstractModule {

	@Override
	protected void configure() {
		Multibinder.newSetBinder(binder(), LanguageServerLauncher.class).addBinding()
				.to(TestLanguageServerLauncher.class);
		bind(MyService.class); 
	}
}