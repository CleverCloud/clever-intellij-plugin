package com.cleverCloud.cleverIdea.action;

import com.cleverCloud.cleverIdea.Settings;
import com.cleverCloud.cleverIdea.api.json.Application;
import com.cleverCloud.cleverIdea.vcs.GitProjectDetector;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

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
    }
  }
}
