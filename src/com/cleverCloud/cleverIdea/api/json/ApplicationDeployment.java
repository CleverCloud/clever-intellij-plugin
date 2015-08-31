package com.cleverCloud.cleverIdea.api.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import git4idea.repo.GitRemote;
import git4idea.repo.GitRepository;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicationDeployment {
  public String url;
  public String type;
  public String repository;
}
