package com.regan.saata.view;

import android.app.Dialog;
import android.content.Context;

import com.regan.saata.R;


public class MySplashDialog {
    private Dialog progressDialog;

    public Dialog getSplashDialog(Context mContext) {
        if (progressDialog == null) {
            progressDialog = new Dialog(mContext, R.style.my_splash_dialog);
            progressDialog.setContentView(R.layout.splash_loading);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
        }
        return progressDialog;
    }
}
