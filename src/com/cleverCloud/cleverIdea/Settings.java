package com.cleverCloud.cleverIdea;

import com.cleverCloud.cleverIdea.api.json.Application;
import com.intellij.openapi.components.*;
import com.intellij.util.xmlb.XmlSerializerUtil;

import java.util.List;

@State(
  name = "CleverIdeaSettings",
  storages = {@Storage(id = "other", file = StoragePathMacros.PROJECT_FILE),
    @Storage(id = "dir", file = StoragePathMacros.PROJECT_CONFIG_DIR + "/cleverIdea.xml", scheme = StorageScheme.DIRECTORY_BASED)})
public class Settings implements PersistentStateComponent<Settings> {
  public List<Application> apps = null;
  public String oAuthToken = null;
  public String oAuthSecret = null;

  @Override
  public Settings getState() {
    return this;
  }

  @Override
  public void loadState(Settings state) {
    XmlSerializerUtil.copyBean(state, this);
  }

  public List<Application> getApps() {
    return apps;
  }
}
