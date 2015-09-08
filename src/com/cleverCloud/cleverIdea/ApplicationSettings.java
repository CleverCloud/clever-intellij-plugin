package com.cleverCloud.cleverIdea;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
  name = "CleverIdeaApplicationSettings",
  storages = @Storage(id = "other", file = StoragePathMacros.APP_CONFIG + "cleverIdea.xml"))
public class ApplicationSettings implements PersistentStateComponent<ApplicationSettings> {
  @Nullable public String oAuthToken = null;
  @Nullable public String oAuthSecret = null;

  @NotNull
  public ApplicationSettings getState() {
    return this;
  }

  public void loadState(@NotNull ApplicationSettings state) {
    XmlSerializerUtil.copyBean(state, this);
  }
}
