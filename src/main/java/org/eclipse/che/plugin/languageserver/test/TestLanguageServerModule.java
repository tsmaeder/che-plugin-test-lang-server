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

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import org.eclipse.che.api.languageserver.launcher.LanguageServerLauncher;
import org.eclipse.che.api.languageserver.shared.model.LanguageDescription;
import org.eclipse.che.inject.DynaModule;
import org.eclipse.che.plugin.languageserver.test.server.launcher.TestLanguageServerLauncher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

import static java.util.Arrays.asList;



/**
 * 'Test' Language Server Module
 */
@DynaModule
public class TestLanguageServerModule extends AbstractModule {

	private final static Logger LOGGER = LoggerFactory.getLogger(TestLanguageServerModule.class);

    public static final String LANGUAGE_ID = "test";
    private static final String[] EXTENSIONS = new String[] { "test" };
    private static final String MIME_TYPE = "text/x-test" ;


	@Override
	protected void configure() {
		LOGGER.info("Configuring " + this.getClass().getName());
		Multibinder.newSetBinder(binder(), LanguageServerLauncher.class).addBinding()
				.to(TestLanguageServerLauncher.class);
		
        LanguageDescription description = new LanguageDescription();
        description.setFileExtensions(asList(EXTENSIONS));
        description.setFileNames(Collections.singletonList("test.xml"));
        description.setLanguageId(LANGUAGE_ID);
        description.setMimeType(MIME_TYPE);
        Multibinder.newSetBinder(binder(), LanguageDescription.class).addBinding().toInstance(description);

	}
}
