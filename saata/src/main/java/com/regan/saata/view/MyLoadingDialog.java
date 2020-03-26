package com.regan.saata.view;

import android.app.Dialog;
import android.content.Context;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.regan.saata.Constant;
import com.regan.saata.R;
import com.regan.saata.util.LogUtils;


public class MyLoadingDialog {
    private Dialog progressDialog;

    public Dialog getLodingDialog(Context mContext) {
//        if(progressDialog == null){
        progressDialog = new Dialog(mContext, R.style.my_progress_dialog);
        progressDialog.setContentView(R.layout.dialog_loading);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        TextView msg = (TextView) progressDialog.findViewById(R.id.id_tv_loadingmsg);
        ProgressBar pvLoading = (ProgressBar) progressDialog.findViewById(R.id.pb_loading);
        msg.setText("卖力加载中");
//        }
        LogUtils.d(Constant.TAG, " progressDialog  : " + progressDialog.toString() + " Context " + mContext.toString());
        return progressDialog;
    }
}
