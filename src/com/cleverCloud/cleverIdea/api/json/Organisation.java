package com.cleverCloud.cleverIdea.api.json;

import com.cleverCloud.cleverIdea.api.CcApi;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Organisation extends CleverService {

  @Nullable
  public Application[] getChildren(@NotNull Project project) {
    ObjectMapper mapper = new ObjectMapper();
    CcApi ccApi = ServiceManager.getService(project, CcApi.class);
    String json = ccApi.callApi((this.getId().equals("self") ? "/" : "/organisations/") + this.getId() + "/applications");
    Application[] children = null;

    try {
      children = mapper.readValue(json, Application[].class);
      for (Application app : children) {
        app.setParent(this);
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }

    return children;
  }
}
