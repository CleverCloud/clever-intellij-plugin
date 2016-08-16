/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Clever Cloud, SAS
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
import com.cleverCloud.cleverIdea.api.json.Organisation
import com.cleverCloud.cleverIdea.ui.CleverClone
import com.cleverCloud.cleverIdea.utils.GitUtils
import com.cleverCloud.cleverIdea.utils.JacksonUtils
import com.fasterxml.jackson.databind.ObjectMapper
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vcs.CheckoutProvider
import com.intellij.openapi.vfs.LocalFileSystem
import git4idea.actions.BasicAction
import git4idea.checkout.GitCheckoutProvider
import git4idea.commands.Git
import java.io.File
import java.io.IOException
import java.util.*

class CleverCheckoutProvider(git: Git) : GitCheckoutProvider(git) {
    override fun doCheckout(project: Project, listener: CheckoutProvider.Listener?) {
        if (!GitUtils.testGitExecutable(project)) return
        BasicAction.saveAll()

        val applicationList: List<Application>?
        val ccApi = CcApi.getInstance(project)
        ccApi.login()

        applicationList = getApplicationsFromOrga(project)

        if (applicationList == null || applicationList.size == 0) {
            Messages.showMessageDialog("No application is currently linked with you application. Impossible to clone and create a project.",
                    "No Application Available", Messages.getErrorIcon())
            return
        }

        val dialog = CleverClone(project, applicationList)
        if (!dialog.showAndGet()) return

        val destinationParent = LocalFileSystem.getInstance().findFileByIoFile(File(dialog.parentDirectory)) ?: return

        val sourceRepositoryURL = dialog.repositoryUrl
        val directoryName = dialog.directoryName
        val parentDirectory = dialog.parentDirectory

        val git = ServiceManager.getService(Git::class.java)
        GitCheckoutProvider.clone(project, git, listener, destinationParent, sourceRepositoryURL, directoryName, parentDirectory)
    }

    private fun getApplicationsFromOrga(project: Project): List<Application>? {
        val ccApi = CcApi.getInstance(project)
        var json = ccApi.apiRequest("/self/applications")!!
        val applicationsList = ArrayList<Application>()
        var applications = getApplications(json)
        if (applications != null) Collections.addAll(applicationsList, *applications)

        json = ccApi.apiRequest("/self")!!
        val hashMap: HashMap<*, *>
        try {
            hashMap = JacksonUtils.jsonToMap(json)
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }

        val userId = hashMap["id"] as String
        json = ccApi.apiRequest("/organisations?user=" + userId)!!
        val mapper = ObjectMapper()
        val organisations: Array<Organisation>
        try {
            organisations = mapper.readValue(json, Array<Organisation>::class.java)
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        } catch (e: NullPointerException) {
            e.printStackTrace()
            return null
        }

        for (organisation in organisations) {
            applications = organisation.getChildren(project)
            if (applications != null) Collections.addAll(applicationsList, *applications)
        }
        return applicationsList
    }

    private fun getApplications(json: String): Array<Application>? {
        return ObjectMapper().readValue(json, Array<Application>::class.java)
    }

    override fun getVcsName(): String {
        return "Git _Clever Cloud"
    }
}
