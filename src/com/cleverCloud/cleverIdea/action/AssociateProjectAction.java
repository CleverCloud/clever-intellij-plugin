package com.cleverCloud.cleverIdea.action;

import com.cleverCloud.cleverIdea.vcs.ProjectDetector;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;

public class AssociateProjectAction extends AnAction {

  @Override
  public void actionPerformed(AnActionEvent e) {
    Project project = e.getProject();
    new ProjectDetector().detect(project);
  }

}
