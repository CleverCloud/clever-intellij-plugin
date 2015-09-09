package com.cleverCloud.cleverIdea.utils;

import com.cleverCloud.cleverIdea.api.CcApi;
import com.cleverCloud.cleverIdea.api.json.LogsSocket;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
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
  private Editor myEditor;

  public WebSocketCore(URI uri, @NotNull Project project, Editor editor) throws NoSuchAlgorithmException, KeyManagementException {
    super(uri, new Draft_10(), null, 0);

    SSLContext sslContext = SSLContext.getInstance("TLS");
    sslContext.init(null, null, null);
    this.setWebSocketFactory(new DefaultSSLWebSocketClientFactory(sslContext));

    myLogSigner = CcApi.getInstance(project).wsLogSigner();
    myEditor = editor;
  }

  public static void printSocket(@NotNull Editor editor, @NotNull String message) {
    new WriteCommandAction(null) {
      @Override
      protected void run(@NotNull Result result) throws Throwable {
        editor.getDocument().insertString(editor.getCaretModel().getOffset(), message);
      }
    }.execute();
  }

  @Override
  public void onClose(int code, String msg, boolean remote) {
    //printSocket(myEditor, "Connetion Closed\n");
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
      printSocket(myEditor, logs.getSource().getLog());
    }
    catch (IOException e) {
      printSocket(myEditor, "Error, bad json log\n");
      e.printStackTrace();
    }
  }

  @Override
  public void onOpen(ServerHandshake arg0) {
    this.send(myLogSigner);
    printSocket(myEditor, "Connected\n");
  }
}
