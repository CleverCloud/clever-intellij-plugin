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

/**
 * Main class used to interact with th API
 */
public class CcApi {
  @Nullable private static CcApi ourInstance = null;
  private Project myProject;
  @Nullable private Token accessToken;
  private OAuthService service;

  /**
   * Return the instance of the API.
   * @param project Project opened in the IDE. It's used to get the API settings of the project.
   * @return Instance of the class
   */
  @NotNull
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

  /**
   * Login to the CcApi. Required to call the API.
   * @return true if login success, false if login fail
   */
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

  /**
   * @return if the Access Token is defined or not.
   */
  public boolean isValidate() {
    return this.accessToken != null;
  }

  /**
   * Call directly the API.
   * @param url to call in the API. Should be the complete URL, with parameters.
   * @return body of the response of the API
   */
  @Nullable
  public String callApi(@NotNull String url) {
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
