package com.cleverCloud.cleverIdea.action;

import com.cleverCloud.cleverIdea.api.json.Application;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public class DeployDialog extends DialogWrapper {
  private JPanel contentPane;
  private JComboBox<String> myComboBox;

  public DeployDialog(Project project, List<Application> apps) {
    super(project);
    init();
    setTitle("Choose Application to Deploy");
    setComboBoxContent(apps);
  }

  @Nullable
  @Override
  protected JComponent createCenterPanel() {
    return contentPane;
  }

  private void setComboBoxContent(List<Application> apps) {
    if (!apps.isEmpty()) {
      for (Application app : apps) {
        myComboBox.addItem(app.getId());
      }
    }
  }

  @Nullable
  @Override
  public JComponent getPreferredFocusedComponent() {
    return myComboBox;
  }
}
