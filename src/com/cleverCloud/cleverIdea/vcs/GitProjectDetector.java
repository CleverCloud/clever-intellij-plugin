package com.cleverCloud.cleverIdea.vcs;

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
  @NotNull private Pattern pattern =
    Pattern.compile("^git\\+ssh://git@push\\.[\\w]{3}\\.clever-cloud\\.com/(app_([a-f0-9]{8}-(?:[a-f0-9]{4}-){3}[a-f0-9]{12}))\\.git$");
  @Nullable private GitRepositoryManager myGitRepositoryManager = null;
  private Project myProject;

  public GitProjectDetector(Project project) {
    myProject = project;
    if (myGitRepositoryManager == null) myGitRepositoryManager = ServiceManager.getService(myProject, GitRepositoryManager.class);
  }

  @NotNull
  public List<Application> detect() {
    List<Application> applicationList = getApplicationList(getAppIdList());
    String remoteStringList = remoteListToString(applicationList);
    String content = remoteStringList == null
                     ? "No Clever Cloud application has been found in your remotes."
                     : String.format(
                       "Clever IDEA has detected the following remotes corresponding to Clever Cloud applications :<br /><ul>%s</ul><br " +
                       "/>You can push on one of this remotes using the \"Push on Clever Cloud\" action (VCS|Clever Cloud...).",
                       remoteStringList);

    new Notification("Plugins Suggestion", "Clever Cloud application detection", content, NotificationType.INFORMATION).notify(myProject);

    return applicationList;
  }

  @NotNull
  public List<String> getAppIdList() {
    List<String> appIdList = new ArrayList<>();
    List<GitRepository> gitRepositoryList;
    gitRepositoryList = myGitRepositoryManager != null ? myGitRepositoryManager.getRepositories() : null;

    if (gitRepositoryList == null) return appIdList;

    for (GitRepository aGitRepositoryList : gitRepositoryList) {
      Collection<GitRemote> gitRemotes = aGitRepositoryList.getRemotes();

      for (GitRemote aGitRemote : gitRemotes) {
        List<String> remoteUrls = aGitRemote.getUrls();

        for (String anUrl : remoteUrls) {
          Matcher matcher = pattern.matcher(anUrl);

          if (matcher.matches()) {
            appIdList.add(matcher.group(1));
          }
        }
      }
    }

    return appIdList;
  }

  @NotNull
  public List<Application> getApplicationList(@NotNull List<String> appIdList) {
    List<Application> applicationList = new ArrayList<>();
    CcApi ccApi = CcApi.getInstance(myProject);

    for (String appId : appIdList) {
      String response = ccApi.callApi(String.format("/self/applications/%s", appId));

      if (response != null) {
        ObjectMapper mapper = new ObjectMapper();

        try {
          Application application = mapper.readValue(response, Application.class);
          System.out.println(application);
          applicationList.add(application);
        }
        catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    return applicationList;
  }

  @Nullable
  private String remoteListToString(@NotNull List<Application> applications) {
    if (applications.isEmpty()) return null;

    String linkList = "";
    for (Application application : applications) {
      // TODO : replace getName with remote name
      linkList = linkList + String.format("<li>%s<br /></li>", application.getName());
    }

    return linkList;
  }

  @Override
  public void repositoryChanged(@NotNull GitRepository repository) {
    detect();
  }

}