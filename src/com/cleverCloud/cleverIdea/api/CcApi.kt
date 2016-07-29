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

import com.cleverCloud.cleverIdea.api.json.Application
import com.cleverCloud.cleverIdea.api.json.WebSocket
import com.cleverCloud.cleverIdea.settings.ApplicationSettings
import com.cleverCloud.cleverIdea.ui.CcApiLogin
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.model.OAuth1AccessToken
import com.github.scribejava.core.model.OAuthRequest
import com.github.scribejava.core.model.Verb
import com.github.scribejava.core.oauth.OAuth10aService
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import java.io.IOException
import java.io.StringWriter

class CcApi {
    private var myProject: Project? = null
    private var myAccessToken: OAuth1AccessToken? = null
    private var myService: OAuth10aService? = null

    fun login(): Boolean {
        val API_KEY = "JaGomLuixI29k62K9Zf9klIlQbZHdf"
        val API_SECRET = "KRP5Ckc0CKXRBE0QsmmHX3nVG8n5Mu"
        val API_CALLBACK = "https://console.clever-cloud.com/cli-oauth"
        val applicationSettings = ServiceManager.getService(myProject!!, ApplicationSettings::class.java)

        myService = ServiceBuilder().apiKey(API_KEY).apiSecret(API_SECRET).callback(API_CALLBACK).build<OAuth10aService>(CleverCloudApi)

        if (applicationSettings.oAuthToken != null && applicationSettings.oAuthSecret != null) {
            myAccessToken = OAuth1AccessToken(applicationSettings.oAuthToken, applicationSettings.oAuthSecret)
            return true
        }

        val login = CcApiLogin(myProject, API_CALLBACK)

        if (login.showAndGet()) {
            myAccessToken = OAuth1AccessToken(login.token, login.secret)
            applicationSettings.oAuthToken = login.token
            applicationSettings.oAuthSecret = login.secret
            return true
        }

        loginErrorNotification()
        return false
    }

    private fun loginErrorNotification() {
        val notification = Notification("Error Report", "Clever IDEA - Login error", "Error while login in Clever Cloud.", NotificationType.ERROR)
        notification.notify(myProject)
    }

    private val isValidate: Boolean
        get() = this.myService != null && this.myAccessToken != null

    fun apiRequest(url: String): String? {
        if (!isValidate && !login()) {
            callApiErrorNotification(url)
            return null
        }

        val request = OAuthRequest(Verb.GET, AUTHORIZE_URL + url, myService)
        myService!!.signRequest(myAccessToken, request)
        return request.send().body
    }

    private fun callApiErrorNotification(url: String) {
        val notification = Notification("Error Report", "Clever IDEA - Request error", "The following request cannot be executed :<br />" + url,
                NotificationType.ERROR)
        notification.notify(myProject)
    }

    fun logRequest(application: Application): String? {
        val url = LOGS_ENDPOINT + application.id + "?limit=" + LOGS_LIMIT + "&order=asc"
        val request = OAuthRequest(Verb.GET, url, myService)
        return request.send().body
    }

    fun wsLogSigner(): String? {
        if (!isValidate && !login()) return null

        val request = OAuthRequest(Verb.GET, HTTP_BASE_URL, myService)
        myService!!.signRequest(myAccessToken, request)
        val ws = WebSocket(request.headers.toString().replace("{Authorization=", "").replace("}", ""))
        val mapper = ObjectMapper()
        mapper.configure(SerializationFeature.INDENT_OUTPUT, false)
        val writer = StringWriter()

        try {
            mapper.writeValue(writer, ws)
            return writer.toString()
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    companion object {
        private var ourInstance: CcApi? = null

        fun getInstance(project: Project): CcApi {
            if (ourInstance == null) ourInstance = CcApi()
            ourInstance!!.myProject = project
            return ourInstance as CcApi
        }
    }
}
