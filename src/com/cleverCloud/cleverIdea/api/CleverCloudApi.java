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
  public static final String LOGS_URL = "https://api.clever-cloud.com/v2/logs/";
  public static final String LOGS_SOKCET_URL = "wss://api.clever-cloud.com/v2/logs-socket/%s?since=%s";
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

  @NotNull
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
