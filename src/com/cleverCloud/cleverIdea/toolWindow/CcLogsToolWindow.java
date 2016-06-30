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

package com.cleverCloud.cleverIdea.toolWindow;

import com.cleverCloud.cleverIdea.settings.ProjectSettings;
import com.cleverCloud.cleverIdea.api.CcApi;
import com.cleverCloud.cleverIdea.api.CleverCloudApi;
import com.cleverCloud.cleverIdea.api.json.Application;
import com.cleverCloud.cleverIdea.utils.WebSocketCore;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class CcLogsToolWindow {
  public CcLogsToolWindow(@NotNull Project project) {
    ToolWindow toolWindow = ToolWindowManager.getInstance(project).registerToolWindow("Logs Clever Cloud", false, ToolWindowAnchor.BOTTOM);
    ContentManager contentManager = toolWindow.getContentManager();
    ProjectSettings projectSettings = ServiceManager.getService(project, ProjectSettings.class);
    ArrayList<Application> applications = projectSettings.applications;

    for (Application application : applications) {
      TextConsoleBuilder builder = TextConsoleBuilderFactory.getInstance().createBuilder(project);
      ConsoleView console = builder.getConsole();

      Content logs = contentManager.getFactory().createContent(console.getComponent(), application.name, false);
      contentManager.addContent(logs);

      writeLogs(project, application, console);
      String oldLogs = CcApi.getInstance(project).logRequest(application);

      if (oldLogs != null && !oldLogs.isEmpty()) {
        WebSocketCore.printSocket(console, oldLogs);
      }
      else if (oldLogs != null && oldLogs.isEmpty()) {
        WebSocketCore.printSocket(console, "No logs available.\n");
      }
    }
  }

  private void writeLogs(@NotNull Project project, @NotNull Application lastUsedApplication, @NotNull ConsoleView consoleView) {
    TimeZone tz = TimeZone.getTimeZone("UTC");
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:s.S'Z'");
    df.setTimeZone(tz);
    String timestamp = df.format(new Date());

    try {
      URI logUri = new URI(String.format(CleverCloudApi.LOGS_SOKCET_URL, lastUsedApplication.id, timestamp));
      WebSocketCore webSocketCore = new WebSocketCore(logUri, project, consoleView);
      webSocketCore.connect();
    }
    catch (@NotNull URISyntaxException | NoSuchAlgorithmException | KeyManagementException e) {
      e.printStackTrace();
    }
  }
}
