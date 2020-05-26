package com.regan.saata.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.regan.saata.R;

import nl.bravobit.ffmpeg.FFmpeg;

public class ProgressDialog extends Dialog {
    public ProgressDialog progressDialog;
    public ProgressBar progressBar;
    public Context mContext;
    public FFmpeg fFmpeg;
    public FfmpegKill ffmpegKill;
    private ImageView ivClose;

    public ProgressDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public ProgressDialog(Context context, FFmpeg fFmpeg, FfmpegKill ffmpegKill) {
        super(context);
        this.mContext = context;
        this.fFmpeg = fFmpeg;
        this.ffmpegKill = ffmpegKill;
    }

    public ProgressDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
    }

    public ProgressDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    // 退出时间
    private long currentBackPressedTime = 0;
    // 退出间隔
    private static final int BACK_PRESSED_INTERVAL = 2000;

    public ProgressDialog getProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(mContext, R.style.progress_dialog);
            progressDialog.setContentView(R.layout.progress_dialog);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            progressBar = progressDialog.findViewById(R.id.pb_progress);
            ivClose = progressDialog.findViewById(R.id.iv_close);
            progressBar.setProgress(0);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            ivClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    currentBackPressedTime = 0;
                    ffmpegKill.kill();
                }
            });
            progressDialog.setOnKeyListener(new OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                        if (System.currentTimeMillis() - currentBackPressedTime > BACK_PRESSED_INTERVAL) {
                            currentBackPressedTime = System.currentTimeMillis();
                            Toast.makeText(mContext, "再次点击结束任务", Toast.LENGTH_SHORT).show();
                        } else {
                            progressDialog.dismiss();
                            currentBackPressedTime = 0;
                            ffmpegKill.kill();
                        }
                        return true;
                    }

                    return false;
                }
            });
        }
        return progressDialog;
    }

    public interface FfmpegKill {
        void kill();
    }

    public void dismissDialog() {
        if (null != progressDialog && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public ProgressBar getProgressBar() {
        if (progressBar == null) {
            progressBar = progressDialog.findViewById(R.id.pb_progress);
        }
        return progressBar;
    }
}
