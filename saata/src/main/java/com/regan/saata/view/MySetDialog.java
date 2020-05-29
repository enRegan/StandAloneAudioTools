package com.regan.saata.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.regan.saata.Constant;
import com.regan.saata.R;
import com.regan.saata.adapter.SetDialogItemAdapter;
import com.regan.saata.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class MySetDialog extends Dialog {
    DialogListener dialogListener;
    //    private MySetDialog setDialog;
    private ImageView ivClose;
    private List<String> data;
    private RecyclerView rvSet;
    private Button btnConfirm;
    private int result;
    private SetDialogItemAdapter setDialogItemAdapter;
    private TextView tvName;
    private TextView tvContent;

    public MySetDialog(Context context) {
        super(context);
        init(context);
    }

    public MySetDialog(Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    protected MySetDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

    private void init(Context mContext) {
        setContentView(R.layout.dialog_set);
        ivClose = findViewById(R.id.iv_close);
        rvSet = findViewById(R.id.rv_set);
        btnConfirm = findViewById(R.id.btn_confirm);
        tvName = findViewById(R.id.tv_name);
        tvContent = findViewById(R.id.tv_content);

        data = new ArrayList<>();
        setDialogItemAdapter = new SetDialogItemAdapter(mContext, data);
        setDialogItemAdapter.setItemClickListener(new SetDialogItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                result = position;
                LogUtils.d(Constant.TAG, " position " + position + " result : " + result);
            }
        });
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogListener.getResult(result);
                dismiss();
            }
        });

        rvSet.setAdapter(setDialogItemAdapter);
        rvSet.setLayoutManager(new GridLayoutManager(mContext, 3));
    }

    /**
     * 设置显示内容
     *
     * @param type 1 gif播放速度 2 音频采样率 3 音频比特率 4 视频比特率 5 视频分辨率 6
     */
    public void setData(int type, final int position) {
        switch (type) {
            case 1:
                data.add("很快");
                data.add("较快");
                data.add("中等");
                data.add("较慢");
                data.add("很慢");
                tvName.setText("播放速度设置");
                tvContent.setText("Playback speed setting");
                break;
            case 2:
                data.add("96000");
                data.add("48000");
                data.add("44100");
                data.add("32000");
                data.add("11025");
                data.add("8000");
                tvName.setText("采样率设置");
                tvContent.setText("Sample rate setting");
                break;
            case 3:
                data.add("320k");
                data.add("256k");
                data.add("224k");
                data.add("192k");
                data.add("160k");
                data.add("32k");
                tvName.setText("比特率设置");
                tvContent.setText("Bit rate setting");
                break;
            case 4:
                data.add("128K");
                data.add("512K");
                data.add("1M");
                data.add("2M");
                data.add("3M");
                data.add("5M");
                tvName.setText("比特率设置");
                tvContent.setText("Bit rate setting");
                break;
            case 5:
                data.add("240p");//320x240
                data.add("360p");//480X360
                data.add("480p");//540X480
                data.add("720p");//960X720
                data.add("1080p");//1600X900
                tvName.setText("分辨率设置");
                tvContent.setText("Resolution setting");
                break;
        }
        setDialogItemAdapter.refresh(data, position);
//        result = position;
    }

//    public MySetDialog getLodingDialog(Context mContext,int type) {//type: 1速率
////        setDialog = new MySetDialog(mContext, R.style.my_set_dialog);
//        setDialog.setContentView(R.layout.dialog_set);
//
//        ivClose = setDialog.findViewById(R.id.iv_close);
//        rvSet = setDialog.findViewById(R.id.rv_set);
//        btnConfirm = setDialog.findViewById(R.id.btn_confirm);
//
//        data = new ArrayList<>();
//        if(type == 1){
//            data.add("很快");
//            data.add("较快");
//            data.add("中等");
//            data.add("较慢");
//            data.add("很慢");
//        }
//        SetDialogItemAdapter setDialogItemAdapter = new SetDialogItemAdapter(mContext, data);
//        setDialogItemAdapter.setItemClickListener(new SetDialogItemAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(int position) {
//                result = data.get(position);
//            }
//        });
//        ivClose.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setDialog.dismiss();
//            }
//        });
//        btnConfirm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialogListener.getResult(result);
//            }
//        });
//
//        rvSet.setAdapter(setDialogItemAdapter);
//        rvSet.setLayoutManager(new GridLayoutManager(mContext, 3));
//        return setDialog;
//    }

    public interface DialogListener {
        public void getResult(int result);
    }

    public void setDialogListener(DialogListener listener) {
        this.dialogListener = listener;
    }
}
