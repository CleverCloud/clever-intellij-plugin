package com.cleverCloud.cleverIdea.api.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LogsSocket {
  @JsonProperty("_source") private LogsSocketSource source;

  public LogsSocketSource getSource() {
    return source;
  }
}
