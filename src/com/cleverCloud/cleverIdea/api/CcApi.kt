package com.cleverCloud.cleverIdea.api

import java.util.Scanner

import org.scribe.builder.*
import org.scribe.exceptions.OAuthException
import org.scribe.model.*
import org.scribe.oauth.*

public object CcApi {
    private val API_KEY = "JaGomLuixI29k62K9Zf9klIlQbZHdf"
    private val API_SECRET = "KRP5Ckc0CKXRBE0QsmmHX3nVG8n5Mu"

    public fun login() {
        val service = ServiceBuilder()
                .provider(javaClass<CleverCloudApi>())
                .apiKey(API_KEY)
                .apiSecret(API_SECRET)
                .callback("https://console.clever-cloud.com/cli-oauth")
                .build();

        val requestToken = service.getRequestToken()

        println("Please, open the following URL in your browser and log in:")
        println(service.getAuthorizationUrl(requestToken))
        println("And paste the verifier here:")
        print(">>")
        val verifier = Verifier(Scanner(System.`in`).nextLine())

        val accessToken: Token?
        try {
            accessToken = service.getAccessToken(requestToken, verifier)
        } catch(e: OAuthException) {
            return
        }

        val request = OAuthRequest(Verb.GET, CleverCloudApi.BASE_URL + "/self/applications")
        service.signRequest(accessToken, request)
        val response = request.send()
        println("Got it! Lets see what we found...")
        println()
        println(response.getBody())
    }
}
