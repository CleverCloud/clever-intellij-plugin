package com.cleverCloud.cleverIdea.action;

import com.cleverCloud.cleverIdea.api.json.Application;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public class DeployDialog extends DialogWrapper {
  private JPanel contentPane;
  private JComboBox<String> myComboBox;
  private List<Application> myApplications;

  public DeployDialog(Project project, @NotNull List<Application> apps) {
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

  private void setComboBoxContent(@NotNull List<Application> applications) {
    myApplications = applications;

    if (!applications.isEmpty()) {
      for (Application app : applications) {
        myComboBox.addItem(app.getName());
      }
    }
  }

  @Nullable
  @Override
  public JComponent getPreferredFocusedComponent() {
    return myComboBox;
  }

  public Application getApplicationSelected() {
    int selectedIndex = myComboBox.getSelectedIndex();
    Application application = myApplications.get(selectedIndex);

    return application.getName().equals(myComboBox.getSelectedItem()) ? application : null;
  }
}
