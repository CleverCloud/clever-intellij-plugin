package com.cleverCloud.cleverIdea.api.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ApplicationJSON extends CleverServiceJSON {

  private ApplicationDeployment deployment;

  private OrganisationJSON parent;

  public ApplicationJSON() {
    this.parent = new OrganisationJSON();
    this.parent.setId("self");
  }

  public void setParent(OrganisationJSON parent) {
    this.parent = parent;
  }

  public OrganisationJSON getParent() {
    return this.parent;
  }

  public ApplicationDeployment getDeployment() {
    return this.deployment;
  }
}
