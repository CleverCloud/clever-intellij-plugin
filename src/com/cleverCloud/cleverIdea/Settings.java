package com.cleverCloud.cleverIdea;

import com.cleverCloud.cleverIdea.api.json.Application;
import com.intellij.openapi.components.*;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@State(
  name = "CleverIdeaSettings",
  storages = {@Storage(id = "other", file = StoragePathMacros.PROJECT_FILE),
    @Storage(id = "dir", file = StoragePathMacros.PROJECT_CONFIG_DIR + "/cleverIdea.xml", scheme = StorageScheme.DIRECTORY_BASED)})
public class Settings implements PersistentStateComponent<Settings> {
  @Nullable public List<Application> apps = null;
  @Nullable public String oAuthToken = null;
  @Nullable public String oAuthSecret = null;

  @Override
  public Settings getState() {
    return this;
  }

  @Override
  public void loadState(@NotNull Settings state) {
    XmlSerializerUtil.copyBean(state, this);
  }

  @Nullable
  public List<Application> getApps() {
    return apps;
  }
}
