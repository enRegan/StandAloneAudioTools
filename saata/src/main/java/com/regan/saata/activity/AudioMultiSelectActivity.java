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

import com.regan.saata.Constant;
import com.regan.saata.R;
import com.regan.saata.adapter.AudioMuitiSelectAdapter;
import com.regan.saata.bean.AudioInfo;
import com.regan.saata.util.FileManager;
import com.regan.saata.util.LogUtils;
import com.regan.saata.view.MyLoadingDialog;

import java.util.ArrayList;

public class AudioMultiSelectActivity extends AppCompatActivity {
    private RecyclerView rvAudio;
    private TextView tvConfirm;
    private ImageView ivBack;
    private Dialog myLoadingDialog;
    private ArrayList<AudioInfo> audioInfos;
    private AudioMuitiSelectAdapter audioListAdapter;

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
        myLoadingDialog = new MyLoadingDialog().getLodingDialog(AudioMultiSelectActivity.this);
        audioListAdapter = new AudioMuitiSelectAdapter(AudioMultiSelectActivity.this, audioInfos, tvConfirm, new AudioMuitiSelectAdapter.CheckDone() {
            @Override
            public void done(ArrayList<AudioInfo> data) {
//                LogUtils.d(Constant.TAG, "CheckDone data.size : " + data.size());
                Intent intent = new Intent(AudioMultiSelectActivity.this, AudioMergeActivity.class);
                intent.putExtra("data", data);
                AudioMultiSelectActivity.this.setResult(RESULT_OK, intent);
//                mContext.startActivity(intent);
//                ((AppCompatActivity)mContext).startActivityForResult(intent, MainActivity.CODE_TO_FUNC);
                AudioMultiSelectActivity.this.finish();
            }
        });
        rvAudio.setLayoutManager(new LinearLayoutManager(AudioMultiSelectActivity.this));
        rvAudio.setAdapter(audioListAdapter);
        myLoadingDialog.show();
//        LogUtils.d(Constant.TAG, "show");
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileManager fileManager = FileManager.getInstance(AudioMultiSelectActivity.this);
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

    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.d(Constant.TAG, " AudioMultiSelectActivity onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.d(Constant.TAG, " AudioMultiSelectActivity onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.d(Constant.TAG, " AudioMultiSelectActivity onDestroy");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        LogUtils.d(Constant.TAG, " AudioMultiSelectActivity onBackPressed");
    }
}
