package com.kisu.ltsconsole;

import android.app.Activity;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 全屏无状态栏
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(
                  View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );

        WebView wv = new WebView(this);
        setContentView(wv);

        WebSettings s = wv.getSettings();

        // 必须开的
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);
        s.setCacheMode(WebSettings.LOAD_DEFAULT);

        // 让网页认为这是较新的 Chrome（重点）
        String chromeUa = s.getUserAgentString();
        // 把 "Version/4.0" 之类的老标记干掉，强化 Chrome 标识
        if (chromeUa != null) {
            chromeUa = chromeUa
                .replaceAll("; wv\\)", "")   // 去掉 wv 标记（可选，但很多网页靠这玩意区分）
                .replaceAll("AppleWebKit/537\\.\\d+", "AppleWebKit/537.36");
        }
        // 兜底：更像 Chrome
        s.setUserAgentString(
            chromeUa == null
                ? "Mozilla/5.0 (Linux; Android 12; Pixel 6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Mobile Safari/537.36"
                : chromeUa
        );

        CookieManager cm = CookieManager.getInstance();
        cm.setAcceptCookie(true);
        cm.setAcceptThirdPartyCookies(wv, true);

        wv.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed(); // ⚠️ 自签/不匹配常用；你公网IP+HTTPS常见
            }
        });

        wv.loadUrl("https://120.79.247.156/");
    }

    @Override
    public void onBackPressed() {
        WebView wv = (WebView) getWindow().getDecorView().findViewById(android.R.id.content);
        if (wv != null && wv.canGoBack()) wv.goBack();
        else super.onBackPressed();
    }
}
