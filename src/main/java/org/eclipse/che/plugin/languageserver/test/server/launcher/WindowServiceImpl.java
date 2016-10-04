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

import java.util.function.Consumer;

import io.typefox.lsapi.MessageParams;
import io.typefox.lsapi.ShowMessageRequestParams;
import io.typefox.lsapi.services.WindowService;

/**
 * Default {@link WindowService} implementation.
 */
public class WindowServiceImpl implements WindowService {

	@Override
	public void onShowMessage(Consumer<MessageParams> callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onShowMessageRequest(Consumer<ShowMessageRequestParams> callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLogMessage(Consumer<MessageParams> callback) {
		// TODO Auto-generated method stub

	}

}
