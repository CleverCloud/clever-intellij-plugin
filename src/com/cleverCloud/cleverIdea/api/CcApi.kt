package com.cleverCloud.cleverIdea.api

import java.util.Scanner

import org.scribe.builder.*
import org.scribe.model.*
import org.scribe.oauth.*

public object scribe_example {
    //private val PROTECTED_RESOURCE_URL = "https://api.twitter.com/1.1/account/verify_credentials.json"
    private val API_KEY = "owV52820wFdzmCX6AVpZpJ8DTjbvOM"
    private val API_SECRET = "4gjQQRpa9iQT3pwfgy1546ZI4mbwvl"

    public fun main(args: Array<String>) {
        // If you choose to use a callback, "oauth_verifier" will be the return value by Twitter (request param)
        val service = ServiceBuilder()
                .provider(javaClass<CleverCloudApi>())
                .apiKey(API_KEY)
                .apiSecret(API_SECRET)
                .callback("https://console.clever-cloud.com/cli-oauth")
                .build();
        val `in` = Scanner(System.`in`)

        println("=== Clever Cloud's OAuth Workflow ===")
        println()

        // Obtain the Request Token
        println("Fetching the Request Token...")
        val requestToken = service.getRequestToken()
        println("Got the Request Token!")
        println()

        println("Now go and authorize Scribe here:")
        println(service.getAuthorizationUrl(requestToken))
        println("And paste the verifier here")
        print(">>")
        val verifier = Verifier(`in`.nextLine())
        println()

        // Trade the Request Token and Verfier for the Access Token
        println("Trading the Request Token for an Access Token...")
        val accessToken = service.getAccessToken(requestToken, verifier)
        println("Got the Access Token!")
        println("(if you're curious, it looks like this: " + accessToken + " )")
        println()

        /* // Now let's go and ask for a protected resource!
        println("Now we're going to access a protected resource...")
        val request = OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL)
        service.signRequest(accessToken, request)
        val response = request.send()
        println("Got it! Lets see what we found...")
        println()
        println(response.getBody())*/

        println()
        println("That's it man! Go and build something awesome with Scribe! :)")
    }
}

fun main(args: Array<String>) = scribe_example.main(args)
