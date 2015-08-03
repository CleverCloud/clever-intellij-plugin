package com.cleverCloud.cleverIdea.vcs;

import com.cleverCloud.cleverIdea.Settings;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import git4idea.repo.GitRemote;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import org.apache.batik.dom.util.HashTable;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProjectDetector {
  private Pattern pattern =
    Pattern.compile("^git\\+ssh://git@push\\.[\\w]{3}\\.clever-cloud\\.com/(app_([a-f0-9]{8}-(?:[a-f0-9]{4}-){3}[a-f0-9]{12}))\\.git$");

  public void detect(Project project) {
    List<HashTable> cleverRemoteList = getCleverRemoteList(project);
    notifyNewRemotes(project, getRemoteStringList(cleverRemoteList));
  }

  private void notifyNewRemotes(Project project, String remoteStringList) {
    NotificationListener notificationListener = (notification, event) -> {
      Settings settings = Settings.getInstance(project);
      settings.appId = event.getDescription();
      notification.expire();
    };

    Notification notification = new Notification("Plugins Suggestion", "CleverèCloud app detected", String.format(
      "Clever IDEA has detected a remote corresponding to a Clever-Cloud. If you want to add " +
      "support for this application, please select the desired application :<br />%s", remoteStringList), NotificationType.INFORMATION,
                                                 notificationListener);

    notification.notify(project);
  }

  @Nullable
  private List<GitRepository> getRepositoryList(Project project) {
    GitRepositoryManager repositoryManager = ServiceManager.getService(project, GitRepositoryManager.class);
    return repositoryManager != null ? repositoryManager.getRepositories() : null;
  }

  private List<HashTable> getCleverRemoteList(Project project) {
    List<GitRepository> gitRepositoryList = getRepositoryList(project);
    List<HashTable> cleverRemoteList = new ArrayList<>();

    if (gitRepositoryList != null) {
      for (GitRepository aGitRepositoryList : gitRepositoryList) {
        Collection<GitRemote> gitRemotes = aGitRepositoryList.getRemotes();

        for (GitRemote aGitRemote : gitRemotes) {
          List<String> remoteUrls = aGitRemote.getUrls();

          for (String anUrl : remoteUrls) {
            Matcher matcher = pattern.matcher(anUrl);

            if (matcher.matches()) {
              HashTable hashTable = new HashTable();
              hashTable.put("remoteName", aGitRemote.getName());
              hashTable.put("url", anUrl);
              cleverRemoteList.add(hashTable);
            }
          }
        }
      }
    }
    return cleverRemoteList;
  }

  private String getRemoteStringList(List<HashTable> remoteUrls) {
    String linkList = "";

    for (HashTable aRemoteUrl : remoteUrls) {
      Matcher matcher = pattern.matcher((String)aRemoteUrl.get("url"));
      if (matcher.matches() && matcher.groupCount() > 1) {
        String appId = matcher.group(1);
        linkList = linkList + String.format("%s : <a href=\"%s\">%s</a><br />", aRemoteUrl.get("remoteName"), appId, appId);
      }
    }

    return linkList;
  }
}
