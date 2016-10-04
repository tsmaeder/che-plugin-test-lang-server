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

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import io.typefox.lsapi.CodeActionParams;
import io.typefox.lsapi.CodeLens;
import io.typefox.lsapi.CodeLensParams;
import io.typefox.lsapi.Command;
import io.typefox.lsapi.CompletionItem;
import io.typefox.lsapi.CompletionList;
import io.typefox.lsapi.DidChangeTextDocumentParams;
import io.typefox.lsapi.DidCloseTextDocumentParams;
import io.typefox.lsapi.DidOpenTextDocumentParams;
import io.typefox.lsapi.DidSaveTextDocumentParams;
import io.typefox.lsapi.DocumentFormattingParams;
import io.typefox.lsapi.DocumentHighlight;
import io.typefox.lsapi.DocumentOnTypeFormattingParams;
import io.typefox.lsapi.DocumentRangeFormattingParams;
import io.typefox.lsapi.DocumentSymbolParams;
import io.typefox.lsapi.Hover;
import io.typefox.lsapi.Location;
import io.typefox.lsapi.PublishDiagnosticsParams;
import io.typefox.lsapi.ReferenceParams;
import io.typefox.lsapi.RenameParams;
import io.typefox.lsapi.SignatureHelp;
import io.typefox.lsapi.SymbolInformation;
import io.typefox.lsapi.TextDocumentPositionParams;
import io.typefox.lsapi.TextEdit;
import io.typefox.lsapi.WorkspaceEdit;
import io.typefox.lsapi.services.TextDocumentService;

class TextDocumentServiceImpl implements TextDocumentService {

		@Override
		public CompletableFuture<CompletionList> completion(TextDocumentPositionParams position) {
			return CompletableFuture.completedFuture(new CompletionList() {
				@Override
				public boolean isIncomplete() {
					return false;
				}
				
				@Override
				public List<? extends CompletionItem> getItems() {
					return Collections.emptyList();
				}
			});
		}

		@Override
		public CompletableFuture<CompletionItem> resolveCompletionItem(CompletionItem unresolved) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public CompletableFuture<Hover> hover(TextDocumentPositionParams position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public CompletableFuture<SignatureHelp> signatureHelp(TextDocumentPositionParams position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public CompletableFuture<List<? extends Location>> definition(TextDocumentPositionParams position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public CompletableFuture<List<? extends Location>> references(ReferenceParams params) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public CompletableFuture<DocumentHighlight> documentHighlight(TextDocumentPositionParams position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public CompletableFuture<List<? extends SymbolInformation>> documentSymbol(DocumentSymbolParams params) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public CompletableFuture<List<? extends Command>> codeAction(CodeActionParams params) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public CompletableFuture<List<? extends CodeLens>> codeLens(CodeLensParams params) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public CompletableFuture<CodeLens> resolveCodeLens(CodeLens unresolved) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public CompletableFuture<List<? extends TextEdit>> formatting(DocumentFormattingParams params) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public CompletableFuture<List<? extends TextEdit>> rangeFormatting(DocumentRangeFormattingParams params) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public CompletableFuture<List<? extends TextEdit>> onTypeFormatting(DocumentOnTypeFormattingParams params) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public CompletableFuture<WorkspaceEdit> rename(RenameParams params) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void didOpen(DidOpenTextDocumentParams params) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void didChange(DidChangeTextDocumentParams params) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void didClose(DidCloseTextDocumentParams params) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void didSave(DidSaveTextDocumentParams params) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPublishDiagnostics(Consumer<PublishDiagnosticsParams> callback) {
			// TODO Auto-generated method stub
			
		}
		
	}