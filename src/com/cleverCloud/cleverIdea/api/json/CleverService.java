package com.cleverCloud.cleverIdea.api.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jetbrains.annotations.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CleverService {
  public String id;
  public String name;
  public String description;

  @NotNull
  public ServiceType getType() {
    //noinspection SpellCheckingInspection
    if (this.id.startsWith("addon_")) return ServiceType.ADDON;
    if (this.id.startsWith("app_")) return ServiceType.APPLICATION;

    return ServiceType.ORGANISATION;
  }

  enum ServiceType {
    ADDON,
    APPLICATION,
    ORGANISATION
  }
}
