package com.cleverCloud.cleverIdea.action;

import com.cleverCloud.cleverIdea.Settings;
import com.cleverCloud.cleverIdea.api.json.Application;
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
    ArrayList<Application> applications = settings.applications;

    if (applications.isEmpty()) {
      notifyNoApplication();
    }

    DeployDialog dialog = new DeployDialog(e.getProject(), applications);

    if (dialog.showAndGet()) {
      pushOnClever(dialog);
    }
  }

  /**
   * TODO : Add Notification in case of no Application available
   */
  private void notifyNoApplication() {

  }

  private void pushOnClever(@NotNull DeployDialog dialog) {
    Application application = dialog.getApplicationSelected();
    /*GitRepository repository = application.getRepository();

    GitPushSupport pushSupport = ServiceManager.getService(e.getProject(), GitPushSupport.class);
    // this simply creates the GitPushSource wrapper around the current branch or current revision in case of the detached HEAD
    PushSource source = pushSupport.getSource(repository);
    GitLocalBranch localBranch = repository.getCurrentBranch();
    // TODO : add selection of branch if current branch is null

    if (localBranch != null) {
      GitPushTarget target = GitPushTarget.getFromPushSpec(repository, localBranch);
      // create target either directly, or by using some methods from GitPushSupport. Just check them, most
      // probably you'll find what's needed.
      Map<GitRepository, PushSpec<PushSource, GitPushTarget>> pushSpecs =
        Collections.singletonMap(repository, new PushSpec(source, target));

      pushSupport.getPusher().push(pushSpecs, null, false);
    }*/
  }
}
