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

package com.cleverCloud.cleverIdea.ui;

import com.cleverCloud.cleverIdea.api.json.Application;
import com.intellij.dvcs.DvcsRememberedInputs;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import git4idea.remote.GitRememberedInputs;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    if (project != null) {
      myParentDirectory.setText(project.getBaseDir().getParent().getPath());
    }
    else {
      DvcsRememberedInputs gitRememberedInputs = GitRememberedInputs.getInstance();
      myParentDirectory.setText(gitRememberedInputs.getCloneParentDir());
    }

    myDirectoryName.setText(((Application)myRepositoryUrl.getSelectedItem()).name);
    myRepositoryUrl.addActionListener(evt -> myDirectoryName.setText(((Application)myRepositoryUrl.getSelectedItem()).name));
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
