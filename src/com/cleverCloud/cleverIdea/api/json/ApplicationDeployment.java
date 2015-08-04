package com.cleverCloud.cleverIdea.api.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicationDeployment {

  private String url;
  private String type;

  public String getUrl() {
    return this.url;
  }

  public String getType() {
    return this.type;
  }
}
