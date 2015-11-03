/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Clever Cloud, SAS
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.cleverCloud.cleverIdea.vcs;

import com.cleverCloud.cleverIdea.api.CcApi;
import com.cleverCloud.cleverIdea.api.json.Application;
import com.cleverCloud.cleverIdea.api.json.Organisation;
import com.cleverCloud.cleverIdea.ui.CleverClone;
import com.cleverCloud.cleverIdea.utils.GitUtils;
import com.cleverCloud.cleverIdea.utils.JacksonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import git4idea.GitUtil;
import git4idea.actions.BasicAction;
import git4idea.checkout.GitCheckoutProvider;
import git4idea.commands.Git;
import git4idea.config.GitVersion;
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
    if (GitUtils.testGitExecutable(project)) return;
    BasicAction.saveAll();

    List<Application> applicationList;
    CcApi ccApi = CcApi.getInstance(project);
    ccApi.login();

    applicationList = getApplicationsFromOrga(project);

    if (applicationList == null || applicationList.size() == 0) {
      Messages.showMessageDialog("No application is currently linked with you application. Impossible to clone and create a project.",
                                 "No Application Available", Messages.getErrorIcon());
      return;
    }

    final CleverClone dialog = new CleverClone(project, applicationList);
    if (!dialog.showAndGet()) return;

    final VirtualFile destinationParent = LocalFileSystem.getInstance().findFileByIoFile(new File(dialog.getParentDirectory()));
    if (destinationParent == null) return;

    final String sourceRepositoryURL = dialog.getRepositoryUrl();
    final String directoryName = dialog.getDirectoryName();
    final String parentDirectory = dialog.getParentDirectory();

    Git git = ServiceManager.getService(Git.class);
    GitCheckoutProvider.clone(project, git, listener, destinationParent, sourceRepositoryURL, directoryName, parentDirectory);
  }

  private List<Application> getApplicationsFromOrga(@NotNull Project project) {
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
    catch (@NotNull IOException | NullPointerException e) {
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
    catch (@NotNull IOException | NullPointerException e) {
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
