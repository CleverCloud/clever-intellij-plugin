package com.cleverCloud.cleverIdea.action;

import com.cleverCloud.cleverIdea.Settings;
import com.cleverCloud.cleverIdea.api.json.Application;
import com.cleverCloud.cleverIdea.vcs.GitProjectDetector;
import com.intellij.dvcs.push.PushSpec;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import git4idea.push.GitPushSupport;
import git4idea.push.GitPushTarget;
import git4idea.repo.GitRepository;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

public class DeployAction extends AnAction {
  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    if (e.getProject() == null) return;

    Settings settings = ServiceManager.getService(e.getProject(), Settings.class);
    ArrayList<Application> apps = settings.apps;
    if (apps.isEmpty()) {
      GitProjectDetector gitProjectDetector = new GitProjectDetector(e.getProject());
      apps = gitProjectDetector.detect();
    }
    DeployDialog dialog = new DeployDialog(e.getProject(), apps);
    if (dialog.showAndGet()) {
      /*GitPushSupport pushSupport = ServiceManager.getService(e.getProject(), GitPushSupport.class);
      // this simply creates the GitPushSource wrapper around the current branch or current revision in case of the detached HEAD
      GitPushSource source = pushSupport.getSource(repository);

      //GitPushTarget target = // create target either directly, or by using some methods from GitPushSupport. Just check them, most
      // probably you'll find what's needed.
      Map<GitRepository, PushSpec<GitPushSource, GitPushTarget> pushSpecs = Collections.singletonMap(repository, new PushSpec(source, target));

      pushSupport.getPusher().push(specs, null, false);*/
    }
  }
}
