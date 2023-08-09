package com.example.webviewapk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    WebView webView; //declare the webview
    ProgressBar progressBar;
    SwipeRefreshLayout swipeContainer;

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.progressBar = findViewById(R.id.progressBar);

        this.webView = findViewById(R.id.web_view);

        webView.loadUrl(getString(R.string.base_URL));

        WebSettings webSettings = webView.getSettings();
        webSettings.setDomStorageEnabled(true);

//        CookieManager cookieManager = CookieManager.getInstance();
//        cookieManager.setAcceptCookie(true); // allow cookies
//        CookieManager.getInstance().setAcceptThirdPartyCookies(this.webView, true);

        this.webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT); // Use cache when content is available

        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);

        webView.getSettings().setSupportZoom(true); //used for providing some flexibilty while zooming in or out
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);

        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(false);

        webSettings.setUserAgentString(webSettings.getUserAgentString().replace("; wv",""));

        swipeContainer = findViewById(R.id.swipeContainer);

//        // Configure the refreshing colors
//        swipeContainer.setColorSchemeResources(R.color.black,
//                android.R.color.holo_green_dark,
//                android.R.color.holo_orange_dark,
//                android.R.color.holo_blue_dark);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.reload();
                swipeContainer.setRefreshing(false);
            }
        });

//        Toast.makeText(this,webSettings.getUserAgentString(),Toast.LENGTH_LONG).show();
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return false;
            }
        });

        this.webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    progressBar.setVisibility(View.INVISIBLE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        });

        // WebViewClient allows you to handler
        // onPageFinished and override Url loading.
        webView.setWebViewClient(new WebViewClient());
    }
}