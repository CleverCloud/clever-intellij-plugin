/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Clever Cloud, SAS
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.cleverCloud.cleverIdea.utils;

import com.cleverCloud.cleverIdea.api.CcApi;
import com.cleverCloud.cleverIdea.api.json.LogsSocket;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.project.Project;
import org.java_websocket.client.DefaultSSLWebSocketClientFactory;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.handshake.ServerHandshake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class WebSocketCore extends WebSocketClient {

  @Nullable private String myLogSigner;
  private ConsoleView myConsoleView;

  public WebSocketCore(URI uri, @NotNull Project project, ConsoleView consoleView) throws NoSuchAlgorithmException, KeyManagementException {
    super(uri, new Draft_10(), null, 0);

    SSLContext sslContext = SSLContext.getInstance("TLS");
    sslContext.init(null, null, null);
    this.setWebSocketFactory(new DefaultSSLWebSocketClientFactory(sslContext));

    myLogSigner = CcApi.Companion.getInstance(project).wsLogSigner();
    myConsoleView = consoleView;
  }

  public static void printSocket(@NotNull ConsoleView consoleView, @NotNull String message) {
    consoleView.print(message, ConsoleViewContentType.NORMAL_OUTPUT);
  }

  @Override
  public void onClose(int code, String msg, boolean remote) {
    //printSocket(myConsoleView, "Connetion Closed\n");
  }

  @Override
  public void onError(@NotNull Exception e) {
    e.printStackTrace();
  }

  @Override
  public void onMessage(String message) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      LogsSocket logs = mapper.readValue(message, LogsSocket.class);
      printSocket(myConsoleView, logs.getSource().getLog());
    }
    catch (IOException e) {
      printSocket(myConsoleView, "Error, bad json log\n");
      e.printStackTrace();
    }
  }

  @Override
  public void onOpen(ServerHandshake arg0) {
    assert myLogSigner != null;
    this.send(myLogSigner);
    printSocket(myConsoleView, "Connected\n");
  }
}
