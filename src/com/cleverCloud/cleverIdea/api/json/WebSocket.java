package com.cleverCloud.cleverIdea.api.json;

public class WebSocket {
  public String message_type = "oauth";
  public String authorization;

  public WebSocket(String authorization) {
    this.authorization = authorization;
  }
}
