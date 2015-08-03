package com.cleverCloud.cleverIdea.api;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class CcApiLogin extends DialogWrapper {
  private JPanel panel;
  private JTextField tokenTextField;
  private JLabel label;

  public CcApiLogin(@Nullable Project project, @NotNull String address) {
    super(project);
    Desktop desktop = Desktop.getDesktop();
    try {
      URI uri = new URI(address);
      desktop.browse(uri);
      init();
      setTitle("Login");
    }
    catch (URISyntaxException | IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected JComponent createCenterPanel() {
    return panel;
  }

  public String getVerifier() {
    return tokenTextField.getText();
  }
}
