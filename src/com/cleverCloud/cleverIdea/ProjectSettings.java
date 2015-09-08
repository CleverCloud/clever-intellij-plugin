package com.cleverCloud.cleverIdea;

import com.cleverCloud.cleverIdea.api.json.Application;
import com.intellij.openapi.components.*;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

@State(
  name = "CleverIdeaProjectSettings",
  storages = {@Storage(id = "other", file = StoragePathMacros.PROJECT_FILE),
    @Storage(id = "dir", file = StoragePathMacros.PROJECT_CONFIG_DIR + "/cleverIdea.xml", scheme = StorageScheme.DIRECTORY_BASED)})
public class ProjectSettings implements PersistentStateComponent<ProjectSettings> {
  @NotNull public ArrayList<Application> applications = new ArrayList<>();
  @Nullable public Application lastUsedApplication = null;

  @Override
  public ProjectSettings getState() {
    return this;
  }

  @Override
  public void loadState(@NotNull ProjectSettings state) {
    XmlSerializerUtil.copyBean(state, this);
  }
}
