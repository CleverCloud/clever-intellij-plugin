package com.cleverCloud.cleverIdea.api;

import com.cleverCloud.cleverIdea.ApplicationSettings;
import com.cleverCloud.cleverIdea.ProjectSettings;
import com.cleverCloud.cleverIdea.api.json.Application;
import com.cleverCloud.cleverIdea.api.json.WebSocket;
import com.cleverCloud.cleverIdea.ui.CcApiLogin;
import com.cleverCloud.cleverIdea.ui.SelectApplication;
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
  private Token myAccessToken;
  @Nullable private OAuthService myService = null;

  @NotNull
  public static CcApi getInstance(@NotNull Project project) {
    if (ourInstance == null) ourInstance = new CcApi();
    ourInstance.myProject = project;
    return ourInstance;
  }

  private boolean login() {
    @SuppressWarnings("SpellCheckingInspection") final String API_KEY = "JaGomLuixI29k62K9Zf9klIlQbZHdf";
    @SuppressWarnings("SpellCheckingInspection") final String API_SECRET = "KRP5Ckc0CKXRBE0QsmmHX3nVG8n5Mu";
    final String API_CALLBACK = "https://console.clever-cloud.com/cli-oauth";
    final ApplicationSettings applicationSettings = ServiceManager.getService(myProject, ApplicationSettings.class);

    myService = new ServiceBuilder().provider(CleverCloudApi.class).apiKey(API_KEY).apiSecret(API_SECRET).callback(API_CALLBACK).build();

    if (applicationSettings.oAuthToken != null && applicationSettings.oAuthSecret != null) {
      myAccessToken = new Token(applicationSettings.oAuthToken, applicationSettings.oAuthSecret);
      return true;
    }

    final CcApiLogin login = new CcApiLogin(myProject, API_CALLBACK);

    if (login.showAndGet()) {
      myAccessToken = new Token(login.getToken(), login.getSecret());
      applicationSettings.oAuthToken = login.getToken();
      applicationSettings.oAuthSecret = login.getSecret();
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
    if (!isValidate()) {
      if (!login()) {
        callApiErrorNotification(url);
        return null;
      }
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
  public String logRequest() {
    ProjectSettings projectSettings = ServiceManager.getService(this.myProject, ProjectSettings.class);
    Application application = projectSettings.lastUsedApplication;
    if (application == null) {
      SelectApplication selectApplication = new SelectApplication(myProject, projectSettings.applications, null);
      if (selectApplication.showAndGet()) application = selectApplication.getSelectedItem();
    }

    assert application != null;
    OAuthRequest request =
      new OAuthRequest(Verb.GET, CleverCloudApi.LOGS_URL + application.id + "?limit=" + Integer.toString(CleverCloudApi.LOG_LIMIT));

    assert myService != null;
    myService.signRequest(myAccessToken, request);
    Response response = request.send();

    return response.getBody();
  }

  @Nullable
  public String wsLogSigner() {
    if (!isValidate()) {
      if (!login()) {
        return null;
      }
    }

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
