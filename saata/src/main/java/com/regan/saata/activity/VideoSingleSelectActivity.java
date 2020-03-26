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
import com.regan.saata.adapter.VideoSingleSelectAdapter;
import com.regan.saata.bean.VideoInfo;
import com.regan.saata.util.FileManager;
import com.regan.saata.view.MyLoadingDialog;

import java.util.ArrayList;

public class VideoSingleSelectActivity extends AppCompatActivity {
    private RecyclerView rvVideo;
    private TextView tvConfirm;
    private ImageView ivBack;
    private Dialog myLoadingDialog;
    private ArrayList<VideoInfo> audioInfos;
    private VideoSingleSelectAdapter videoSingleSelectAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_single_select);
        init();
    }

    private void init() {
        rvVideo = findViewById(R.id.rv_video);
        tvConfirm = findViewById(R.id.tv_confirm);
        ivBack = findViewById(R.id.iv_back);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        audioInfos = new ArrayList<>();
        myLoadingDialog = new MyLoadingDialog().getLodingDialog(VideoSingleSelectActivity.this);
        videoSingleSelectAdapter = new VideoSingleSelectAdapter(VideoSingleSelectActivity.this, audioInfos, tvConfirm, new VideoSingleSelectAdapter.CheckDone() {
            @Override
            public void done(ArrayList<VideoInfo> data) {
//                LogUtils.d(Constant.TAG, "CheckDone data.size : " + data.size());
                Intent intent = new Intent();
                intent.putExtra("data", data);
                VideoSingleSelectActivity.this.setResult(RESULT_OK, intent);
//                mContext.startActivity(intent);
//                ((AppCompatActivity)mContext).startActivityForResult(intent, MainActivity.CODE_TO_FUNC);
                VideoSingleSelectActivity.this.finish();
            }
        });
        rvVideo.setLayoutManager(new LinearLayoutManager(VideoSingleSelectActivity.this));
        rvVideo.setAdapter(videoSingleSelectAdapter);
        myLoadingDialog.show();
//        LogUtils.d(Constant.TAG, "show");
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileManager fileManager = FileManager.getInstance(VideoSingleSelectActivity.this);
                audioInfos = fileManager.getVideo();
                new Handler(getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        videoSingleSelectAdapter.refresh(audioInfos);
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
