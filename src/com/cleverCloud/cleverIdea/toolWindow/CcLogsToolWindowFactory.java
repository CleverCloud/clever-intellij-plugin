package com.cleverCloud.cleverIdea.toolWindow;

import com.cleverCloud.cleverIdea.SelectApplication;
import com.cleverCloud.cleverIdea.Settings;
import com.cleverCloud.cleverIdea.api.CcApi;
import com.cleverCloud.cleverIdea.api.CleverCloudApi;
import com.cleverCloud.cleverIdea.api.json.Application;
import com.cleverCloud.cleverIdea.utils.WebSocketCore;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class CcLogsToolWindowFactory implements ToolWindowFactory, Condition<Project> {

  @Override
  public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
    ContentManager contentManager = toolWindow.getContentManager();
    CcLogForm ccLogForm = new CcLogForm();
    Content logs = contentManager.getFactory().createContent(ccLogForm.getEditor(), "Logs", false);
    contentManager.addContent(logs);

    Settings settings = ServiceManager.getService(project, Settings.class);
    Application lastUsedApplication = settings.lastUsedApplication;
    if (lastUsedApplication == null) {
      SelectApplication selectApplication = new SelectApplication(project, settings.applications, null);
      if (selectApplication.showAndGet()) {
        lastUsedApplication = selectApplication.getSelectedItem();
        settings.lastUsedApplication = lastUsedApplication;
      }
    }

    // TODO : add button to choose application
    if (lastUsedApplication != null) {
      Editor editor = ccLogForm.getEditor().getEditor();
      assert editor != null;
      writeLogs(project, lastUsedApplication, editor);
      String oldLogs = CcApi.getInstance(project).logRequest();
      if (oldLogs != null) {
        String[] logsLines = oldLogs.split("\n");
        for (int i = logsLines.length - 1; i >= 0; --i) {
          WebSocketCore.printSocket(editor, logsLines[i] + "\n");
        }
      }
    }
  }

  private void writeLogs(@NotNull Project project, @NotNull Application lastUsedApplication, @NotNull Editor editor) {
    TimeZone tz = TimeZone.getTimeZone("UTC");
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:s.S'Z'");
    df.setTimeZone(tz);
    String timestamp = df.format(new Date());

    try {
      URI logUri = new URI(String.format(CleverCloudApi.LOGS_SOKCET_URL, lastUsedApplication.id, timestamp));
      WebSocketCore webSocketCore = new WebSocketCore(logUri, project, editor);
      webSocketCore.connectBlocking();
    }
    catch (URISyntaxException | NoSuchAlgorithmException | KeyManagementException | InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Override
  public boolean value(@NotNull Project project) {
    Settings settings = ServiceManager.getService(project, Settings.class);
    return !settings.applications.isEmpty();
  }
}
