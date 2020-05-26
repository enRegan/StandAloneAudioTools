package com.regan.saata.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.regan.saata.Constant;
import com.regan.saata.R;
import com.regan.saata.bean.MediaInfo;
import com.regan.saata.util.FileDurationUtil;
import com.regan.saata.util.LogUtils;
import com.regan.saata.util.MediaTool;
import com.regan.saata.util.TimeUtils;
import com.regan.saata.view.MySetDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;

import nl.bravobit.ffmpeg.FFcommandExecuteResponseHandler;

public class SuccessActivity extends BaseFunctionActivity implements View.OnClickListener {
    private String mVideoPath;
    private Button btnGoList;
    private float mVideoTime;
    private VideoView videoView;
    private TextView tvName, tvContent;
    private ImageView ivStart, ivPreview;
    private LinearLayout llGohome, llShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);
        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText("生成完毕");
        btnGoList = findViewById(R.id.btn_go_list);
        videoView = findViewById(R.id.vv_video);
        tvName = findViewById(R.id.tv_name);
        tvContent = findViewById(R.id.tv_content);
        ivPreview = findViewById(R.id.iv_preview);
        ivStart = findViewById(R.id.iv_video_start);
        llGohome = findViewById(R.id.ll_go_home);
        llShare = findViewById(R.id.ll_share);

        if (getIntent() != null && getIntent().getExtras() != null) {
            mVideoPath = getIntent().getStringExtra("mVideoPath");
            mVideoTime = FileDurationUtil.getDuration(mVideoPath);
            LogUtils.d(Constant.TAG, " mVideoPath : " + mVideoPath);
            videoView.setVideoPath(mVideoPath);
            final Bitmap videoFrame = MediaTool.getVideoFrame(mVideoPath, 1);
            ivPreview.setImageBitmap(videoFrame);
            tvName.setText(mVideoPath.substring(mVideoPath.lastIndexOf("/"), mVideoPath.length()));
            tvContent.setText(TimeUtils.secondToTime(FileDurationUtil.getDuration(mVideoPath) / 1000));
            //创建MediaController对象
            MediaController mediaController = new MediaController(this);
            mediaController.setVisibility(View.INVISIBLE);
            //VideoView与MediaController建立关联
            videoView.setMediaController(mediaController);

            //让VideoView获取焦点
            videoView.requestFocus();
        }
        ivStart.setOnClickListener(this);
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                LogUtils.d(Constant.TAG, " mp " + mp.isPlaying());
                ivStart.setVisibility(View.VISIBLE);
            }
        });
        btnGoList.setOnClickListener(this);
        llGohome.setOnClickListener(this);
        llShare.setOnClickListener(this);
        ivBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //格式
            case R.id.btn_go_list:
                goList();
                break;
            case R.id.iv_video_start:
                LogUtils.d(Constant.TAG, "videoView start");
                ivPreview.setVisibility(View.GONE);
                ivStart.setVisibility(View.GONE);
                videoView.start();
                break;
            case R.id.ll_go_home:
                goHome();
                break;
            case R.id.ll_share:
                allShare(mVideoPath);
                break;
            case R.id.iv_back:
                goHome();
                break;
        }
    }

    private void goHome() {
        Intent intent = new Intent(SuccessActivity.this, MainActivity.class);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void goList() {
        Intent intent = new Intent(SuccessActivity.this, MainActivity.class);
        intent.putExtra("toList", true);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Android原生分享功能
     * 默认选取手机所有可以分享的APP
     */
    public void allShare(String path) {
        Intent share_intent = new Intent();
        share_intent.setAction(Intent.ACTION_SEND);//设置分享行为
        share_intent.setType("*/*");//设置分享内容的类型
        share_intent.putExtra(Intent.EXTRA_SUBJECT, "分享");//添加分享内容标题
        File cameraPhoto = new File(path);
        Uri photoUri = FileProvider.getUriForFile(
                this,
                getPackageName() + ".fileprovider",
                cameraPhoto);
        share_intent.putExtra(Intent.EXTRA_STREAM, photoUri);//添加分享内容
//        share_intent.putExtra(Intent.EXTRA_FROM_STORAGE, "分享给你一首歌" + name);//添加分享内容
        //创建分享的Dialog
        share_intent = Intent.createChooser(share_intent, "分享到:");
        startActivity(share_intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 是否触发按键为back键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            goHome();
            return true;
        }
        // 如果不是back键正常响应
        return super.onKeyDown(keyCode, event);
    }
}
