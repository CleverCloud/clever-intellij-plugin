package com.cleverCloud.cleverIdea.api.json;

import com.cleverCloud.cleverIdea.api.CcApi;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;

import java.io.IOException;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganisationJSON extends CleverServiceJSON {

  public ApplicationJSON[] getChildren(Project project) {
    ObjectMapper mapper = new ObjectMapper();
    CcApi ccApi = ServiceManager.getService(project, CcApi.class);
    String json = ccApi.callApi((this.getId().equals("self") ? "/" : "/organisations/") + this.getId() + "/applications");
    ApplicationJSON[] children = null;

    try {
      children = mapper.readValue(json, ApplicationJSON[].class);
      for (ApplicationJSON app : children) {
        app.setParent(this);
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }

    return children;
  }
}
