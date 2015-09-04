package com.cleverCloud.cleverIdea.ui;

import com.cleverCloud.cleverIdea.api.json.Application;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public class SelectApplication extends DialogWrapper {
  private JPanel contentPane;
  private JComboBox<Application> myComboBox;

  public SelectApplication(@NotNull Project project, @NotNull List<Application> apps, @Nullable Application lastApplication) {
    super(project);
    init();
    setTitle("Choose Application to Use");
    setComboBoxContent(apps);
    if (lastApplication != null) myComboBox.setSelectedItem(lastApplication);
  }

  @Nullable
  @Override
  protected JComponent createCenterPanel() {
    return contentPane;
  }

  private void setComboBoxContent(@NotNull List<Application> applications) {

    if (!applications.isEmpty()) {
      applications.forEach(myComboBox::addItem);
    }
  }

  @Nullable
  @Override
  public JComponent getPreferredFocusedComponent() {
    return myComboBox;
  }

  @NotNull
  public Application getSelectedItem() {
    return (Application)myComboBox.getSelectedItem();
  }
}
