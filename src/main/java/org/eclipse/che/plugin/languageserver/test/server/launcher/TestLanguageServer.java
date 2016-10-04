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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.typefox.lsapi.InitializeParams;
import io.typefox.lsapi.InitializeResult;
import io.typefox.lsapi.Message;
import io.typefox.lsapi.ServerCapabilities;
import io.typefox.lsapi.impl.InitializeResultImpl;
import io.typefox.lsapi.impl.ServerCapabilitiesImpl;
import io.typefox.lsapi.services.LanguageServer;
import io.typefox.lsapi.services.TextDocumentService;
import io.typefox.lsapi.services.WindowService;
import io.typefox.lsapi.services.WorkspaceService;
import io.typefox.lsapi.services.transport.AbstractLanguageEndpoint;

/**
 * 
 */
public class TestLanguageServer extends AbstractLanguageEndpoint implements LanguageServer {

	/** The usual Logger.*/
	private static final Logger LOGGER = LoggerFactory.getLogger(TestLanguageServer.class);
	
	private final Process testLanguageServerProcess;
	
	public TestLanguageServer(final Process testLanguageServerProcess) {
		super(Executors.newFixedThreadPool(10));
		this.testLanguageServerProcess = testLanguageServerProcess;
	}
	
	public void connect(final InputStream inputStream, final OutputStream outputStream) {
		LOGGER.info("Connecting with Test LSP process streams");
		
	}
	
	@Override
	public void exit() {
		testLanguageServerProcess.destroy();
	}

	@Override
	public TextDocumentService getTextDocumentService() {
		return new TextDocumentServiceImpl();
	}

	@Override
	public WindowService getWindowService() {
		return new WindowServiceImpl();
	}

	@Override
	public WorkspaceService getWorkspaceService() {
		return new WorkspaceServiceImpl();
	}

	@Override
	public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
		final InitializeResultImpl initializeResult = new InitializeResultImpl();
		final ServerCapabilitiesImpl serverCapabilities = new ServerCapabilitiesImpl();
		initializeResult.setCapabilities(serverCapabilities);
		return CompletableFuture.completedFuture(initializeResult);
	}

	@Override
	public void shutdown() {
		this.testLanguageServerProcess.destroy();
	}

	@Override
	public void onTelemetryEvent(Consumer<Object> callback) {

	}

	@Override
	public void accept(Message t) {
		
	}

	
}
