package com.omsworld.familycare.Shopping.InWeb;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.omsworld.familycare.R;
import com.omsworld.familycare.api_call.CommonFunctions;

public class ShopInWeb_Ac extends Activity {

    CommonFunctions cmf;
RelativeLayout RL_backClick;
    WebView web;
    ProgressBar progressBar;
    String siteName,siteUrl;
    TextView TV_toolbar_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_in_web_ac);
        Initview();
    }

    private void Initview() {
        cmf=new CommonFunctions(this);
        try {
            cmf.bdl = this.getIntent().getExtras();
            if (cmf.bdl != null) {
                siteName = cmf.bdl.getString("siteName");
                siteUrl = cmf.bdl.getString("siteUrl");
                cmf.bdl.clear();

            } else {
                cmf.back_fragment();
            }

        } catch (Exception ex) {
        }
        web = (WebView) findViewById(R.id.webview01);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        TV_toolbar_text = (TextView) findViewById(R.id.TV_toolbar_text);
        RL_backClick = (RelativeLayout) findViewById(R.id.RL_backClick);

        WebSettings webSettings=web.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        web.setWebViewClient(new myWebClient());
        web.loadUrl(siteUrl);

        RL_backClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        TV_toolbar_text.setVisibility(View.VISIBLE);
        TV_toolbar_text.setText(R.string.app_name);

    }

    public class myWebClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
           // progressBar.setVisibility(View.VISIBLE);
            view.loadUrl(url);
            return true;

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);

            progressBar.setVisibility(View.GONE);
        }
    }

    // To handle "Back" key press event for WebView to go back to previous screen.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if ((keyCode == KeyEvent.KEYCODE_BACK) && web.canGoBack()) {
            web.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
