/*package com.cleverCloud.cleverIdea.api;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;

public class CcApi {

    private static final String API_KEY = "CHzDKmX55yaM1hIUZ0A9ksdFQYZvkX";
    private static final String API_SECRET = "qydDZfvx9LEuFBcx1rAPU5mbOtFOMi";

    private static CleverCloudApi api = new CleverCloudApi();

    public void executeLogin() {
        CleverCloudApi.oauth = new ServiceBuilder()
                .provider(CleverCloudApi.class)
                .apiKey(API_KEY)
                .apiSecret(API_SECRET)
                .callback("https://console.clever-cloud.com/cli-oauth")
                .build();
        Token requestToken = CleverCloudApi.oauth.getRequestToken();
        String authURL = CleverCloudApi.oauth.getAuthorizationUrl(requestToken);
        /*LoginUI login = new LoginUI(shell, authURL);
        login.openLogin(api);
        Verifier verifier = new Verifier(CleverCloudApi.oauthVerifier);
        CleverCloudApi.accessToken = CleverCloudApi.oauth.getAccessToken(requestToken, verifier);
    }

    @Execute
    public void execute(Shell shell) {
        if (CleverCloudApi.accessToken == null) {
            this.executeLogin(shell);
            return;
        }
        OAuthRequest request = new OAuthRequest(Verb.GET, CleverCloudApi.BASE_URL + "/organisations?user=" + CleverCloudApi.user);
        CleverCloudApi.oauth.signRequest(CleverCloudApi.accessToken, request);
        Response response = request.send();
        System.out.println(response.getBody());
    }
}
*/
