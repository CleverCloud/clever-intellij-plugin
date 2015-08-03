package com.cleverCloud.cleverIdea.action;

import com.cleverCloud.cleverIdea.api.CcApi;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;

public class LoginAction extends AnAction {
  public void actionPerformed(AnActionEvent e) {
    Project project = e.getProject();
    CcApi ccApi = CcApi.getInstance();
    ccApi.login(project);
  }

  public void update(AnActionEvent e) {
    CcApi ccApi = CcApi.getInstance();

    if (e.getProject() == null || ccApi.isValidate()) e.getPresentation().setEnabled(false);
  }

}
