package com.regan.saata.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.regan.saata.Constant;
import com.regan.saata.R;
import com.regan.saata.util.SharedPrefrencesUtil;
import com.regan.saata.view.MySplashDialog;

import nl.bravobit.ffmpeg.FFmpeg;


public class SplashActivity extends AppCompatActivity {
    private final static int CODE_REQUEST_WRITE_EXTERNAL = 0x100;
    private WebView wvInit;
    private TextView t1;
    private TextView t2;
    private TextView t3;
    private TextView tvSafe;
    private ImageView ivCancle;
    private ImageView ivConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        wvInit = findViewById(R.id.wv_init);
        wvInit.loadUrl(Constant.USER_PROTOCOL);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.GONE);
        Dialog mySplashDialog = new MySplashDialog(this, R.style.my_splash_dialog);
        t1 = mySplashDialog.findViewById(R.id.tv_t1);
        t2 = mySplashDialog.findViewById(R.id.tv_t2);
        t3 = mySplashDialog.findViewById(R.id.tv_t3);
        t1.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        t2.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        t3.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        tvSafe = mySplashDialog.findViewById(R.id.tv_safe);
        ivCancle = mySplashDialog.findViewById(R.id.iv_cancle);
        ivConfirm = mySplashDialog.findViewById(R.id.iv_confirm);
        //去掉点击后的背景色
        tvSafe.setHighlightColor(getResources().getColor(android.R.color.transparent));
        String safe = tvSafe.getText().toString();
        SpannableStringBuilder spannable = new SpannableStringBuilder(safe);
        //文字点击
        spannable.setSpan(new firstClick(), safe.indexOf("《"), safe.indexOf("》") + 1
                , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new secondClick(), safe.lastIndexOf("《"), safe.lastIndexOf("》") + 1
                , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //一定要记得设置，不然点击不生效
        tvSafe.setMovementMethod(LinkMovementMethod.getInstance());
        tvSafe.setText(spannable);
        boolean isFirst = true;
        isFirst = SharedPrefrencesUtil.getBooleanByKey(this, "isFirst", true);
        if (isFirst) {
            mySplashDialog.show();

            FFmpeg fFmpeg = FFmpeg.getInstance(this);
            fFmpeg.isSupported();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }
            }, 2000);
        }
        ivCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPrefrencesUtil.saveBooleanByKey(SplashActivity.this, "isFirst", true);
                finish();
                System.exit(0);
            }
        });
        ivConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPrefrencesUtil.saveBooleanByKey(SplashActivity.this, "isFirst", false);
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        });
//        checkPermission();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                startActivity(new Intent(SplashActivity.this, MainActivity.class));
//                finish();
//            }
//        }, 2000);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_BACK || super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private class firstClick extends ClickableSpan {

        @Override
        public void onClick(View widget) {
            Intent agr = new Intent(SplashActivity.this, WebViewActivity.class);
            agr.putExtra("url", Constant.USER_PROTOCOL);
            SplashActivity.this.startActivity(agr);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(SplashActivity.this.getResources().getColor(R.color.feed_back_color_area));
        }
    }

    private class secondClick extends ClickableSpan {

        @Override
        public void onClick(View widget) {
            Intent sa = new Intent(SplashActivity.this, WebViewActivity.class);
            sa.putExtra("url", Constant.SAFE_PROTOCOL);
            SplashActivity.this.startActivity(sa);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(SplashActivity.this.getResources().getColor(R.color.feed_back_color_area));
        }
    }

    protected void checkPermission() {
        int permissions = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissions != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    CODE_REQUEST_WRITE_EXTERNAL
            );
        }

    }
}
