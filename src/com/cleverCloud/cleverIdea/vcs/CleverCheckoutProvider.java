package com.cleverCloud.cleverIdea.vcs;

import com.cleverCloud.cleverIdea.api.CcApi;
import com.cleverCloud.cleverIdea.api.json.Application;
import com.cleverCloud.cleverIdea.api.json.Organisation;
import com.cleverCloud.cleverIdea.ui.CleverClone;
import com.cleverCloud.cleverIdea.utils.JacksonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import git4idea.checkout.GitCheckoutProvider;
import git4idea.commands.Git;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class CleverCheckoutProvider extends GitCheckoutProvider {
  public CleverCheckoutProvider(@NotNull Git git) {
    super(git);
  }

  @Override
  public void doCheckout(@NotNull Project project, @Nullable Listener listener) {
    AtomicReference<List<Application>> applicationList = new AtomicReference<>();
    ProgressManager.getInstance().run(new Task.Modal(project, "Connexion to Clever Cloud", false) {
      @Override
      public void run(@NotNull ProgressIndicator indicator) {
        applicationList.set(getApplicationsFromOrga(project));
      }
    });

    final CleverClone dialog = new CleverClone(project, applicationList.get());
    if (!dialog.showAndGet()) return;

    final VirtualFile destinationParent = LocalFileSystem.getInstance().findFileByIoFile(new File(dialog.getParentDirectory()));
    if (destinationParent == null) {
      return;
    }

    final String sourceRepositoryURL = dialog.getRepositoryUrl();
    final String directoryName = dialog.getDirectoryName();
    final String parentDirectory = dialog.getParentDirectory();

    Git git = ServiceManager.getService(Git.class);
    GitCheckoutProvider.clone(project, git, listener, destinationParent, sourceRepositoryURL, directoryName, parentDirectory);
  }

  private List<Application> getApplicationsFromOrga(Project project) {
    CcApi ccApi = CcApi.getInstance(project);
    String json = ccApi.apiRequest("/self/applications");
    assert json != null;
    List<Application> applicationsList = new ArrayList<>();
    Application[] applications = getApplications(json);
    if (applications != null) Collections.addAll(applicationsList, applications);

    json = ccApi.apiRequest("/self");
    HashMap hashMap;
    try {
      hashMap = JacksonUtils.jsonToMap(json);
    }
    catch (IOException e) {
      e.printStackTrace();
      return null;
    }

    String userId = (String)hashMap.get("id");
    json = ccApi.apiRequest("/organisations?user=" + userId);
    ObjectMapper mapper = new ObjectMapper();
    Organisation[] organisations;
    try {
      organisations = mapper.readValue(json, Organisation[].class);
    }
    catch (IOException | NullPointerException e) {
      e.printStackTrace();
      return null;
    }

    for (Organisation organisation : organisations) {
      applications = organisation.getChildren(project);
      if (applications != null) Collections.addAll(applicationsList, applications);
    }
    return applicationsList;
  }

  @Nullable
  private Application[] getApplications(@NotNull String json) {
    ObjectMapper mapper = new ObjectMapper();

    try {
      return mapper.readValue(json, Application[].class);
    }
    catch (IOException | NullPointerException e) {
      e.printStackTrace();
      return null;
    }
  }

  @NotNull
  @Override
  public String getVcsName() {
    return "Git _Clever Cloud";
  }
}
