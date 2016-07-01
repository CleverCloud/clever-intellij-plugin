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

package com.cleverCloud.cleverIdea.toolWindow

import com.cleverCloud.cleverIdea.settings.ProjectSettings
import com.cleverCloud.cleverIdea.api.CcApi
import com.cleverCloud.cleverIdea.api.CleverCloudApi
import com.cleverCloud.cleverIdea.api.json.Application
import com.cleverCloud.cleverIdea.utils.WebSocketCore
import com.intellij.execution.filters.TextConsoleBuilder
import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowAnchor
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentManager

import java.net.URI
import java.net.URISyntaxException
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.TimeZone

/**
 * Tool window displaying logs of linked applications.
 *
 * @param project <code>project</code> which will contain the tool window.
 *
 * TODO : make tabs closable (Need Disposable + change Toolwindow registration)
 */
class CcLogsToolWindow(private val project: Project) {

    init {
        val toolWindow = ToolWindowManager.getInstance(project).registerToolWindow("Logs Clever Cloud", false, ToolWindowAnchor.BOTTOM, true)
        val contentManager = toolWindow.contentManager
        val projectSettings = ServiceManager.getService(project, ProjectSettings::class.java)
        val applications = projectSettings.applications

        for (application in applications) {
            addApplication(application, contentManager)
        }
    }

    /**
     * Create log tab for the specified <code>application</code>.
     *
     * @param application <code>application</code> for which the tool window will be created.
     * @param contentManager
     */
    fun addApplication(application: Application, contentManager: ContentManager) {
        val builder = TextConsoleBuilderFactory.getInstance().createBuilder(this.project)
        val console = builder.console

        val logs = contentManager.factory.createContent(console.component, application.name, false)
        contentManager.addContent(logs)

        writeLogs(this.project, application, console)
        val oldLogs = CcApi.getInstance(this.project).logRequest(application)

        if (oldLogs != null && !oldLogs.isEmpty()) {
            WebSocketCore.printSocket(console, oldLogs)
        } else if (oldLogs != null && oldLogs.isEmpty()) {
            WebSocketCore.printSocket(console, "No logs available.\n")
        }
    }

    private fun writeLogs(project: Project, lastUsedApplication: Application, consoleView: ConsoleView) {
        val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:s.S'Z'")
        df.timeZone = TimeZone.getTimeZone("UTC")
        val timestamp = df.format(Date())

        try {
            val logUri = URI(String.format(CleverCloudApi.LOGS_SOKCET_URL, lastUsedApplication.id, timestamp))
            val webSocketCore = WebSocketCore(logUri, project, consoleView)
            webSocketCore.connect()
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: KeyManagementException) {
            e.printStackTrace()
        }
    }
}
