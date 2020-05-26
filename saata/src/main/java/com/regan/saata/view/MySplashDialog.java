package com.regan.saata.view;

import android.app.Dialog;
import android.content.Context;

import com.regan.saata.R;


public class MySplashDialog extends Dialog {

    public MySplashDialog(Context context) {
        super(context);
        init(context);
    }

    public MySplashDialog(Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    private void init(Context context) {
        setContentView(R.layout.dialog_splash);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
    }
}
