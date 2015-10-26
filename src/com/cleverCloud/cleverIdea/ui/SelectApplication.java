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
