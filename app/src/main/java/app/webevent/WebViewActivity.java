package app.webevent;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class WebViewActivity extends AppCompatActivity {

    public class WebAppInterface {
        WebViewActivity act;
        WebView wv;

        WebAppInterface(WebViewActivity act, WebView wv) {
            this.act = act;
            this.wv = wv;
        }

        @JavascriptInterface
        public void updateUrl(String url) {
            SharedPreferences sp = getSharedPreferences(getString(R.string.pref_key), MODE_PRIVATE);
            SharedPreferences.Editor spe = sp.edit();
            spe.putString("url", url);
            spe.apply();
        }

        @JavascriptInterface
        public void resetUrl() {
            SharedPreferences sp = getSharedPreferences(getString(R.string.pref_key), MODE_PRIVATE);
            SharedPreferences.Editor spe = sp.edit();
            spe.remove("url");
            spe.apply();
        }

        @JavascriptInterface
        public void clearCache() {
            runOnUiThread(() -> wv.clearCache(true));
        }

        @JavascriptInterface
        public void reload() {
            SharedPreferences sp = getSharedPreferences(getString(R.string.pref_key), MODE_PRIVATE);
            String url = sp.getString("url", "");
            if(url.isEmpty()) {
                runOnUiThread(() -> act.recreate());
            } else {
                runOnUiThread(() -> wv.loadUrl(url));
            }
        }
    }


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        SharedPreferences sp = getSharedPreferences(getString(R.string.pref_key), MODE_PRIVATE);
        String url = sp.getString("url", "");
        if(url.isEmpty()) {
            Intent in = new Intent(this, ProvisioningActivity.class);
            in.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION |
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_TASK_ON_HOME);
            startActivity(in);
            this.finish();
        } else {
            WebView wv = findViewById(R.id.webview);
            WebSettings ws = wv.getSettings();
            ws.setAllowFileAccess(false);
            ws.setJavaScriptEnabled(true);
            ws.setUserAgentString("WebEvent " + BuildConfig.VERSION_NAME);
            wv.addJavascriptInterface(new WebAppInterface(this, wv), "webEvent");
            CookieManager cm = CookieManager.getInstance();
            cm.setAcceptCookie(true);
            cm.setAcceptThirdPartyCookies(wv, false);
            wv.loadUrl(url);
        }
    }
}