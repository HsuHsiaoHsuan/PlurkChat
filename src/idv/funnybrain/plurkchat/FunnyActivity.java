package idv.funnybrain.plurkchat;

import android.app.Activity;
import android.os.Bundle;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.PlurkApi;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

public class FunnyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        OAuthService service = new ServiceBuilder().provider(PlurkApi.class)
                                                   .apiKey("eOrMDFD8f5V2")
                                                   .apiSecret("aiVo5OOdWJMl0vEEblPVNRmbG3EeIQh5")
                                                   .build();
        Token requestToken = service.getRequestToken();

        String authUrl = service.getAuthorizationUrl(requestToken);
    }
}
