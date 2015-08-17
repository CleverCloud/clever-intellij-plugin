package com.cleverCloud.cleverIdea.api.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import git4idea.repo.GitRemote;
import git4idea.repo.GitRepository;

@JsonIgnoreProperties(value = {"myRemote"}, ignoreUnknown = true)
public class Application extends CleverService {
  private ApplicationDeployment deployment;
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
}
