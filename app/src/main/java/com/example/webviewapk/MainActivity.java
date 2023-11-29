package com.example.sample;

        import androidx.annotation.NonNull;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.core.app.ActivityCompat;
        import androidx.core.content.ContextCompat;
        import android.Manifest;
        import android.annotation.TargetApi;
        import android.content.SharedPreferences;
        import android.content.pm.PackageManager;
        import android.os.Build;
        import android.os.Bundle;
        import android.view.KeyEvent;
        import android.view.View;
        import android.webkit.PermissionRequest;
        import android.webkit.WebChromeClient;
        import android.webkit.WebResourceRequest;
        import android.webkit.WebSettings;
        import android.webkit.WebView;
        import android.webkit.WebViewClient;
        import android.widget.FrameLayout;
        import android.widget.ProgressBar;
        import android.widget.Toast;
        
        import java.util.ArrayList;
        import java.util.List;
        
        public class MainActivity extends AppCompatActivity {
            WebView webView; //declare the webview
            ProgressBar progressBar;
        
            FrameLayout frameLayout;
        
            private PermissionRequest mPermissionRequest;
        
            SharedPreferences sharedPreferences;
        
        
            private static final int PERMISSION_REQUEST_CODE = 1234;
            @Override
            public boolean onKeyDown(int keyCode, KeyEvent event) {
        
                if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
                    webView.goBack();
                    return true;
                }
                return super.onKeyDown(keyCode, event);
            }
        
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main);
        
                this.progressBar = findViewById(R.id.progressBar);
        
                this.webView = findViewById(R.id.web_view);
        
                this.frameLayout = findViewById(R.id.fragment_container);
        
                sharedPreferences = getSharedPreferences("permissions", MODE_PRIVATE);
        
                if(!sharedPreferences.getBoolean("hasAssigned",false))
                {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
        
                    editor.putString("permission","0000");
        
                    editor.putBoolean("hasAssigned",true);
        
                    editor.commit();
                }
        
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new splash_fragment())
                        .commit();
        
                webView.loadUrl(getString(R.string.base_URL));
        
                WebSettings webSettings = webView.getSettings();
                webSettings.setDomStorageEnabled(true);
                webSettings.setJavaScriptEnabled(true);
                webView.getSettings().setAllowFileAccess(true);
        
                this.webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT); // Use cache when content is available
        
                webSettings.setLoadWithOverviewMode(true);
                webSettings.setUseWideViewPort(true);
        
                webView.setNestedScrollingEnabled(true);
        
                webView.getSettings().setSupportZoom(true); //used for providing some flexibilty while zooming in or out
                webView.getSettings().setBuiltInZoomControls(true);
                webView.getSettings().setDisplayZoomControls(false);
        
                webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
                webView.setScrollbarFadingEnabled(false);
        
        //        webSettings.setUserAgentString(webSettings.getUserAgentString().replace("; wv",""));
                webSettings.setUserAgentString("Mozilla/5.0 (Linux; Android 10; Pixel 4 XL Build/QQ3A.200805.001) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.82 Mobile Safari/537.36");
        
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
                    public void onPermissionRequest(final PermissionRequest request) {
                        mPermissionRequest = request; // Store the permission request
                        runOnUiThread(() -> {
                            String[] requestedResources = request.getResources();
        
                            String[] androidPermissions = get_permissions();
        
                            String permission = "";
        
                            for(String perm: requestedResources)
                            {
                                permission+=perm;
                            }
        
                            Toast.makeText(getApplicationContext(),permission,Toast.LENGTH_LONG).show();
        
                            if (hasAllPermissions(androidPermissions)) {
                                request.grant(requestedResources);
                            } else {
                                ActivityCompat.requestPermissions(MainActivity.this, androidPermissions, PERMISSION_REQUEST_CODE);
                            }
                        });
                    }
        
                    @Override
                    public void onProgressChanged(WebView view, int newProgress) {
                        progressBar.setProgress(newProgress);
                        if (newProgress == 100) {
                            progressBar.setVisibility(View.INVISIBLE);
                            frameLayout.setVisibility(View.GONE);
                            webView.setVisibility(View.VISIBLE);
                        } else {
                            progressBar.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        
            private boolean hasAllPermissions(String[] permissions) {
                for (String permission : permissions) {
                    if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                        return false;
                    }
                }
                return true;
            }
        
            private String[] mapToAndroidPermissions(List<String> webResources) {
                List<String> permissions = new ArrayList<>();
                permissions.add(Manifest.permission.CAMERA);
                permissions.add(Manifest.permission.RECORD_AUDIO);
        
                return permissions.toArray(new String[0]);
            }
        
            private String[] get_permissions() {
                String permission = sharedPreferences.getString("permission", "0000");
                List<String> permissions = new ArrayList<>();
        
                if (permission.charAt(0) == '1') {
                    permissions.add(android.Manifest.permission.CAMERA);
                    permissions.add(android.Manifest.permission.RECORD_AUDIO);
                }
                if (permission.charAt(1) == '1') {
                    permissions.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
                    permissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
                    permissions.add(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION);
                }
                if (permission.charAt(2) == '1') {
                    permissions.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
                    permissions.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
                if (permission.charAt(3) == '1') {
                    permissions.add(android.Manifest.permission.BLUETOOTH);
                    permissions.add(android.Manifest.permission.BLUETOOTH_ADMIN);
                    permissions.add(android.Manifest.permission.BLUETOOTH_CONNECT);
                    permissions.add(android.Manifest.permission.BLUETOOTH_ADVERTISE);
                    permissions.add(android.Manifest.permission.BLUETOOTH_SCAN);
                }
        
                return permissions.toArray(new String[0]);
            }
        
        
        
            @Override
            public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                if (requestCode == PERMISSION_REQUEST_CODE) {
                    Toast.makeText(getApplicationContext(),"Permissions Granted",Toast.LENGTH_LONG).show();
                }
            }
        }