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

package com.cleverCloud.cleverIdea.action;

import com.cleverCloud.cleverIdea.settings.ProjectSettings;
import com.cleverCloud.cleverIdea.vcs.GitProjectDetector;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
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
      return;
    }
    else {
      e.getPresentation().setEnabledAndVisible(true);
    }

    ProjectSettings projectSettings = ServiceManager.getService(e.getProject(), ProjectSettings.class);
    if (!projectSettings.applications.isEmpty()) {
      e.getPresentation().setText("Update associate applications");
    }
  }
}
