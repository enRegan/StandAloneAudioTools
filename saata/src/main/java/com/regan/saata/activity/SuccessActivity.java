package com.regan.saata.activity;

import androidx.core.content.FileProvider;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.regan.saata.Constant;
import com.regan.saata.R;
import com.regan.saata.util.FileDurationUtil;
import com.regan.saata.util.FileManager;
import com.regan.saata.util.LogUtils;
import com.regan.saata.util.TimeUtils;

import java.io.File;

public class SuccessActivity extends BaseFunctionActivity implements View.OnClickListener {
    private String mVideoPath, mOutType;
    private Button btnGoList;
    private float mVideoTime;
    //    private VideoView videoView;
    private TextView tvName, tvContent;
    private ImageView ivStart, ivPreview;
    private LinearLayout llGohome, llShare;
    private RelativeLayout rlVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);
        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText("生成完毕");
        btnGoList = findViewById(R.id.btn_go_list);
//        videoView = findViewById(R.id.vv_video);
        tvName = findViewById(R.id.tv_name);
        tvContent = findViewById(R.id.tv_content);
        ivPreview = findViewById(R.id.iv_preview);
        ivStart = findViewById(R.id.iv_video_start);
        llGohome = findViewById(R.id.ll_go_home);
        llShare = findViewById(R.id.ll_share);
        rlVideo = findViewById(R.id.rl_video);

        if (getIntent() != null && getIntent().getExtras() != null) {
            mVideoPath = getIntent().getStringExtra("mVideoPath");
            mVideoTime = FileDurationUtil.getDuration(mVideoPath);
            mOutType = getIntent().getStringExtra("mOutType");
            LogUtils.d(Constant.TAG, " mVideoPath : " + mVideoPath);
            if (mOutType.equals("mp4") || mOutType.equals("avi") || mOutType.equals("wmv") || mOutType.equals("gif")) {
                Glide.with(this).load(mVideoPath).into(ivPreview);
            } else {
                rlVideo.setBackgroundResource(R.color.transparent);
                ivPreview.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Glide.with(this).load(R.drawable.success_icon).into(ivPreview);
            }
            if (mOutType.equals("gif")) {
                ivStart.setVisibility(View.GONE);
            }
            tvName.setText(mVideoPath.substring(mVideoPath.lastIndexOf("/") + 1));
            tvContent.setText(TimeUtils.secondToTime(FileDurationUtil.getDuration(mVideoPath) / 1000));
        }
        ivStart.setOnClickListener(this);
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
//                ivPreview.setVisibility(View.GONE);
//                ivStart.setVisibility(View.GONE);
                FileManager.openFile(SuccessActivity.this, mVideoPath, mOutType);
//                videoView.start();
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
