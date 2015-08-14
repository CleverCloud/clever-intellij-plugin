package git4idea.push;

import com.cleverCloud.cleverIdea.Settings;
import com.cleverCloud.cleverIdea.action.DeployDialog;
import com.cleverCloud.cleverIdea.api.json.Application;
import com.intellij.dvcs.push.PushSource;
import com.intellij.dvcs.push.PushSpec;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import git4idea.repo.GitRepository;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

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
      pushOnClever(dialog, e.getProject());
    }
  }

  /**
   * TODO : Add Notification in case of no Application available
   */
  private void notifyNoApplication() {

  }

  private void pushOnClever(@NotNull DeployDialog dialog, Project project) {
    Application application = dialog.getApplicationSelected();
    GitRepository repository = application.getRepository();

    GitPushSupport pushSupport = ServiceManager.getService(project, GitPushSupport.class);
    GitPushSource source = pushSupport.getSource(repository);
    GitPushTarget target = pushSupport.getDefaultTarget(repository);

    if (target != null) {
      PushSpec<GitPushSource, GitPushTarget> pushSourceGitPushTargetPushSpec = new PushSpec<>(source, target);

      Map<GitRepository, PushSpec<GitPushSource, GitPushTarget>> pushSpecs =
        Collections.singletonMap(repository, pushSourceGitPushTargetPushSpec);

      pushSupport.getPusher().push(pushSpecs, null, false);
    }
  }
}
