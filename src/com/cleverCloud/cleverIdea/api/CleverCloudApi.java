package com.cleverCloud.cleverIdea.api;

import org.jetbrains.annotations.NotNull;
import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.Token;
import org.scribe.services.PlaintextSignatureService;
import org.scribe.services.SignatureService;

/**
 * Class representing the API used by Scribe to interact with the Clever Cloud API.
 */
public class CleverCloudApi extends DefaultApi10a {
  public static final String BASE_URL = "https://api.clever-cloud.com/v2";
  public static final String LOGS_URL = "https://logs-api.clever-cloud.com/logs/";
  public static final String LOGS_SOKCET_URL = "wss://logs-api.clever-cloud.com/logs-socket/%s?since=%s";
  public static final int LOG_LIMIT = 300;
  private static final String AUTHORIZE_URL = BASE_URL + "/oauth/authorize?oauth_token=%s";

  @NotNull
  @Override
  public String getAccessTokenEndpoint() {
    return BASE_URL + "/oauth/access_token";
  }

  @NotNull
  @Override
  public String getRequestTokenEndpoint() {
    return BASE_URL + "/oauth/request_token";
  }

  @Override
  public String getAuthorizationUrl(@NotNull Token requestToken) {
    return String.format(AUTHORIZE_URL, requestToken.getToken());
  }

  @NotNull
  @Override
  public SignatureService getSignatureService() {
    return new PlaintextSignatureService();
  }
}
