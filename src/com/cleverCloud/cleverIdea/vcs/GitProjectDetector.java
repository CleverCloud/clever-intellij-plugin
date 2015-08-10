package com.cleverCloud.cleverIdea.vcs;

import com.cleverCloud.cleverIdea.Settings;
import com.cleverCloud.cleverIdea.api.CcApi;
import com.cleverCloud.cleverIdea.api.json.Application;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import git4idea.repo.GitRemote;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryChangeListener;
import git4idea.repo.GitRepositoryManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GitProjectDetector implements GitRepositoryChangeListener {
  private Pattern pattern =
    Pattern.compile("^git\\+ssh://git@push\\.[\\w]{3}\\.clever-cloud\\.com/(app_([a-f0-9]{8}-(?:[a-f0-9]{4}-){3}[a-f0-9]{12}))\\.git$");
  private GitRepositoryManager myGitRepositoryManager = null;

  public List<Application> detect(@NotNull Project project) {
    if (myGitRepositoryManager == null) myGitRepositoryManager = ServiceManager.getService(project, GitRepositoryManager.class);

    List<Application> cleverRemoteList = getCleverRemoteList(project);

    if (!cleverRemoteList.isEmpty()) {
      Settings settings = ServiceManager.getService(project, Settings.class);
      settings.apps = cleverRemoteList;
    }

    String remoteStringList = remoteListToString(cleverRemoteList);
    notifyNewRemotes(project, remoteStringList);

    return cleverRemoteList;
  }

  @NotNull
  private List<Application> getCleverRemoteList(Project project) {
    List<GitRepository> gitRepositoryList;
    List<Application> cleverRemoteList = new ArrayList<>();
    gitRepositoryList = myGitRepositoryManager != null ? myGitRepositoryManager.getRepositories() : null;
    CcApi ccApi = CcApi.getInstance(project);

    if (gitRepositoryList == null) return cleverRemoteList;

    for (GitRepository aGitRepositoryList : gitRepositoryList) {
      Collection<GitRemote> gitRemotes = aGitRepositoryList.getRemotes();

      for (GitRemote aGitRemote : gitRemotes) {
        List<String> remoteUrls = aGitRemote.getUrls();

        for (String anUrl : remoteUrls) {
          Matcher matcher = pattern.matcher(anUrl);

          if (matcher.matches()) {
            String response = ccApi.callApi(String.format("/self/applications/%s", matcher.group(1)));

            if (response != null) {
              ObjectMapper mapper = new ObjectMapper();

              try {
                Application application = mapper.readValue(response, Application.class);
                System.out.println(application);
                cleverRemoteList.add(application);
              }
              catch (IOException e) {
                e.printStackTrace();
              }
            }
          }
        }
      }
    }
    return cleverRemoteList;
  }

  @Nullable
  private String remoteListToString(List<Application> applications) {
    if (applications.isEmpty()) return null;

    String linkList = "";
    for (Application application : applications) {
      // TODO : replace getName with remote name
      linkList = linkList + String.format("%s : %s<br />", application.getName(), application.getId());
    }

    return linkList;
  }

  private void notifyNewRemotes(Project project, String remoteStringList) {
    String content;
    if (remoteStringList == null) {
      content = "No Clever Cloud application has been found in your remotes.";
    }
    else {
      content = String.format(
        "Clever IDEA has detected the following remotes corresponding to Clever Cloud applications :<br />%s<br />You can push on one of " +
        "this remotes using the \"Push on Clever Cloud\" action (VCS|Clever Cloud...).", remoteStringList);
    }

    Notification notification =
      new Notification("Plugins Suggestion", "Clever Cloud application detection", content, NotificationType.INFORMATION);

    notification.notify(project);
  }

  @Override
  public void repositoryChanged(@NotNull GitRepository repository) {
    detect(repository.getProject());
  }

}
