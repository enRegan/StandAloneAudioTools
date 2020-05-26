package com.regan.saata.activity;


import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
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

import androidx.annotation.Nullable;

import com.regan.saata.Constant;
import com.regan.saata.R;
import com.regan.saata.util.FileDurationUtil;
import com.regan.saata.util.LogUtils;
import com.regan.saata.util.MediaTool;
import com.regan.saata.util.TimeUtils;
import com.regan.saata.view.MySetDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;

import nl.bravobit.ffmpeg.FFcommandExecuteResponseHandler;

public class Video2GifActivity extends BaseFunctionActivity implements View.OnClickListener {
    private String mVideoPath, mOutPath, mOutType;
    private Button btnStartTranscode;
    private float mVideoTime;
    private VideoView videoView;
    private TextView tvName, tvContent;
    private ImageView ivStart, ivPreview;
    private LinearLayout llSetSpeed;
    private TextView tvSpeed;
    private float speed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video2_gif);
        tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText("视频转gif");
        btnStartTranscode = findViewById(R.id.btn_start_transcode);
        videoView = findViewById(R.id.vv_video);
        tvName = findViewById(R.id.tv_name);
        tvContent = findViewById(R.id.tv_content);
        ivPreview = findViewById(R.id.iv_preview);
        ivStart = findViewById(R.id.iv_video_start);
        llSetSpeed = findViewById(R.id.ll_set_speed);
        tvSpeed = findViewById(R.id.tv_speed);

        if (getIntent() != null && getIntent().getExtras() != null) {
            mVideoPath = getIntent().getStringExtra("mVideoPath");
            mOutPath = getIntent().getStringExtra("mOutPath");
            mOutType = getIntent().getStringExtra("mOutType");
            mVideoTime = FileDurationUtil.getDuration(mVideoPath);
            LogUtils.d(Constant.TAG, " mVideoPath : " + mVideoPath + " mOutPath : " + mOutPath);
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
        mOutType = "gif";
        speed = 1f;
        btnStartTranscode.setOnClickListener(this);
        llSetSpeed.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //格式
            case R.id.btn_start_transcode:
                startTranscode();
                break;
            case R.id.iv_video_start:
                LogUtils.d(Constant.TAG, "videoView start");
                ivPreview.setVisibility(View.GONE);
                ivStart.setVisibility(View.GONE);
                videoView.start();
                break;
            case R.id.ll_set_speed:
                LogUtils.d(Constant.TAG, "ll_set_speed");
                MySetDialog setDialog = new MySetDialog(Video2GifActivity.this, R.style.my_set_dialog);
                Window window = setDialog.getWindow();
                //设置弹出位置
                window.setGravity(Gravity.BOTTOM);
                setDialog.setCanceledOnTouchOutside(false);
                setDialog.setData(1);
                setDialog.setDialogListener(new MySetDialog.DialogListener() {
                    @Override
                    public void getResult(final String result) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!TextUtils.isEmpty(result)) {
                                    tvSpeed.setText(result);
                                    switch (result) {
                                        case "很快":
                                            speed = 0.5f;
                                            break;
                                        case "较快":
                                            speed = 0.8f;
                                            break;
                                        case "中等":
                                            speed = 1f;
                                            break;
                                        case "较慢":
                                            speed = 1.5f;
                                            break;
                                        case "很慢":
                                            speed = 2f;
                                            break;
                                    }
                                }
                            }
                        });
                    }
                });
                setDialog.show();
                /** * 设置dialog宽度全屏 */
                WindowManager m = getWindowManager();
                Display d = m.getDefaultDisplay(); //为获取屏幕宽、高
                android.view.WindowManager.LayoutParams params = setDialog.getWindow().getAttributes(); //获取对话框当前的参数值、
                params.width = (int) (d.getWidth()); //宽度设置全屏宽度
                setDialog.getWindow().setAttributes(params);

                break;
        }
    }

    private void startTranscode() {
        run(mVideoPath, mOutPath, mOutType);
    }

    public void run(String srcPath, final String outPath, final String outType) {
        String[] cmd;
        ArrayList<String> cmdd = new ArrayList<>();
//        cmdd.add("-encoders");
        cmdd.add("-i");
        cmdd.add(srcPath);
//        cmdd.add("-vn");
//        cmdd.add("-y");
        cmdd.add("-r");
        cmdd.add("15");
//        cmdd.add("-s");
//        cmdd.add("272x480");
//        cmdd.add("-b:v");
//        cmdd.add("200k");
        cmdd.add("-filter:v");
        cmdd.add("setpts=" + speed + "*PTS");
        final String outFile = outPath + "." + outType;
        cmdd.add(outFile);

        LogUtils.d(Constant.TAG, " ffmpeg: " + cmdd.toString());
        cmd = cmdd.toArray(new String[0]);
        try {
            fFtask = fFmpeg.execute(cmd, new FFcommandExecuteResponseHandler() {
                @Override
                public void onSuccess(String message) {
                    Log.d(Constant.TAG, "execute onSuccess : " + message);
                    Toast.makeText(Video2GifActivity.this, "生成成功，存储在" + outFile, Toast.LENGTH_LONG).show();
                    MediaTool.insertMedia(getApplicationContext(), outFile);
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(Video2GifActivity.this, SuccessActivity.class);
                            intent.putExtra("toList", true);
                            intent.putExtra("mVideoPath", outFile);
//                            setResult(RESULT_OK, intent);
                            startActivityForResult(intent, MainActivity.CODE_TO_FUNC);
                        }
                    }, 100);
                }

                @Override
                public void onProgress(String message) {
                    if (!TextUtils.isEmpty(message) && message.contains("time=") && message.contains("bitrate")) {
                        String time = message.substring(message.lastIndexOf("time=") + 5, message.lastIndexOf("bitrate"));
                        float d = (Constant.time2Float(time) * 1000) / mVideoTime;
                        LogUtils.d(Constant.TAG, "onProgress : " + time);
                        LogUtils.d(Constant.TAG, "onProgress : " + Constant.time2Float(time));
                        LogUtils.d(Constant.TAG, "mVideoTime : " + mVideoTime);
                        int progress = Double.valueOf(d * 100).intValue();
                        progressMsg = new Message();
                        progressMsg.arg1 = pMsg;
                        timerHandler.sendMessage(progressMsg);
                        if (progressDialog != null) {
                            ProgressBar progressBar = progressDialog.findViewById(R.id.pb_progress);
                            progressBar.setProgress(progress);
                        }
                    }
                }

                @Override
                public void onFailure(String message) {
                    Log.d(Constant.TAG, "execute onFailure : " + message);
                    File file = new File(outFile);
                    if (file.exists()) {
                        file.delete();
                    }
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    if (!isKill) {
//                        Toast.makeText(Video2GifActivity.this, "该音频不支持该参数，请修改后重试", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onStart() {
                    Log.d(Constant.TAG, "execute onStart : ");
//                    btnStartTranscode.setEnabled(false);
//                    progressDialog = new ProgressDialog(AudioTranscodeActivity.this).getProgressDialog();
                    progressDialog.show();
                    if (progressDialog != null) {
                        ProgressBar progressBar = progressDialog.findViewById(R.id.pb_progress);
                        progressBar.setProgress(0);
                    }
                    fFmpegTimer = new Timer();
                    fFmpegTimerTask = new MTimerTask();
                    fFmpegTimer.schedule(fFmpegTimerTask, 0, 1000);
//                    Toast.makeText(AudioTranscodeActivity.this, "正在转码请稍等", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFinish() {
                    Log.d(Constant.TAG, "execute onFinish : ");
//                    btnStartTranscode.setEnabled(true);
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    stopMsg = new Message();
                    stopMsg.arg1 = sMsg;
                    timerHandler.sendMessage(stopMsg);
//                    mHandler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            Uri uri = Uri.parse(mEditSavePath.getText().toString());
//                            Intent intent = new Intent(Intent.ACTION_VIEW);
//                            intent.setDataAndType(uri, "audio/*");
//                            try {
//                                startActivity(intent);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }, 10);
                }
            });
        } catch (Exception e) {
            Log.d(Constant.TAG, "execute exception : " + e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MainActivity.CODE_TO_FUNC && resultCode == RESULT_OK && null != data) {
            if (data.getExtras() != null) {
                boolean toList = data.getBooleanExtra("toList", false);
                LogUtils.d(Constant.TAG, " toList : " + toList);
                if (toList) {
                    setResult(RESULT_OK, data);
                }
            }
            finish();
        }
    }
}
