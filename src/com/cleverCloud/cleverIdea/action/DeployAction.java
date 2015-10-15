/*
 * The MIT License (MIT)
 *
 * Copyright (c) $YEAR Clever Cloud, SAS
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

package com.cleverCloud.cleverIdea.action;

import com.cleverCloud.cleverIdea.ProjectSettings;
import com.cleverCloud.cleverIdea.api.json.Application;
import com.cleverCloud.cleverIdea.ui.SelectApplication;
import com.intellij.dvcs.DvcsUtil;
import com.intellij.dvcs.push.PushSpec;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.AbstractVcs;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import git4idea.GitBranch;
import git4idea.GitRemoteBranch;
import git4idea.GitStandardRemoteBranch;
import git4idea.push.GitPushSource;
import git4idea.push.GitPushSupport;
import git4idea.push.GitPushTarget;
import git4idea.repo.GitRemote;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class DeployAction extends AnAction {
  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    if (e.getProject() == null) return;

    ProjectSettings projectSettings = ServiceManager.getService(e.getProject(), ProjectSettings.class);
    ArrayList<Application> applications = projectSettings.applications;

    if (applications.isEmpty()) {
      new Notification("Vcs Important Messages", "No application available",
                       "No Clever Cloud application is associated with the project. Run \"VCS | Clever Cloud ... | Associate with project\" to ssociate an application with the project.",
                       NotificationType.ERROR).notify(e.getProject());
      return;
    }

    Application lastApplication = projectSettings.lastUsedApplication;
    if (!applications.contains(lastApplication)) lastApplication = projectSettings.lastUsedApplication = null;
    SelectApplication dialog = new SelectApplication(e.getProject(), applications, lastApplication);

    if (dialog.showAndGet()) {
      ProgressManager.getInstance().run(new Task.Backgroundable(e.getProject(), "Push on Clever Cloud", true) {
        @Override
        public void run(@NotNull ProgressIndicator indicator) {
          pushOnClever(dialog, e.getProject());
        }
      });
      projectSettings.lastUsedApplication = dialog.getSelectedItem();
    }
  }

  private void pushOnClever(@NotNull SelectApplication dialog, @NotNull Project project) {
    Application application = dialog.getSelectedItem();
    VirtualFile gitRoot = LocalFileSystem.getInstance().findFileByIoFile(new File(application.deployment.repository));
    assert gitRoot != null;
    GitRepositoryManager repositoryManager = ServiceManager.getService(project, GitRepositoryManager.class);
    GitRepository repository = repositoryManager.getRepositoryForRoot(gitRoot);
    if (repository == null) return;

    GitRemote remote = getRemote(repository.getRemotes(), application.deployment.url);
    if (remote == null) return;
    GitRemoteBranch branch = getBranch(repository, remote);

    ProjectLevelVcsManager projectLevelVcsManager = ProjectLevelVcsManager.getInstance(project);
    AbstractVcs abstractVcs = projectLevelVcsManager.getVcsFor(gitRoot);
    assert abstractVcs != null;
    GitPushSupport pushSupport = (GitPushSupport)DvcsUtil.getPushSupport(abstractVcs);
    assert pushSupport != null;
    GitPushSource source = pushSupport.getSource(repository);
    GitPushTarget target = new GitPushTarget(branch, false);

    PushSpec<GitPushSource, GitPushTarget> pushSourceGitPushTargetPushSpec = new PushSpec<>(source, target);
    Map<GitRepository, PushSpec<GitPushSource, GitPushTarget>> pushSpecs =
      Collections.singletonMap(repository, pushSourceGitPushTargetPushSpec);

    pushSupport.getPusher().push(pushSpecs, null, false);
  }

  private GitRemote getRemote(@NotNull Collection<GitRemote> gitRemoteCollections, String remoteUrl) {
    for (GitRemote gitRemote : gitRemoteCollections) {
      int remoteIndex = gitRemote.getUrls().indexOf(remoteUrl);
      if (remoteIndex != -1) {
        return gitRemote;
      }
    }
    return null;
  }

  @NotNull
  private GitRemoteBranch getBranch(@NotNull GitRepository repository, @NotNull GitRemote remote) {
    for (GitRemoteBranch remoteBranch : repository.getBranches().getRemoteBranches()) {
      if (remoteBranch.getRemote().equals(remote) && remoteBranch.getName().equals("master")) return remoteBranch;
    }
    return new GitStandardRemoteBranch(remote, "master", GitBranch.DUMMY_HASH);
  }

  @Override
  public void update(@NotNull AnActionEvent e) {
    if (e.getProject() == null) {
      e.getPresentation().setEnabledAndVisible(false);
      return;
    }

    ProjectSettings projectSettings = ServiceManager.getService(e.getProject(), ProjectSettings.class);
    if (projectSettings.applications.isEmpty()) {
      e.getPresentation().setEnabled(false);
      return;
    }
    e.getPresentation().setEnabledAndVisible(true);
  }
}
