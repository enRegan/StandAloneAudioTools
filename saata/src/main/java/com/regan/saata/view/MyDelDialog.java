package com.regan.saata.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.ViewGroup;

import com.regan.saata.R;


public class MyDelDialog extends Dialog {
    public MyDelDialog(Context context) {
        super(context);
        init(context);
    }

    public MyDelDialog(Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    private void init(Context context) {
        setContentView(R.layout.dialog_del);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
    }

}
