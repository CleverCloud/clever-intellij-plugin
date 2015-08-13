package com.cleverCloud.cleverIdea;

import com.cleverCloud.cleverIdea.api.json.Application;
import com.intellij.openapi.components.*;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@State(
  name = "CleverIdeaSettings",
  storages = {@Storage(id = "other", file = StoragePathMacros.PROJECT_FILE),
    @Storage(id = "dir", file = StoragePathMacros.PROJECT_CONFIG_DIR + "/cleverIdea.xml", scheme = StorageScheme.DIRECTORY_BASED)})
public class Settings implements PersistentStateComponent<Settings> {
  @NotNull public ArrayList<Application> apps = new ArrayList<>();
  @NotNull public String oAuthToken = "";
  @NotNull public String oAuthSecret = "";

  @Override
  public Settings getState() {
    return this;
  }

  @Override
  public void loadState(@NotNull Settings state) {
    XmlSerializerUtil.copyBean(state, this);
  }
}
