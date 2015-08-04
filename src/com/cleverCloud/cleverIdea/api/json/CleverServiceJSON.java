package com.cleverCloud.cleverIdea.api.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CleverServiceJSON {

  private String id;
  private String name;
  private String description;

  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(String desc) {
    this.description = desc;
  }

  public ServiceType getType() {
    if (this.id.startsWith("addon_")) return ServiceType.ADDON;
    if (this.id.startsWith("app_")) return ServiceType.APPLICATION;

    return ServiceType.ORGANISATION;
  }

  public enum ServiceType {
    ADDON,
    APPLICATION,
    ORGANISATION
  }
}
