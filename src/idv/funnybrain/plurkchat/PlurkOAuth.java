package idv.funnybrain.plurkchat;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.PlurkApi;
import org.scribe.model.*;
import org.scribe.oauth.OAuthService;

/**
 * Created by Freeman on 2014/4/2.
 */
public class PlurkOAuth implements Parcelable {
    private static final boolean D = true;
    private static final String TAG = "PlurkOAuth";

    private final String APP_KEY;
    private final String APP_SECRET;

    private OAuthService service;
    private Token requestToken;
    private Token accessToken;

    public PlurkOAuth(String key, String secret) {
        APP_KEY = key;
        APP_SECRET = secret;
        service = new ServiceBuilder().provider(PlurkApi.Mobile.class)
                                      .apiKey(APP_KEY)
                                      .apiSecret(APP_SECRET)
                                      .build();
        requestToken = service.getRequestToken();
    }

    public PlurkOAuth(Parcel parcel) {
        APP_KEY = parcel.readString();
        APP_SECRET = parcel.readString();
    }

    public static final Creator<PlurkOAuth> CREATOR = new Creator<PlurkOAuth>() {
        @Override
        public PlurkOAuth createFromParcel(Parcel source) {
            return new PlurkOAuth(source);
        }

        @Override
        public PlurkOAuth[] newArray(int size) {
            return new PlurkOAuth[0];
        }
    };

    public String getAuthURL() {
        if(D) { Log.d(TAG, "getAuthURL"); }
        return service.getAuthorizationUrl(requestToken);
    }

    public Token getRequestToken() {
        if(D) { Log.d(TAG, "getRequestToken"); }
        return requestToken;
    }

    public boolean setAccessToken(String validation_code) {
        accessToken = service.getAccessToken(requestToken, new Verifier(validation_code));
        return true;
    }

    public Token getAccessToken() {
        return accessToken;
    }

    public void test() {
        OAuthRequest req = new OAuthRequest(Verb.POST, "http://www.plurk.com/APP/Timeline/plurkAdd?content=myAppTestTest&qualifier=says&lang=tr_ch");
        service.signRequest(accessToken, req);
        Response resp = req.send();
        String result = resp.getBody();
        System.out.println("final result: " + result);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(APP_KEY);
        dest.writeString(APP_SECRET);
    }
}
