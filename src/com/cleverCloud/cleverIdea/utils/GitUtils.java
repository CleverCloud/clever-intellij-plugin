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

package com.cleverCloud.cleverIdea.utils;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import git4idea.config.GitVcsApplicationSettings;
import git4idea.config.GitVersion;

public class GitUtils {
  public static boolean testGitExecutable(Project project) {
    GitVcsApplicationSettings settings = GitVcsApplicationSettings.getInstance();
    String executable = settings.getPathToGit();

    GitVersion version;
    try {
      version = GitVersion.identifyVersion(executable);
    }
    catch (Exception var5) {
      new Notification("Vcs Important Messages", "Git executable can't be found",
                       "The git executable ca not be found. Please check your settings.", NotificationType.INFORMATION).notify(project);
      return false;
    }

    if (!version.isSupported()) {
      new Notification("Vcs Important Messages", "Git version unsupported",
                       "Your version of git is not supported.", NotificationType.INFORMATION).notify(project);
      return false;
    }
    else {
      return true;
    }
  }
}
