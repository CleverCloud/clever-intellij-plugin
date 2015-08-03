package com.cleverCloud.cleverIdea;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;

@State(
  name = "CleverIdeaSettings",
  storages = {@Storage(id = "other", file = StoragePathMacros.PROJECT_FILE),
    @Storage(id = "dir", file = StoragePathMacros.PROJECT_CONFIG_DIR + "/cleverIdea.xml", scheme = StorageScheme.DIRECTORY_BASED)})
public class Settings implements PersistentStateComponent<Settings> {
  public String appId;

  public static Settings getInstance(Project project) {
    return ServiceManager.getService(project, Settings.class);
  }

  @Override
  public Settings getState() {
    return this;
  }

  @Override
  public void loadState(Settings state) {
    XmlSerializerUtil.copyBean(state, this);
  }

}
