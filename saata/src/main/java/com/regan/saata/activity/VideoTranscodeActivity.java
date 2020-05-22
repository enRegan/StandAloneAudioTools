package com.regan.saata.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.regan.saata.Constant;
import com.regan.saata.R;
import com.regan.saata.util.FileDurationUtil;
import com.regan.saata.util.LogUtils;
import com.regan.saata.util.MediaTool;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;

import nl.bravobit.ffmpeg.FFcommandExecuteResponseHandler;

public class VideoTranscodeActivity extends FuncActivity implements View.OnClickListener {
    private String mVideoPath, mAudioType, mOutPath, mOutType;
    private String mAudioName;
    private Button btnStartTranscode;
    private Button btMp3, btWav, btWma, btFlac, btAac, btM4A;
    private float mVideoTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_transcode);

        btnStartTranscode = findViewById(R.id.btn_start_transcode);
        btMp3 = findViewById(R.id.bt_mp3);
        btWav = findViewById(R.id.bt_wav);
        btWma = findViewById(R.id.bt_wma);

        if (getIntent() != null && getIntent().getExtras() != null) {
            mVideoPath = getIntent().getStringExtra("mVideoPath");
            mOutPath = getIntent().getStringExtra("mOutPath");
            mVideoTime = FileDurationUtil.getDuration(mVideoPath);
            LogUtils.d(Constant.TAG, " mVideoPath : " + mVideoPath + " mOutPath : " + mOutPath);
        }

        btMp3.setSelected(true);
        mOutType = "mp4";
        btMp3.setOnClickListener(this);
        btWav.setOnClickListener(this);
        btWma.setOnClickListener(this);
        btnStartTranscode.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //格式
            case R.id.bt_mp3:
                btMp3.setSelected(true);
                btWav.setSelected(false);
                btWma.setSelected(false);
                mOutType = "mp4";
                break;
            case R.id.bt_wav:
                btWav.setSelected(true);
                btMp3.setSelected(false);
                btWma.setSelected(false);
                mOutType = "avi";
                break;
            case R.id.bt_wma:
                btWma.setSelected(true);
                btMp3.setSelected(false);
                btWav.setSelected(false);
                mOutType = "wmv";
                break;
            case R.id.btn_start_transcode:
                startTranscode();
                break;
        }
    }

    private void startTranscode() {
        run(mVideoPath, mOutPath, mOutType, "48000", "700k", "2");
    }

    public void run(String srcPath, final String outPath, final String outType, final String hz, final String kbps, final String channel) {
        String[] cmd;
        ArrayList<String> cmdd = new ArrayList<>();
        cmdd.add("-i");
        cmdd.add(srcPath);
//        cmdd.add("-f");
//        cmdd.add(outType);
        if (!TextUtils.isEmpty(hz)) {
            cmdd.add("-ar");//-ar freq 设置音频采样率
            cmdd.add(hz);
        }
        if (!TextUtils.isEmpty(kbps)) {
            cmdd.add("-b:a");//-ab bitrate 设置音频码率
            cmdd.add(kbps);
        }
        if (!TextUtils.isEmpty(channel)) {
            cmdd.add("-ac");//设定声道数，1就是单声道，2就是立体声
            cmdd.add(channel);
        }

        cmdd.add("-b:v");//-ab bitrate 设置视频码率
        cmdd.add(kbps);

//        if (mAudioType.equals(mOutType)) {
//            cmdd.add("-codec:a");
//            cmdd.add("copy");
//        } else {
//            cmdd.add("-codec:a");
//            if ("wav".equals(outType)) {
//                cmdd.add("adpcm_ima_wav");
//            } else if ("wma".equals(outType)) {
//                cmdd.add("wmav2");
//            } else if ("m4a".equals(outType)) {
//                cmdd.add("aac");
//            } else if ("mp3".equals(outType)) {
//                cmdd.add("libmp3lame");
//            } else {
//                cmdd.add(outType);
//            }
//        }
//        else if(srcPath.substring(srcPath.lastIndexOf(".")).equals(".flac") && "mp3".equals(outType)){
//            cmdd.add("alac");
//        }
        final String outFile = outPath + "." + outType;
        cmdd.add(outFile);

        LogUtils.d(Constant.TAG, " ffmpeg: " + cmdd.toString());
        cmd = cmdd.toArray(new String[0]);
        try {
            fFtask = fFmpeg.execute(cmd, new FFcommandExecuteResponseHandler() {
                @Override
                public void onSuccess(String message) {
                    Log.d(Constant.TAG, "execute onSuccess : " + message);
                    Toast.makeText(VideoTranscodeActivity.this, "生成成功，存储在" + outFile, Toast.LENGTH_LONG).show();
                    MediaTool.insertMedia(getApplicationContext(), outFile);
//                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            Intent intent = new Intent(VideoTranscodeActivity.this, MainActivity.class);
//                            intent.putExtra("toList", true);
//                            setResult(RESULT_OK, intent);
//                            finish();
//                        }
//                    }, 2000);
                }

                @Override
                public void onProgress(String message) {
//                    if (!TextUtils.isEmpty(message) && message.contains("time=") && message.contains("bitrate")){
//                        String time = message.substring(message.lastIndexOf("time=") + 5, message.lastIndexOf("bitrate"));
//                        LogUtils.d(Constant.TAG, "onProgress : " + time);
//                        LogUtils.d(Constant.TAG, "onProgress : " + Constant.time2Float(time));
//                        LogUtils.d(Constant.TAG, "current onProgress : " + Constant.time2Float(time) / (mAudioTime));
//                        if(progressDialog != null){
//                            ProgressBar progressBar = progressDialog.findViewById(R.id.pb_progress);
//                            progressBar.setProgress(Double.valueOf(Constant.time2Float(time) / mAudioTime * 100).intValue());
//                        }
//                    }
                    if (!TextUtils.isEmpty(message) && message.contains("time=") && message.contains("bitrate")) {
                        String time = message.substring(message.lastIndexOf("time=") + 5, message.lastIndexOf("bitrate"));
//                        LogUtils.d(Constant.TAG, "onProgress : " + time);
//                        LogUtils.d(Constant.TAG, "onProgress : " + Constant.time2Float(time));
//                        LogUtils.d(Constant.TAG, "mAudioTime : " + mAudioTime);
                        float d = (Constant.time2Float(time) * 1000) / mVideoTime;
                        int progress = Double.valueOf(d * 100).intValue();
                        progressMsg = new Message();
                        progressMsg.arg1 = pMsg;
                        timerHandler.sendMessage(progressMsg);
//                        LogUtils.d(Constant.TAG, "current onProgress : " + progress);
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
                        Toast.makeText(VideoTranscodeActivity.this, "该音频不支持该参数，请修改后重试", Toast.LENGTH_SHORT).show();
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

}
