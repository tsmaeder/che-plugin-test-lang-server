package org.eclipse.che.plugin.languageserver.test.server.launcher;

import org.eclipse.che.api.languageserver.registry.LanguageRegistrar;
import org.eclipse.che.api.languageserver.registry.LanguageServerRegistry;
import org.eclipse.che.api.languageserver.shared.model.impl.LanguageDescriptionImpl;

import static java.util.Arrays.asList;

public class TestLanguage implements LanguageRegistrar {
    public static final String LANGUAGE_ID = "test";
    private static final String[] EXTENSIONS = new String[] { "test" };
    private static final String[] MIME_TYPES = new String[] { "text/x-test" };

    @Override
    public void register(LanguageServerRegistry registry) {
        LanguageDescriptionImpl description = new LanguageDescriptionImpl();
        description.setFileExtensions(asList(EXTENSIONS));
        description.setLanguageId(LANGUAGE_ID);
        description.setMimeTypes(asList(MIME_TYPES));
        registry.registerLanguage(description);
    }

}
