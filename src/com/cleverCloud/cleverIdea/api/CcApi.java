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

package com.cleverCloud.cleverIdea.api;

import com.cleverCloud.cleverIdea.settings.ApplicationSettings;
import com.cleverCloud.cleverIdea.api.json.Application;
import com.cleverCloud.cleverIdea.api.json.WebSocket;
import com.cleverCloud.cleverIdea.ui.CcApiLogin;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import java.io.IOException;
import java.io.StringWriter;

public class CcApi {
  @Nullable private static CcApi ourInstance = null;
  private Project myProject;
  @Nullable private Token myAccessToken;
  @Nullable private OAuthService myService = null;

  @NotNull
  public static CcApi getInstance(@NotNull Project project) {
    if (ourInstance == null) ourInstance = new CcApi();
    ourInstance.myProject = project;
    return ourInstance;
  }

  public boolean login() {
    @SuppressWarnings("SpellCheckingInspection") final String API_KEY = "JaGomLuixI29k62K9Zf9klIlQbZHdf";
    @SuppressWarnings("SpellCheckingInspection") final String API_SECRET = "KRP5Ckc0CKXRBE0QsmmHX3nVG8n5Mu";
    final String API_CALLBACK = "https://console.clever-cloud.com/cli-oauth";
    final ApplicationSettings applicationSettings = ServiceManager.getService(myProject, ApplicationSettings.class);

    myService = new ServiceBuilder().provider(CleverCloudApi.class).apiKey(API_KEY).apiSecret(API_SECRET).callback(API_CALLBACK).build();

    if (applicationSettings.getOAuthToken() != null && applicationSettings.getOAuthSecret() != null) {
      myAccessToken = new Token(applicationSettings.getOAuthToken(), applicationSettings.getOAuthSecret());
      return true;
    }

    final CcApiLogin login = new CcApiLogin(myProject, API_CALLBACK);

    if (login.showAndGet()) {
      myAccessToken = new Token(login.getToken(), login.getSecret());
      applicationSettings.setOAuthToken(login.getToken());
      applicationSettings.setOAuthSecret(login.getSecret());
      return true;
    }

    loginErrorNotification();
    return false;
  }

  private void loginErrorNotification() {
    Notification notification =
      new Notification("Error Report", "Clever IDEA - Login error", "Error while login in Clever Cloud.", NotificationType.ERROR);
    notification.notify(myProject);
  }

  private boolean isValidate() {
    return this.myService != null && this.myAccessToken != null;
  }

  @Nullable
  public String apiRequest(@NotNull String url) {
    if (!isValidate() && !login()) {
      callApiErrorNotification(url);
      return null;
    }

    OAuthRequest request = new OAuthRequest(Verb.GET, CleverCloudApi.BASE_URL + url);
    assert myService != null;
    myService.signRequest(myAccessToken, request);
    return request.send().getBody();
  }

  private void callApiErrorNotification(String url) {
    Notification notification =
      new Notification("Error Report", "Clever IDEA - Request error", "The following request cannot be executed :<br />" + url,
                       NotificationType.ERROR);
    notification.notify(myProject);
  }

  @Nullable
  public String logRequest(@NotNull Application application) {
    String url = CleverCloudApi.LOGS_URL + application.id + "?limit=300&order=asc";
    System.out.println(url);
    OAuthRequest request = new OAuthRequest(Verb.GET, url);

    assert myService != null;
    myService.signRequest(myAccessToken, request);
    Response response = request.send();

    return response.getBody();
  }

  @Nullable
  public String wsLogSigner() {
    if (!isValidate() && !login()) return null;

    OAuthRequest request = new OAuthRequest(Verb.GET, CleverCloudApi.BASE_URL);
    assert myService != null;
    myService.signRequest(myAccessToken, request);
    WebSocket ws = new WebSocket(request.getHeaders().toString().replace("{Authorization=", "").replace("}", ""));
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.INDENT_OUTPUT, false);
    StringWriter writer = new StringWriter();

    try {
      mapper.writeValue(writer, ws);
      return writer.toString();
    }
    catch (IOException e) {
      e.printStackTrace();
    }

    return null;
  }
}
