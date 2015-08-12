package com.cleverCloud.cleverIdea.action;

import com.cleverCloud.cleverIdea.vcs.GitProjectDetector;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import git4idea.GitVcs;
import org.jetbrains.annotations.NotNull;

public class AssociateProjectAction extends AnAction {

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    if (e.getProject() == null) return;
    GitProjectDetector gitProjectDetector = new GitProjectDetector(e.getProject());
    gitProjectDetector.detect();
  }

  @Override
  public void update(@NotNull AnActionEvent e) {
    if (e.getProject() == null) {
      e.getPresentation().setEnabledAndVisible(false);
      return;
    }

    ProjectLevelVcsManager projectLevelVcsManager = ProjectLevelVcsManager.getInstance(e.getProject());

    if (!projectLevelVcsManager.checkVcsIsActive(GitVcs.NAME)) {
      e.getPresentation().setEnabled(false);
    }
    else {
      e.getPresentation().setEnabledAndVisible(true);
    }
  }
}
