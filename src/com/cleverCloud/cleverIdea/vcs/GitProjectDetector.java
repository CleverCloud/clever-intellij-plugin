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

package com.cleverCloud.cleverIdea.vcs;

import com.cleverCloud.cleverIdea.ProjectSettings;
import com.cleverCloud.cleverIdea.api.CcApi;
import com.cleverCloud.cleverIdea.api.json.Application;
import com.cleverCloud.cleverIdea.utils.ApplicationsUtilities;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import git4idea.repo.GitRemote;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import org.apache.batik.dom.util.HashTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GitProjectDetector {
  @NotNull private final Pattern pattern =
    Pattern.compile("^git\\+ssh://git@push\\.[\\w]{3}\\.clever-cloud\\.com/(app_([a-f0-9]{8}-(?:[a-f0-9]{4}-){3}[a-f0-9]{12}))\\.git$");
  private final Project myProject;
  @Nullable private GitRepositoryManager myGitRepositoryManager = null;

  public GitProjectDetector(Project project) {
    myProject = project;
    if (myGitRepositoryManager == null) myGitRepositoryManager = ServiceManager.getService(myProject, GitRepositoryManager.class);
  }

  public void detect() {
    ArrayList<Application> applicationList = getApplicationList(getAppList());
    String remoteStringList = ApplicationsUtilities.remoteListToString(applicationList);
    String content;
    if (remoteStringList == null) {
      content = "No Clever Cloud application has been found in your remotes.<br />" +
                "Add a remote with the command :<br /><pre>git remote add clever &lt;URL&gt;</pre>";
    }
    else {
      content = String.format(
        "Clever IDEA has detected the following remotes corresponding to Clever Cloud applications :<ul>%s</ul>You can push on one of this " +
        "remotes using VCS | Clever Cloud... | Push on Clever Cloud.", remoteStringList);
    }

    new Notification("Vcs Important Messages", "Clever Cloud application detection", content, NotificationType.INFORMATION)
      .notify(myProject);

    ServiceManager.getService(myProject, ProjectSettings.class).applications = applicationList;
    //CcLogsToolWindow.openToolWindow(myProject);
  }

  /**
   * Find remote corresponding to a Clever Cloud app in the remotes of the current project.
   *
   * @return List of HashTable containing :  {"AppID" => String, "Repository" => GitRepository}
   */
  @NotNull
  public ArrayList<HashTable> getAppList() {
    ArrayList<HashTable> appIdList = new ArrayList<>();
    List<GitRepository> gitRepositoryList;
    gitRepositoryList = myGitRepositoryManager != null ? myGitRepositoryManager.getRepositories() : null;

    if (gitRepositoryList == null) return appIdList;

    for (GitRepository aGitRepository : gitRepositoryList) {
      Collection<GitRemote> gitRemotes = aGitRepository.getRemotes();

      for (GitRemote aGitRemote : gitRemotes) {
        List<String> remoteUrls = aGitRemote.getUrls();

        for (String anUrl : remoteUrls) {
          Matcher matcher = pattern.matcher(anUrl);

          if (matcher.matches()) {
            HashTable hashTable = new HashTable();
            hashTable.put(AppInfos.APP_ID, matcher.group(1));
            hashTable.put(AppInfos.REPOSITORY, aGitRepository.getGitDir().getParent().getPath());
            appIdList.add(hashTable);
          }
        }
      }
    }

    return appIdList;
  }

  /**
   * Transform detected app in the project into Application list by getting info in the Clever Cloud API.
   *
   * @param appList List of HashTable containing : {"AppID" => String, "Repository" => GitRepository}
   * @return ArrayList containing {@link Application} of the current project
   */
  @NotNull
  public ArrayList<Application> getApplicationList(@NotNull List<HashTable> appList) {
    ArrayList<Application> applicationList = new ArrayList<>();
    CcApi ccApi = CcApi.getInstance(myProject);

    for (HashTable app : appList) {
      String response = ccApi.apiRequest(String.format("/self/applications/%s", app.get(AppInfos.APP_ID)));

      if (response != null) {
        ObjectMapper mapper = new ObjectMapper();

        try {
          Application application = mapper.readValue(response, Application.class);
          application.deployment.repository = (String)app.get(AppInfos.REPOSITORY);
          applicationList.add(application);
        }
        catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    return applicationList;
  }

  private enum AppInfos {
    APP_ID,
    REPOSITORY
  }
}
