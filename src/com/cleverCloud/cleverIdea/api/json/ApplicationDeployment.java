package com.cleverCloud.cleverIdea.api.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicationDeployment {
  public String url;
  public String type;
  public String repository;
}
