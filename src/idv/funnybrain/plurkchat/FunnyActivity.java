package idv.funnybrain.plurkchat;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.PlurkApi;
import org.scribe.model.*;
import org.scribe.oauth.OAuthService;

public class FunnyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        new PlurkOAuthAsyncTask().execute("GO");
    }

    private static class PlurkOAuthAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            // Step One: Create the OAuthService object
            OAuthService service = new ServiceBuilder().provider(PlurkApi.class)
                    .apiKey("eOrMDFD8f5V2")
                    .apiSecret("aiVo5OOdWJMl0vEEblPVNRmbG3EeIQh5")
                    .build();

            // Step Two: Get the request token
            Token requestToken = service.getRequestToken();

            // Step Three: Making the user validate your request token
            String authUrl = service.getAuthorizationUrl(requestToken);

            // Step Four: Get the access Token
            Verifier v = new Verifier("verifier you got from the user");
            Token accessToken = service.getAccessToken(requestToken, v); // the requestToken you had from step 2

            // Step Five: Sign request
            OAuthRequest request = new OAuthRequest(Verb.POST, "http://www.plurk.com/OAuth/request_token");
            service.signRequest(accessToken, request); // the access token from step 4
            Response response = request.send();
            System.out.println(response.getBody());

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
}
