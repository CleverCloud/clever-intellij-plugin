package com.cleverCloud.cleverIdea.api;

import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.Token;

public class CleverCloudApi extends DefaultApi10a {
  public static final String BASE_URL = "https://api.clever-cloud.com/v2";
  private static final String AUTHORIZE_URL = BASE_URL + "/oauth/authorize?oauth_token=%s";

  @Override
  public String getAccessTokenEndpoint() {
    return BASE_URL + "/oauth/access_token";
  }

  @Override
  public String getRequestTokenEndpoint() {
    return BASE_URL + "/oauth/request_token";
  }

  @Override
  public String getAuthorizationUrl(Token requestToken) {
    return String.format(AUTHORIZE_URL, requestToken.getToken());
  }
}
