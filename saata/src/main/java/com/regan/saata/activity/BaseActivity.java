package com.regan.saata.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import com.regan.saata.Constant;
import com.regan.saata.view.MyLoadingDialog;

import nl.bravobit.ffmpeg.FFmpeg;


public class BaseActivity extends AppCompatActivity {
    private final static int CODE_REQUEST_WRITE_EXTERNAL = 0x100;
    boolean isShowBack;
    ImageView ivBack;
    TextView tvTitle;

    public FFmpeg fFmpeg;

    public Dialog loadingDialog;
    public boolean isGetPermissions = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermission();
        fFmpeg = FFmpeg.getInstance(this);
        fFmpeg.setTimeout(Constant.FFMPEG_TIMEOUT);
        loadingDialog = new MyLoadingDialog().getLodingDialog(this);
    }

    protected void checkPermission() {
        int permissions;
        if (Build.VERSION.SDK_INT < 23) {
            permissions = PermissionChecker.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } else {
            permissions = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permissions != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    CODE_REQUEST_WRITE_EXTERNAL
            );
        } else {
            isGetPermissions = true;
        }

    }

    public void showBack(ImageView ivBack, boolean isShowBack) {
        this.isShowBack = isShowBack;
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ivBack.setVisibility(isShowBack ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODE_REQUEST_WRITE_EXTERNAL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isGetPermissions = true;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loadingDialog = null;
        fFmpeg = null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
