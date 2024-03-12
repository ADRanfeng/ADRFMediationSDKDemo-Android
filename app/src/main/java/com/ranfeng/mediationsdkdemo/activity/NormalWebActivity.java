package com.ranfeng.mediationsdkdemo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ranfeng.mediationsdkdemo.R;

/**
 * @description 描述
 */
public class NormalWebActivity extends BaseAdActivity {
    private static final String WEB_URL = "WEB_URL";
    private WebView webView;

    public static void jump(@NonNull Context context, @NonNull String webUrl) {
        Intent intent = new Intent(context, NormalWebActivity.class);
        intent.putExtra(WEB_URL, webUrl);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_web);

        webView = findViewById(R.id.webView);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        //设置 缓存模式
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        // 是否支持viewport属性，默认值 false
        // 页面通过`<meta name="viewport" ... />`自适应手机屏幕
        settings.setUseWideViewPort(true);
        // 是否自动加载图片
        settings.setLoadsImagesAutomatically(true);
        // 开启 DOM storage API 功能
        settings.setDomStorageEnabled(true);
        settings.setDefaultTextEncodingName("UTF-8");
        //启用数据库
        settings.setDatabaseEnabled(true);
        settings.setGeolocationEnabled(true);
        webView.clearHistory();
        webView.clearFormData();
        webView.clearCache(true);

        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setSupportMultipleWindows(true);

        // android 5.0以上默认不支持Mixed Content
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(
                    WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 是否在离开屏幕时光栅化(会增加内存消耗)，默认值 false
            settings.setOffscreenPreRaster(false);
        }
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        webView.loadUrl(getIntent().getStringExtra(WEB_URL));
    }
}
