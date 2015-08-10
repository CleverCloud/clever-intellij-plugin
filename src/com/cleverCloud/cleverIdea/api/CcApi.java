package com.cleverCloud.cleverIdea.api;

import com.cleverCloud.cleverIdea.Settings;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

public class CcApi {
  private static CcApi ourInstance = null;
  private Project myProject;
  private Token accessToken;
  private OAuthService service;

  public static CcApi getInstance(@NotNull Project project) {
    if (ourInstance == null) {
      ourInstance = new CcApi();
    }

    ourInstance.myProject = project;
    Settings settings = ServiceManager.getService(project, Settings.class);
    if (settings.oAuthToken != null && settings.oAuthSecret != null) {
      ourInstance.accessToken = new Token(settings.oAuthToken, settings.oAuthSecret);
    }
    return ourInstance;
  }

  public boolean login() {
    @SuppressWarnings("SpellCheckingInspection") final String API_KEY = "JaGomLuixI29k62K9Zf9klIlQbZHdf";
    @SuppressWarnings("SpellCheckingInspection") final String API_SECRET = "KRP5Ckc0CKXRBE0QsmmHX3nVG8n5Mu";
    final String API_CALLBACK = "https://console.clever-cloud.com/cli-oauth";
    final CcApiLogin login = new CcApiLogin(myProject, API_CALLBACK);
    service = new ServiceBuilder().provider(CleverCloudApi.class).apiKey(API_KEY).apiSecret(API_SECRET).callback(API_CALLBACK).build();

    if (login.showAndGet()) {
      this.accessToken = new Token(login.getToken(), login.getSecret());
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

  public boolean isValidate() {
    return this.accessToken != null;
  }

  @Nullable
  public String callApi(String url) {
    if (!isValidate()) {
      if (!login()) {
        callApiErrorNotification(url);
        return null;
      }
    }

    OAuthRequest request = new OAuthRequest(Verb.GET, CleverCloudApi.BASE_URL + url);
    service.signRequest(accessToken, request);
    return request.send().getBody();
  }

  private void callApiErrorNotification(String url) {
    Notification notification =
      new Notification("Error Report", "Clever IDEA - Request error", "The following request cannot be executed :<br />" + url,
                       NotificationType.ERROR);
    notification.notify(myProject);
  }

}
