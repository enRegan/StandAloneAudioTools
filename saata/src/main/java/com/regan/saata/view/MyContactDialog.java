package com.regan.saata.view;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.regan.saata.R;
import com.regan.saata.util.SharedPrefrencesUtil;


public class MyContactDialog {
    private Dialog contactDialog;

    public Dialog getContactDialog(final Context mContext) {
        if (contactDialog == null) {
            contactDialog = new Dialog(mContext, R.style.my_contact_dialog);
            contactDialog.setContentView(R.layout.dialog_contact);
            contactDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            LinearLayout llEmail = contactDialog.findViewById(R.id.ll_email);
            LinearLayout llWxchat = contactDialog.findViewById(R.id.ll_wxchat);
            LinearLayout llQq = contactDialog.findViewById(R.id.ll_qq);
            final TextView tvMail = (TextView) contactDialog.findViewById(R.id.tv_mail);
            final TextView tvWx = (TextView) contactDialog.findViewById(R.id.tv_wx);
            final TextView tvQq = (TextView) contactDialog.findViewById(R.id.tv_qq);
            String email = SharedPrefrencesUtil.getStringByKey(mContext, SharedPrefrencesUtil.EMAIL);
            tvMail.setText(email);
            llEmail.setVisibility(TextUtils.isEmpty(email) ? View.GONE : View.VISIBLE);
            String wx = SharedPrefrencesUtil.getStringByKey(mContext, SharedPrefrencesUtil.WXCHAT);
            tvWx.setText(wx);
            llWxchat.setVisibility(TextUtils.isEmpty(wx) ? View.GONE : View.VISIBLE);
            String qq = SharedPrefrencesUtil.getStringByKey(mContext, SharedPrefrencesUtil.QQ);
            tvQq.setText(qq);
            llQq.setVisibility(TextUtils.isEmpty(qq) ? View.GONE : View.VISIBLE);
//            contactDialog.setCanceledOnTouchOutside(false);
            Button btMail = contactDialog.findViewById(R.id.bt_mail);
            Button btWx = contactDialog.findViewById(R.id.bt_wx);
            Button btQq = contactDialog.findViewById(R.id.bt_qq);
            btMail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    copy(mContext, tvMail.getText().toString());
                }
            });
            btWx.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    copy(mContext, tvWx.getText().toString());
                }
            });
            btQq.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    copy(mContext, tvQq.getText().toString());
                }
            });
        }
        return contactDialog;
    }

    private void copy(Context mContext, String text) {
        //获取剪贴板管理器：
        ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("Label", text);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
        Toast.makeText(mContext, "联系方式复制成功", Toast.LENGTH_LONG).show();
    }
}
