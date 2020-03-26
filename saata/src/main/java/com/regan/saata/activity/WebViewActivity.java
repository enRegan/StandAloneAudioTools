package com.regan.saata.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.regan.saata.Constant;
import com.regan.saata.R;
import com.regan.saata.util.LogUtils;

import java.lang.reflect.Field;

public class WebViewActivity extends AppCompatActivity {
    private WebView my_webview;
    private String url = "";
    private ProgressBar adProgressBar;
    private LinearLayout webview_parent;
    ImageView ivBack;
    TextView tvTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);
        my_webview = this.findViewById(R.id.web);
        webview_parent = this.findViewById(R.id.parent);
        adProgressBar = this.findViewById(R.id.adProgressBar);

        if (getIntent() != null && getIntent().getExtras() != null) {
            url = getIntent().getStringExtra("url");
        }

        tvTitle.setText("");
        loadwebview();
        showbar();
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadwebview() {
        WebSettings settings = my_webview.getSettings();
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportZoom(true);
        settings.setDomStorageEnabled(true);
        my_webview.setBackgroundColor(Color.parseColor("#ffffff"));
        my_webview.loadUrl(url);

        my_webview.setWebViewClient(new MyWebViewClient());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && my_webview.canGoBack()) {
            my_webview.goBack();
            return true;
        } else {
            finish();
        }
        return true;
    }


    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("http://") || url.startsWith("https://")) {
                view.loadUrl(url);
                LogUtils.d("进来", "进来http://");
            } else {
                LogUtils.d("进来", "进来没有http://");
            }
            return true;
        }
    }


    public void showbar() {
        //显示进度条
        my_webview.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {

                if (newProgress == 100) {
                    adProgressBar.setVisibility(View.GONE);
                } else {
                    if (View.GONE == adProgressBar.getVisibility()) {
                        adProgressBar.setVisibility(View.VISIBLE);
                    }
                    adProgressBar.setProgress(newProgress);
                }


            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                tvTitle.setText(title);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        my_webview.removeAllViews();
        my_webview.destroy();
        webview_parent.removeView(my_webview);
        releaseAllWebViewCallback();
        System.gc();
    }


    public void releaseAllWebViewCallback() {
        if (android.os.Build.VERSION.SDK_INT < 16) {
            try {

                Field field = WebView.class.getDeclaredField("mWebViewCore");
                field = field.getType().getDeclaredField("mBrowserFrame");
                field = field.getType().getDeclaredField("sConfigCallback");
                field.setAccessible(true);
                field.set(null, null);
            } catch (NoSuchFieldException e) {
                LogUtils.d(Constant.TAG, e.getMessage());
            } catch (IllegalAccessException e) {
                LogUtils.d(Constant.TAG, e.getMessage());
            }
        } else {
            try {
                Field sConfigCallback = Class.forName("android.webkit.BrowserFrame").getDeclaredField("sConfigCallback");
                if (sConfigCallback != null) {
                    sConfigCallback.setAccessible(true);
                    sConfigCallback.set(null, null);
                }
            } catch (NoSuchFieldException e) {
                LogUtils.d(Constant.TAG, e.getMessage());
            } catch (ClassNotFoundException e) {
                LogUtils.d(Constant.TAG, e.getMessage());
            } catch (IllegalAccessException e) {
                LogUtils.d(Constant.TAG, e.getMessage());
            }
        }
    }
}
