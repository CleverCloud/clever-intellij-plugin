package com.cleverCloud.cleverIdea.api.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LogsSocketSource {
  @JsonProperty("message") private String message;
  @JsonProperty("@timestamp") private String timestamp;

  @NotNull
  public String getLog() {
    return timestamp + ": " + message;
  }
}
