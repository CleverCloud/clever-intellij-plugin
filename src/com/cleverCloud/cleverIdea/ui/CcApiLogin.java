/*
 * The MIT License (MIT)
 *
 * Copyright (c) $YEAR Clever Cloud, SAS
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

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.HyperlinkLabel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class CcApiLogin extends DialogWrapper {
  private JPanel panel;
  private JTextField tokenTextField1;
  private JTextField secretTextField1;
  private HyperlinkLabel label;

  public CcApiLogin(@Nullable Project project, @NotNull String address) {
    super(project);
    openBrowser(address);
    label.setHtmlText(String.format(
      "Copy and past the Token and the Secret shown in your browser : (If the page does not open, <a href=\"%s\">click here</a>)",
      address));
    label.addHyperlinkListener(e -> openBrowser(address));
  }

  private void openBrowser(@NotNull String address) {
    Desktop desktop = Desktop.getDesktop();
    try {
      if (desktop.isSupported(Desktop.Action.BROWSE)) {
        URI uri = new URI(address);
        desktop.browse(uri);
      }
      else {
        System.out.println("Browse action isn't supported");
      }
      init();
      setTitle("Login");
    }
    catch (@NotNull URISyntaxException | IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected JComponent createCenterPanel() {
    return panel;
  }

  public String getToken() {
    return tokenTextField1.getText();
  }

  public String getSecret() {
    return secretTextField1.getText();
  }
}
