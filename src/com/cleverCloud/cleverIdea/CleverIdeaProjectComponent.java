package com.cleverCloud.cleverIdea;

import com.cleverCloud.cleverIdea.api.CcApi;
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
import git4idea.GitVcs;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CleverIdeaProjectComponent implements ProjectComponent {
  private Project myProject;
  private ProjectLevelVcsManager myProjectLevelVcsManager;
  private GitRepositoryManager myGitRepositoryManager;

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
    if (!CcApi.getInstance(myProject).isValidate()) return;

    ProjectLevelVcsManagerImpl vcsManager = (ProjectLevelVcsManagerImpl)myProjectLevelVcsManager;
    vcsManager.addInitializationRequest(VcsInitObject.AFTER_COMMON, () -> {
      GitVcs gitVcs = GitVcs.getInstance(myProject);
      VirtualFile[] gitRoots = new VirtualFile[0];

      if (gitVcs != null) gitRoots = vcsManager.getRootsUnderVcs(gitVcs);

      for (VirtualFile root : gitRoots) {
        GitRepository repo = myGitRepositoryManager.getRepositoryForRoot(root);

        if (repo != null) {
          GitProjectDetector gitProjectDetector = new GitProjectDetector(myProject);
          List<String> appIdList = gitProjectDetector.getAppIdList();

          if (!appIdList.isEmpty()) {
            Settings settings = ServiceManager.getService(myProject, Settings.class);

            new Notification("Plugins Suggestion", "Clever Cloud application detection", String.format(
              "The Clever IDEA plugin has detected that you have %d remotes pointing to Clever Cloud. " +
              "<a href=\"\">Click here</a> to enable integration.", appIdList.size()), NotificationType.INFORMATION,
                             (notification, event) -> {
                               settings.apps = gitProjectDetector.getApplicationList(appIdList);
                               notification.expire();
                             }).notify(myProject);
          }
        }
      }
    });
  }

  @Override
  public void projectClosed() {
    // called when project is being closed
  }
}
