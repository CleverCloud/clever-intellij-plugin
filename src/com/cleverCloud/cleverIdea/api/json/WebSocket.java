package com.cleverCloud.cleverIdea.api.json;

import org.jetbrains.annotations.NotNull;

public class WebSocket {
  @NotNull public String message_type = "oauth";
  public String authorization;

  public WebSocket(String authorization) {
    this.authorization = authorization;
  }
}
