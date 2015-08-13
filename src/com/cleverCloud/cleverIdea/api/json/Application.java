package com.cleverCloud.cleverIdea.api.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import git4idea.repo.GitRemote;

@JsonIgnoreProperties(value = {"myRemote"}, ignoreUnknown = true)
public class Application extends CleverService {
  private ApplicationDeployment deployment;
  private GitRemote myRemote;
  private Organisation parent;

  public Application() {
    this.parent = new Organisation();
    this.parent.setId("self");
  }

  public void setParent(Organisation parent) {
    this.parent = parent;
  }

  public Organisation getParent() {
    return this.parent;
  }

  public ApplicationDeployment getDeployment() {
    return this.deployment;
  }

  public GitRemote getRemote() {
    return myRemote;
  }
}
