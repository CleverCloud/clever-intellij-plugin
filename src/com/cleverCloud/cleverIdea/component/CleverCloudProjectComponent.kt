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

package com.cleverCloud.cleverIdea.component

import com.cleverCloud.cleverIdea.settings.ProjectSettings
import com.cleverCloud.cleverIdea.toolWindow.CcLogsToolWindow
import com.cleverCloud.cleverIdea.utils.ApplicationsUtilities
import com.cleverCloud.cleverIdea.vcs.GitProjectDetector
import com.intellij.notification.Notification
import com.intellij.notification.NotificationListener
import com.intellij.notification.NotificationType
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.ProjectLevelVcsManager
import com.intellij.openapi.vcs.impl.ProjectLevelVcsManagerImpl
import com.intellij.openapi.vcs.impl.VcsInitObject
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.xmlb.XmlSerializationException
import git4idea.GitVcs
import git4idea.repo.GitRepository
import git4idea.repo.GitRepositoryManager
import org.apache.batik.dom.util.HashTable

import java.util.ArrayList
import javax.swing.event.HyperlinkEvent

class CleverCloudProjectComponent(private val myProject: Project,
                                  private val myProjectLevelVcsManager: ProjectLevelVcsManager,
                                  private val myGitRepositoryManager: GitRepositoryManager) : ProjectComponent {

    /**
     * @see ProjectComponent.initComponent
     */
    override fun initComponent() {
    }

    /**
     * @see ProjectComponent.disposeComponent
     */
    override fun disposeComponent() {
    }

    /**
     * @see ProjectComponent.getComponentName
     */
    override fun getComponentName(): String {
        return "CleverCloudProjectComponent"
    }

    /**
     * @see ProjectComponent.projectOpened
     *
     * TODO : check if tool windows well available
     */
    override fun projectOpened() {
        if (ServiceManager.getService(myProject, ProjectSettings::class.java).applications.isEmpty())
            detectCleverApp()
    }

    /**
     * @see ProjectComponent.projectClosed
     */
    override fun projectClosed() {
    }

    private fun detectCleverApp() {
        val vcsManager = myProjectLevelVcsManager as ProjectLevelVcsManagerImpl
        vcsManager.addInitializationRequest(VcsInitObject.AFTER_COMMON) {
            val gitVcs = GitVcs.getInstance(myProject)
            val gitRoots = if (gitVcs != null) vcsManager.getRootsUnderVcs(gitVcs) else arrayOfNulls<VirtualFile>(0)

            gitRoots.forEach {
                val repo = myGitRepositoryManager.getRepositoryForRoot(it)

                if (repo != null) {
                    val gitProjectDetector = GitProjectDetector(myProject)
                    val appList = gitProjectDetector.appList

                    if (!appList.isEmpty()) {
                        val projectSettings = ServiceManager.getService(myProject, ProjectSettings::class.java)

                        Notification("Vcs Important Messages", "Clever Cloud application detection", String.format(
                                "The Clever IDEA plugin has detected that you have %d remotes pointing to Clever Cloud. "
                                        + "<a href=\"\">Click here</a> to enable integration.", appList.size), NotificationType.INFORMATION,
                                hyperlinkUpdaterListener(gitProjectDetector, appList, projectSettings)).notify(myProject)
                    }
                } else {} // TODO: add else
            }
        }
    }

    /**
     * TODO : improve listener
     */
    private fun hyperlinkUpdaterListener(gitProjectDetector: GitProjectDetector,
                                         appList: ArrayList<HashTable>,
                                         projectSettings: ProjectSettings): NotificationListener {
        return { notification: Notification, event: HyperlinkEvent ->
            projectSettings.applications = gitProjectDetector.getApplicationList(appList)
            notification.expire()

            Notification("Vcs Minor Notifications", "Applications successfully linked", String.format("The following Clever Cloud application have been linked successfully :<br />%s",
                    ApplicationsUtilities.remoteListToString(projectSettings.applications)), NotificationType.INFORMATION).notify(myProject)
        } as NotificationListener
    }
}
