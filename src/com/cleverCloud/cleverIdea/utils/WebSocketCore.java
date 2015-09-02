package com.cleverCloud.cleverIdea.utils;

import com.cleverCloud.cleverIdea.api.CcApi;
import com.cleverCloud.cleverIdea.api.json.LogsSocket;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.project.Project;
import org.java_websocket.client.DefaultSSLWebSocketClientFactory;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.handshake.ServerHandshake;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class WebSocketCore extends WebSocketClient {

  private String myLogSigner;

  public WebSocketCore(URI uri, Project project) throws NoSuchAlgorithmException, KeyManagementException {
    super(uri, new Draft_10(), null, 0);

    SSLContext sslContext = SSLContext.getInstance("TLS");
    sslContext.init(null, null, null);
    this.setWebSocketFactory(new DefaultSSLWebSocketClientFactory(sslContext));

    myLogSigner = CcApi.getInstance(project).wsLogSigner();
  }

  public void printSocket(String message) {
    /*if (ConsoleUtils.consoleExist(this.name)) {
      ConsoleUtils.printMessage(this.name, message);
    }
    else {
      this.close();
    }*/
    System.out.println(message);
  }

  @Override
  public void onClose(int code, String msg, boolean remote) {
    /*if (ConsoleUtils.consoleExist(name)) {
      this.printSocket("Connection Closed");
    }
    else {*/
    System.out.println("Connetion Closed");
    //}
  }

  @Override
  public void onError(Exception e) {
    e.printStackTrace();
  }

  @Override
  public void onMessage(String message) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      LogsSocket logs = mapper.readValue(message, LogsSocket.class);
      this.printSocket(logs.getSource().getLog());
    }
    catch (IOException e) {
      this.printSocket("Error, bad json log");
      e.printStackTrace();
    }
  }

  @Override
  public void onOpen(ServerHandshake arg0) {
    this.send(myLogSigner);
    this.printSocket("Connected");
  }
}
