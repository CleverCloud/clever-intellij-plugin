/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Clever Cloud, SAS
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

package com.cleverCloud.cleverIdea.api

import com.github.scribejava.core.builder.api.DefaultApi10a
import com.github.scribejava.core.model.OAuth1RequestToken
import com.github.scribejava.core.services.PlaintextSignatureService
import com.github.scribejava.core.services.SignatureService

/**
 * Class representing the API used by Scribe to interact with the Clever Cloud API.
 */
object CleverCloudApi : DefaultApi10a() {

    override fun getAccessTokenEndpoint(): String {
        return HTTP_BASE_URL + "/oauth/access_token"
    }

    override fun getRequestTokenEndpoint(): String {
        return HTTP_BASE_URL + "/oauth/request_token"
    }

    override fun getAuthorizationUrl(requestToken: OAuth1RequestToken): String {
        return String.format(AUTHORIZE_URL, requestToken.token)
    }

    override fun getSignatureService(): SignatureService {
        return PlaintextSignatureService()
    }
}

/** API base URL */
const val HTTP_BASE_URL = "https://api.clever-cloud.com/v2"
/**  */
const val WSS_BASE_URL = "wss://api.clever-cloud.com/v2"
/** Logs endpoint */
const val LOGS_ENDPOINT = HTTP_BASE_URL + "/logs/"
/** Logs' web socket endpoint */
const val LOGS_SOKCET_URL = WSS_BASE_URL + "/logs-socket/%s?since="
/** Maximum lines of log to fetch */
const val LOGS_LIMIT = 300
/**  */
const val AUTHORIZE_URL = HTTP_BASE_URL + "/oauth/authorize?oauth_token="
