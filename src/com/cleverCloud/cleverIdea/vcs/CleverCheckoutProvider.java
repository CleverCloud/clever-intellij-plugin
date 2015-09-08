package com.cleverCloud.cleverIdea.vcs;

import com.cleverCloud.cleverIdea.api.CcApi;
import com.cleverCloud.cleverIdea.api.json.Application;
import com.cleverCloud.cleverIdea.ui.CleverClone;
import com.cleverCloud.cleverIdea.utils.JacksonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import git4idea.checkout.GitCheckoutProvider;
import git4idea.commands.Git;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

public class CleverCheckoutProvider extends GitCheckoutProvider {
  public CleverCheckoutProvider(@NotNull Git git) {
    super(git);
  }

  @Override
  public void doCheckout(@NotNull Project project, @Nullable Listener listener) {
    CcApi ccApi = CcApi.getInstance(project);
    String json = ccApi.apiRequest("/summary");
    try {
      System.out.println(JacksonUtils.jsonToMap(json));
    }
    catch (IOException e) {
      e.printStackTrace();
      return;
    }

    json = ccApi.apiRequest("/self/applications");
    ObjectMapper mapper = new ObjectMapper();
    Application[] self;

    try {
      self = mapper.readValue(json, Application[].class);
    }
    catch (IOException | NullPointerException e) {
      e.printStackTrace();
      return;
    }

    final CleverClone dialog = new CleverClone(project, self);
    if (!dialog.showAndGet()) return;

    final VirtualFile destinationParent = LocalFileSystem.getInstance().findFileByIoFile(new File(dialog.getParentDirectory()));
    if (destinationParent == null) {
      return;
    }

    final String sourceRepositoryURL = dialog.getRepositoryUrl();
    final String directoryName = dialog.getDirectoryName();
    final String parentDirectory = dialog.getParentDirectory();

    Git git = ServiceManager.getService(Git.class);
    //GitCheckoutProvider.clone(project, git, listener, destinationParent, sourceRepositoryURL, directoryName, parentDirectory);
  }

  @NotNull
  @Override
  public String getVcsName() {
    return "Git _Clever Cloud";
  }
}
