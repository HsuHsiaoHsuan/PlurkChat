package idv.funnybrain.plurkchat;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.scribe.model.*;
import org.scribe.oauth.OAuthService;

public class FunnyActivity extends Activity {
    // ---- constant START ----
    private static final boolean D = true;
    private static final String TAG = "FunnyActivity";

    private final static String API_KEY = "eOrMDFD8f5V2";
    private final static String API_SECRET = "aiVo5OOdWJMl0vEEblPVNRmbG3EeIQh5";

    private final static int HANDLER_SHOW_AUTH_URL = 0;
    private final static int HANDLER_GET_ACCESS_TOKEN_OK = HANDLER_SHOW_AUTH_URL + 1;

    // ---- constant END ----

    // ---- local variable START ----
    private PlurkOAuth plurkOAuth;
    private OAuthService service;
    private Token requestToken;
    private Token accessToken;
    private Handler handler;
    private Verifier verifier;
    // ---- local variable END ----

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final EditText et_auth = (EditText) findViewById(R.id.et_auth);
        final Button bt_submit_auth = (Button) findViewById(R.id.bt_submit_auth);
        bt_submit_auth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String verification_code = et_auth.getText().toString();
                if(D) { Log.d(TAG, "the verification code is: " + verification_code); }
                new PlurkGetAccessTokenAsyncTask().execute(verification_code);
            }
        });

        Button bt_getAuth = (Button) findViewById(R.id.bt_getAuth);
        bt_getAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PlurkLoginAsyncTask().execute("");
            }
        });

        final WebView webView = (WebView) findViewById(R.id.wv_auth_url);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case HANDLER_SHOW_AUTH_URL:
                        if(D) { Log.d(TAG, "HANDLER_SHOW_AUTH_URL"); }
                        String AuthURL = msg.getData().getString("URL");
                        webView.loadUrl(AuthURL);
                        et_auth.setVisibility(View.VISIBLE);
                        bt_submit_auth.setVisibility(View.VISIBLE);
                        break;
                    case HANDLER_GET_ACCESS_TOKEN_OK:
                        if(D) { Log.d(TAG, "HANDLER_GET_ACCESS_TOKEN"); }
                        new PlurkTmpAsyncTask().execute("");
                        break;
                }
            }
        };

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(D) Log.d(TAG, "onResume");
    }

    private void toast(Object msg) {
        Toast.makeText(this, "" + msg, Toast.LENGTH_SHORT).show();
    }

    // ---- inner class START ----
    private class PlurkLoginAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            plurkOAuth = new PlurkOAuth(API_KEY, API_SECRET);
            plurkOAuth.getAuthURL();

            Message msg = new Message();
            msg.what = HANDLER_SHOW_AUTH_URL;
            msg.getData().putString("URL", plurkOAuth.getAuthURL());
            handler.sendMessage(msg);

            return "";
        }
    }

    private class PlurkGetAccessTokenAsyncTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            plurkOAuth.setAccessToken(params[0]);

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean) {
                Message msg = new Message();
                msg.what = HANDLER_GET_ACCESS_TOKEN_OK;
                handler.sendMessage(msg);
            }
        }
    }

    private class PlurkTmpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            plurkOAuth.test();
            return null;
        }
    }
    // ---- inner class END----
}
