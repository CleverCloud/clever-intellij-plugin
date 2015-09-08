package com.cleverCloud.cleverIdea.api.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Application extends CleverService {
  public ApplicationDeployment deployment;
  public Organisation parent;

  public Application() {
    this.parent = new Organisation();
    this.parent.id = "self";
  }

  @Override
  public String toString() {
    String name;
    if (!"self".equals(parent.id)) {
      name = this.parent.name + " / " + this.name;
    }
    else {
      name = this.name;
    }
    return name;
  }
}
