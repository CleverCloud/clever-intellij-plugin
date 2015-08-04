package com.cleverCloud.cleverIdea.api;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.*;
import org.scribe.oauth.OAuthService;

public class CcApi {
  private Token accessToken;
  private OAuthService service;

  public static CcApi getInstance(Project project) {
    return ServiceManager.getService(project, CcApi.class);
  }

  public boolean login(Project project) {
    String API_KEY = "JaGomLuixI29k62K9Zf9klIlQbZHdf";
    String API_SECRET = "KRP5Ckc0CKXRBE0QsmmHX3nVG8n5Mu";
    String API_CALLBACK = "https://console.clever-cloud.com/cli-oauth";

    service = new ServiceBuilder().provider(CleverCloudApi.class).apiKey(API_KEY).apiSecret(API_SECRET).callback(API_CALLBACK).build();

    Token requestToken = service.getRequestToken();
    CcApiLogin login = new CcApiLogin(project, service.getAuthorizationUrl(requestToken));

    if (login.showAndGet()) {
      Verifier verifier = new Verifier(login.getVerifier());
      this.accessToken = service.getAccessToken(requestToken, verifier);
    }

    return true;
  }

  public boolean isValidate() {
    return this.accessToken != null;
  }

  @Nullable
  public String callApi(String url) {
    if (!isValidate()) return null;

    OAuthRequest request = new OAuthRequest(Verb.GET, CleverCloudApi.BASE_URL + url);
    service.signRequest(accessToken, request);
    return request.send().getBody();
  }

}
