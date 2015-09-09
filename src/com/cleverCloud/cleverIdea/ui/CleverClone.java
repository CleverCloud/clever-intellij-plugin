package com.cleverCloud.cleverIdea.ui;

import com.cleverCloud.cleverIdea.api.json.Application;
import com.intellij.dvcs.DvcsRememberedInputs;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import git4idea.remote.GitRememberedInputs;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public class CleverClone extends DialogWrapper {

  private JTextField myParentDirectory;
  private JTextField myDirectoryName;
  private JComboBox<Application> myRepositoryUrl;
  private JPanel jpanel;

  public CleverClone(@Nullable Project project, List<Application> applications) {
    super(project);
    init();
    setTitle("Clone Clever Cloud Application");
    for (Application application : applications) myRepositoryUrl.addItem(application);
    myRepositoryUrl.addActionListener(evt -> myDirectoryName.setText(((Application)myRepositoryUrl.getSelectedItem()).name));
    if (project != null) {
      myParentDirectory.setText(project.getBaseDir().getParent().getPath());
    }
    else {
      DvcsRememberedInputs gitRememberedInputs = GitRememberedInputs.getInstance();
      myParentDirectory.setText(gitRememberedInputs.getCloneParentDir());
    }
  }

  @Nullable
  @Override
  protected JComponent createCenterPanel() {
    return jpanel;
  }

  public String getRepositoryUrl() {
    return ((Application)myRepositoryUrl.getSelectedItem()).deployment.url;
  }

  public String getParentDirectory() {
    return myParentDirectory.getText();
  }

  public String getDirectoryName() {
    return myDirectoryName.getText();
  }
}
