package com.regan.saata.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.regan.saata.R;
import com.regan.saata.adapter.AudioSingleSelectAdapter;
import com.regan.saata.bean.AudioInfo;
import com.regan.saata.util.FileManager;
import com.regan.saata.view.MyLoadingDialog;

import java.util.ArrayList;

public class AudioSingleSelectActivity extends AppCompatActivity {
    private RecyclerView rvAudio;
    private TextView tvConfirm;
    private ImageView ivBack;
    private Dialog myLoadingDialog;
    private ArrayList<AudioInfo> audioInfos;
    private AudioSingleSelectAdapter audioListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_multi_select);
        init();
    }

    private void init() {
        rvAudio = findViewById(R.id.rv_audio);
        tvConfirm = findViewById(R.id.tv_confirm);
        ivBack = findViewById(R.id.iv_back);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        audioInfos = new ArrayList<>();
        myLoadingDialog = new MyLoadingDialog().getLodingDialog(AudioSingleSelectActivity.this);
        audioListAdapter = new AudioSingleSelectAdapter(AudioSingleSelectActivity.this, audioInfos, tvConfirm, new AudioSingleSelectAdapter.CheckDone() {
            @Override
            public void done(ArrayList<AudioInfo> data) {
//                LogUtils.d(Constant.TAG, "CheckDone data.size : " + data.size());
                Intent intent = new Intent();
                intent.putExtra("data", data);
                AudioSingleSelectActivity.this.setResult(RESULT_OK, intent);
//                mContext.startActivity(intent);
//                ((AppCompatActivity)mContext).startActivityForResult(intent, MainActivity.CODE_TO_FUNC);
                AudioSingleSelectActivity.this.finish();
            }
        });
        rvAudio.setLayoutManager(new LinearLayoutManager(AudioSingleSelectActivity.this));
        rvAudio.setAdapter(audioListAdapter);
        myLoadingDialog.show();
//        LogUtils.d(Constant.TAG, "show");
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileManager fileManager = FileManager.getInstance(AudioSingleSelectActivity.this);
                audioInfos = fileManager.getMusics();
                new Handler(getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        audioListAdapter.refresh(audioInfos);
                        if (myLoadingDialog != null) {
                            myLoadingDialog.dismiss();
//                            LogUtils.d(Constant.TAG, "dismiss");
                        }
                    }
                });
            }
        }).start();
    }
}
