package com.claudioc.scena;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class BrowserActivity extends Activity {

    private WebView webView;

    private static final String DESKTOP_USERAGENT = "Mozilla/5.0 (X11; " +
            "Linux x86_64) AppleWebKit/534.24 (KHTML, like Gecko) " +
            "Chrome/11.0.696.34 Safari/534.24";

    private static final String IPHONE_USERAGENT = "Mozilla/5.0 (iPhone; U; " +
            "CPU iPhone OS 4_0 like Mac OS X; en-us) AppleWebKit/532.9 " +
            "(KHTML, like Gecko) Version/4.0.5 Mobile/8A293 Safari/6531.22.7";

    private static final String IPAD_USERAGENT = "Mozilla/5.0 (iPad; U; " +
            "CPU OS 3_2 like Mac OS X; en-us) AppleWebKit/531.21.10 " +
            "(KHTML, like Gecko) Version/4.0.4 Mobile/7B367 Safari/531.21.10";

    @SuppressLint("SetJavaScriptEnabled")
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_browser);

        Intent intent = this.getIntent();
        String url = intent.getExtras().getString("url");

        final ProgressDialog progressBar = new ProgressDialog(this);
        progressBar.setCancelable(true);
        progressBar.setMessage("Loading...");
        progressBar.show();

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        Boolean jsEnabled = sharedPrefs.getBoolean("browser_js_enabled", true);
        String ua = sharedPrefs.getString("browser_ua", "Default");

        webView = (WebView) findViewById(R.id.webView);
        webView.setWebChromeClient(new JavaScriptChromeClient());
        webView.setWebViewClient(new WebViewClient() {

            @Override
    		public boolean shouldOverrideUrlLoading(WebView view, String url) {
            	return true;
            }

            @Override
            public void onLoadResource(WebView view, String url) {
            	
            }
            
            @Override
            public void onReceivedError(WebView view, int errorCode, String Description, String failingUrl) {
            	return;
            }
            

            @Override
            public void onPageFinished(WebView view, String url) {
	            if (progressBar.isShowing()) {
	                progressBar.dismiss();
	            }
            }
        });

        webView.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);

        WebSettings settings = webView.getSettings();

//        settings.setAllowUniversalAccessFromFileURLs(true);

        // This is the user agent set by the old setUserAgent method
        // A desktop user agent is needed in 2.3.x for YouTube to play video using Flash
        if (!ua.equals("Default")) {
            settings.setUserAgentString(ua);
        }

        settings.setJavaScriptEnabled(jsEnabled);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setBuiltInZoomControls(true);
        settings.setDomStorageEnabled(true);

        // http://developer.android.com/reference/android/os/Build.VERSION_CODES.html

        // This method was deprecated in API level 18. Plugins will not be supported in future, and should not be used.
        if (Build.VERSION.SDK_INT < 8 ) {
            // settings.setPluginsEnabled(true);
        } else {
            settings.setPluginState(WebSettings.PluginState.ON);
        }

        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
       
    	if (savedInstanceState == null) {
            webView.loadUrl(url);
    	}
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState )
    {
    	super.onSaveInstanceState(outState);
    	webView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
    	super.onRestoreInstanceState(savedInstanceState);
    	webView.restoreState(savedInstanceState);
    }    

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
            finish();
        }
    }

    private class JavaScriptChromeClient extends WebChromeClient {

        @Override
        public boolean onJsAlert(WebView view, String url, String message,final JsResult result) {

            new AlertDialog.Builder(BrowserActivity.this)
                .setTitle("User message")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok,
                    new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do your stuff
                            result.confirm();
                        }
                    }).setCancelable(false).create().show();
            return true;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
            new AlertDialog.Builder(BrowserActivity.this)
                .setTitle("Confirm")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            result.confirm();
                        }
                    }).setNegativeButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        result.cancel();
                    }
                }).create().show();
            return true;
        }
    }
}

