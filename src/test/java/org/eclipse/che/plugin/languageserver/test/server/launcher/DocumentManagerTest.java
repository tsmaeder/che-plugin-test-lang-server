package org.eclipse.che.plugin.languageserver.test.server.launcher;

import com.google.common.io.Files;
import org.eclipse.che.api.languageserver.exception.LanguageServerException;
import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextDocumentContentChangeEvent;
import org.eclipse.lsp4j.TextDocumentItem;
import org.eclipse.lsp4j.VersionedTextDocumentIdentifier;
import org.eclipse.lsp4j.services.LanguageClient;
import org.jboss.tools.lsp.ext.ExtendedLanguageServer;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DocumentManagerTest {
    static class ChangeFactory {
        private String uri;
        private int    currentVersion;

        public ChangeFactory(String uri, int currentVersion) {
            this.uri = uri;
            this.currentVersion = currentVersion;
        }

        public DidChangeTextDocumentParams newChange(int startLine, int startChar, int endLine, int endChar, int rangeLength,
                                                     String replacement) {
            DidChangeTextDocumentParams result = new DidChangeTextDocumentParams();
            VersionedTextDocumentIdentifier document = new VersionedTextDocumentIdentifier();
            document.setUri(uri);
            document.setVersion(currentVersion++);
            result.setTextDocument(document);
            result.setUri(uri);
            result.setContentChanges(Arrays.asList(createChange(startLine, startChar, endLine, endChar, rangeLength, replacement)));

            return result;
        }

        private TextDocumentContentChangeEvent createChange(int startLine, int startChar, int endLine, int endChar, int rangeLength,
                                                            String replacement) {
            TextDocumentContentChangeEvent result = new TextDocumentContentChangeEvent();
            result.setRange(new Range(new Position(startLine, startChar), new Position(endLine, endChar)));
            result.setRangeLength(rangeLength);
            result.setText(replacement);
            return result;
        }
    }

    @Test
    public void testSimpleInsert() throws LanguageServerException, InterruptedException, ExecutionException, TimeoutException, IOException {
        // given a test lang server launcher
        final TestLanguageServerLauncher testLanguageServerLauncher = new TestLanguageServerLauncher();
        assertTrue(testLanguageServerLauncher.isAbleToLaunch());
        File dir = Files.createTempDir();
        File file = File.createTempFile("smr", "", dir);
        LanguageClient client = Mockito.mock(LanguageClient.class);

        // when
        final ExtendedLanguageServer testLangServer = (ExtendedLanguageServer) testLanguageServerLauncher.launch(dir.getAbsolutePath(),
                                                                                                                 client);
        try {
            // then
            assertNotNull(testLangServer);

            Files.append("abcde", file, Charset.forName("utf-8"));

            ChangeFactory changeFactory = new ChangeFactory("foo", 0);

            TextDocumentItem initialContents = new TextDocumentItem();
            initialContents.setLanguageId("test");
            initialContents.setText("abcde");
            initialContents.setUri("foo");
            initialContents.setVersion(0);

            testLangServer.getTextDocumentService().didOpen(new DidOpenTextDocumentParams(initialContents, "abcde"));
            testLangServer.getTextDocumentService().didChange(changeFactory.newChange(0, 2, 0, 2, 0, "xy"));

            assertEquals("abxycde", testLangServer.getDocument("foo").get(30, TimeUnit.SECONDS));
        } finally {
            testLangServer.exit();
        }

    }

}
