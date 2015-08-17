package com.cleverCloud.cleverIdea.toolWindow;

import com.cleverCloud.cleverIdea.Settings;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import org.jetbrains.annotations.NotNull;

public class CcLogsToolWindowFactory implements ToolWindowFactory, Condition<Project> {

  @Override
  public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

  }

  @Override
  public boolean value(@NotNull Project project) {
    Settings settings = ServiceManager.getService(project, Settings.class);
    return !settings.applications.isEmpty();
  }
}