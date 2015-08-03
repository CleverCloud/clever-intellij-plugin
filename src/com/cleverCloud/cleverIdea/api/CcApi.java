package com.cleverCloud.cleverIdea.api;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.*;
import org.scribe.oauth.OAuthService;

public class CcApi {
  private static CcApi ourInstance = new CcApi();
  private Token accessToken;
  private OAuthService service;

  private CcApi() {
  }

  public static CcApi getInstance() {
    if (ourInstance == null) return new CcApi();

    return ourInstance;
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
  public Response callApi(String address) {
    if (!isValidate()) return null;

    OAuthRequest request = new OAuthRequest(Verb.GET, CleverCloudApi.BASE_URL + address);
    service.signRequest(accessToken, request);
    return request.send();
  }

}
