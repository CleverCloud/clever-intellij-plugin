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

package com.cleverCloud.cleverIdea;

import com.cleverCloud.cleverIdea.toolWindow.CcLogsToolWindow;
import com.cleverCloud.cleverIdea.utils.ApplicationsUtilities;
import com.cleverCloud.cleverIdea.vcs.GitProjectDetector;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.impl.ProjectLevelVcsManagerImpl;
import com.intellij.openapi.vcs.impl.VcsInitObject;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.xmlb.XmlSerializationException;
import git4idea.GitVcs;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import org.apache.batik.dom.util.HashTable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CleverIdeaProjectComponent implements ProjectComponent {
  private final Project myProject;
  private final ProjectLevelVcsManager myProjectLevelVcsManager;
  private final GitRepositoryManager myGitRepositoryManager;

  public CleverIdeaProjectComponent(Project project,
                                    ProjectLevelVcsManager projectLevelVcsManager,
                                    GitRepositoryManager gitRepositoryManager) {
    myProject = project;
    myProjectLevelVcsManager = projectLevelVcsManager;
    myGitRepositoryManager = gitRepositoryManager;
  }

  @Override
  public void initComponent() {
  }

  @Override
  public void disposeComponent() {
  }

  @Override
  @NotNull
  public String getComponentName() {
    return "CleverIdeaProjectComponent";
  }

  @Override
  public void projectOpened() {
    ProjectSettings projectSettings = ServiceManager.getService(myProject, ProjectSettings.class);
    try {
      if (projectSettings.applications.isEmpty()) {
        detectCleverApp();
      }
      else {
        CcLogsToolWindow ccLogsToolWindow = new CcLogsToolWindow(myProject);
      }
    }
    catch (XmlSerializationException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void projectClosed() {
    // called when project is being closed
  }

  private void detectCleverApp() {
    ProjectLevelVcsManagerImpl vcsManager = (ProjectLevelVcsManagerImpl)myProjectLevelVcsManager;
    vcsManager.addInitializationRequest(VcsInitObject.AFTER_COMMON, () -> {
      GitVcs gitVcs = GitVcs.getInstance(myProject);
      VirtualFile[] gitRoots = new VirtualFile[0];

      if (gitVcs != null) gitRoots = vcsManager.getRootsUnderVcs(gitVcs);

      for (VirtualFile root : gitRoots) {
        GitRepository repo = myGitRepositoryManager.getRepositoryForRoot(root);

        if (repo != null) {
          GitProjectDetector gitProjectDetector = new GitProjectDetector(myProject);
          ArrayList<HashTable> appList = gitProjectDetector.getAppList();

          if (!appList.isEmpty()) {
            ProjectSettings projectSettings = ServiceManager.getService(myProject, ProjectSettings.class);

            new Notification("Vcs Important Messages", "Clever Cloud application detection", String.format(
              "The Clever IDEA plugin has detected that you have %d remotes pointing to Clever Cloud. " +
              "<a href=\"\">Click here</a> to enable integration.", appList.size()), NotificationType.INFORMATION,
                             hyperlinkUpdaterListener(gitProjectDetector, appList, projectSettings)).notify(myProject);
          }
        }
      }
    });
  }

  @NotNull
  private NotificationListener hyperlinkUpdaterListener(final GitProjectDetector gitProjectDetector,
                                                        final ArrayList<HashTable> appList,
                                                        final ProjectSettings projectSettings) {
    return (notification, event) -> {
      projectSettings.applications = gitProjectDetector.getApplicationList(appList);
      notification.expire();

      new Notification("Vcs Minor Notifications", "Applications successfully linked", String
        .format("The following Clever Cloud application have been linked successfully :<br />%s",
                ApplicationsUtilities.remoteListToString(projectSettings.applications)), NotificationType.INFORMATION).notify(myProject);
    };
  }
}
