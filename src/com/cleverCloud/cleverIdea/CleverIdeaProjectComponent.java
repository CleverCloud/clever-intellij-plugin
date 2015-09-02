package com.cleverCloud.cleverIdea;

import com.cleverCloud.cleverIdea.vcs.GitProjectDetector;
import com.intellij.notification.Notification;
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
    Settings settings = ServiceManager.getService(myProject, Settings.class);
    try {
      if (settings.applications.isEmpty()) {
        detectCleverApp();
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
            Settings settings = ServiceManager.getService(myProject, Settings.class);

            new Notification("Vcs Important Messages", "Clever Cloud application detection", String.format(
              "The Clever IDEA plugin has detected that you have %d remotes pointing to Clever Cloud. " +
              "<a href=\"\">Click here</a> to enable integration.", appList.size()), NotificationType.INFORMATION,
                             (notification, event) -> {
                               settings.applications = gitProjectDetector.getApplicationList(appList);
                               notification.expire();

                               new Notification("Vcs Minor Notifications", "Applications successfully linked", String
                                 .format("The following Clever Cloud application have been linked successfully :<br />%s",
                                         gitProjectDetector.remoteListToString(settings.applications)), NotificationType.INFORMATION)
                                 .notify(myProject);
                             }).notify(myProject);
          }
        }
      }
    });
  }
}
