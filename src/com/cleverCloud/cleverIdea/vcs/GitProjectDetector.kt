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

package com.cleverCloud.cleverIdea.vcs

import com.cleverCloud.cleverIdea.api.CcApi
import com.cleverCloud.cleverIdea.api.json.Application
import com.cleverCloud.cleverIdea.settings.ProjectSettings
import com.cleverCloud.cleverIdea.utils.ApplicationsUtilities
import com.fasterxml.jackson.databind.ObjectMapper
import com.intellij.dvcs.repo.AbstractRepositoryManager
import com.intellij.dvcs.repo.Repository
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import git4idea.repo.GitRepository
import git4idea.repo.GitRepositoryManager
import org.apache.batik.dom.util.HashTable
import java.io.IOException
import java.util.*
import java.util.regex.Pattern

class GitProjectDetector(private val myProject: Project) {
    /**
     * TODO : move patterns in the CcApi.
     */
    private val applicationPattern = "app_([a-f0-9]{8}-(?:[a-f0-9]{4}-){3}[a-f0-9]{12})"
    private val pattern = Pattern.compile("^git\\+ssh://git@push\\.[\\w]{3}\\.clever-cloud\\.com/($applicationPattern)\\.git$")
    private var myRepositoryManager: AbstractRepositoryManager<GitRepository>

    init {
        myRepositoryManager = ServiceManager.getService(myProject, GitRepositoryManager::class.java)
    }

    fun detect() {
        val applicationList = getApplicationList(appList)
        val remoteStringList = ApplicationsUtilities.remoteListToString(getApplicationList((appList)))
        val content: String
        if (applicationList.isEmpty()) {
            content = "No Clever Cloud application has been found in your remotes.<br />" +
                    "Add a remote with the command :<br /><pre>git remote add clever &lt;URL&gt;</pre>"
        } else {
            content = String.format(
                    "Clever IDEA has detected the following remotes corresponding to Clever Cloud applications :<ul>%s</ul>You can push on one of this "
                            + "remotes using VCS | Clever Cloud... | Push on Clever Cloud.", remoteStringList)
        }

        Notification("Vcs Important Messages", "Clever Cloud application detection", content, NotificationType.INFORMATION).notify(myProject)

        ServiceManager.getService(myProject, ProjectSettings::class.java).applications = applicationList
        //CcLogsToolWindow.openToolWindow(myProject);
    }

    /**
     * Find remote corresponding to a Clever Cloud app in the remotes of the current project.

     * @return List of HashTable containing :  {"AppID" => String, "Repository" => GitRepository}
     */
    val appList: ArrayList<HashTable>
        get() {
            val appIdList = ArrayList<HashTable>()
            val repositoryList = myRepositoryManager.repositories

            repositoryList.forEach {
                val aRepository: Repository = it

                it.remotes.forEach { it.urls.forEach {
                        val matcher = pattern.matcher(it)

                        if (matcher.matches()) {
                            val hashTable = HashTable()
                            hashTable.put(AppInfos.APP_ID, matcher.group(1))
                            hashTable.put(AppInfos.REPOSITORY, aRepository.root.parent.path)
                            appIdList.add(hashTable)
                        }
                    }
                }
            }

            return appIdList
        }

    /**
     * Transform detected app in the project into Application list by getting info in the Clever Cloud API.

     * @param appList List of HashTable containing : {"AppID" => String, "Repository" => GitRepository}
     * *
     * @return ArrayList containing [Application] of the current project
     */
    fun getApplicationList(appList: List<HashTable>): ArrayList<Application> {
        val applicationList = ArrayList<Application>()
        val ccApi = CcApi.getInstance(myProject)

        for (app in appList) {
            val response = ccApi.apiRequest(String.format("/self/applications/%s", app.get(AppInfos.APP_ID)))

            if (response != null) {
                val mapper = ObjectMapper()

                try {
                    val application = mapper.readValue<Application>(response, Application::class.java)
                    application.deployment.repository = app.get(AppInfos.REPOSITORY) as String
                    applicationList.add(application)
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }

        return applicationList
    }

    private enum class AppInfos {
        /**  */
        APP_ID,
        /**  */
        REPOSITORY
    }
}
