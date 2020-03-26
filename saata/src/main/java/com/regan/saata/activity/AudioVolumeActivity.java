package com.regan.saata.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;


import com.regan.saata.Constant;
import com.regan.saata.R;
import com.regan.saata.bean.AudioInfo;
import com.regan.saata.util.FileDurationUtil;
import com.regan.saata.util.LogUtils;
import com.regan.saata.util.MediaTool;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;

import nl.bravobit.ffmpeg.FFcommandExecuteResponseHandler;

public class AudioVolumeActivity extends FuncActivity implements View.OnClickListener {
    //    private FFmpeg fFmpeg;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private SeekBar seekBar;
    private ImageView ivPlus, ivReduce;
    private Button btStartVolume;
    private TextView tvVol;
    private int vol;
    private String mAudioPath, mOutPath, mOutType;
    private float mAudioTime;
    //    private ProgressDialog progressDialog;
    private int volMax = 30;
    private int volMin = -30;
    private ArrayList<AudioInfo> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_volume);

        init();
        getData();
    }

    private void init() {
        vol = 0;
        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);
        seekBar = findViewById(R.id.seekbar);
        ivPlus = findViewById(R.id.iv_plus);
        ivReduce = findViewById(R.id.iv_reduce);
        btStartVolume = findViewById(R.id.btn_start_volume);
        tvVol = findViewById(R.id.tv_vol);
        tvVol.setText(getVol(vol));

        ivPlus.setOnClickListener(this);
        ivReduce.setOnClickListener(this);
        btStartVolume.setOnClickListener(this);

        showBack(ivBack, true);
        tvTitle.setText("音量调整");
//        fFmpeg = FFmpeg.getInstance(this);
//        fFmpeg.setTimeout(Constant.FFMPEG_TIMEOUT);

        seekBar.setPadding(0, 0, 0, 0);
        seekBar.setThumbOffset(0);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                LogUtils.d(Constant.TAG, " onProgressChanged progress : " + progress);
                vol = progress - 30;
//                LogUtils.d(Constant.TAG, " onProgressChanged vol : " + vol);
                tvVol.setText(getVol(vol));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

//        if(getIntent() != null && getIntent().getExtras() != null){
//            mAudioPath = getIntent().getStringExtra("mAudioPath");
//            mOutPath = getIntent().getStringExtra("mOutPath");
//            mOutType = getIntent().getStringExtra("mOutType");
//            LogUtils.d(Constant.TAG, " mAudioPath : " + mAudioPath + " mOutPath : " + mOutPath + " mOutType : " + mOutType);
//        }
//        getLength();
    }

    private void getData() {
        startActivityForResult(new Intent(AudioVolumeActivity.this, AudioSingleSelectActivity.class), MainActivity.CODE_TO_FUNC);
    }

    public void run(String srcPath, String outPath, String dB, String outType) {
        String[] cmd;
        ArrayList<String> cmdd = new ArrayList<>();

        //调整volume
        cmdd.add("-i");
        cmdd.add(srcPath);
        cmdd.add("-filter:a");
        cmdd.add("volume=" + dB);
//        final String outFile = outPath + "-" + dB + outType;
        final String outFile = outPath + outType;
        cmdd.add(outFile);

        cmd = cmdd.toArray(new String[0]);
        try {
            fFtask = fFmpeg.execute(cmd, new FFcommandExecuteResponseHandler() {
                @Override
                public void onSuccess(String message) {
                    LogUtils.d(Constant.TAG, "execute onSuccess : " + message);
                    Toast.makeText(AudioVolumeActivity.this, "生成成功", Toast.LENGTH_SHORT).show();
                    MediaTool.insertMedia(AudioVolumeActivity.this, outFile);
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(AudioVolumeActivity.this, MainActivity.class);
                            intent.putExtra("toList", true);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }, 2000);
                }

                @Override
                public void onProgress(String message) {
//                    LogUtils.d(Constant.TAG, "execute onProgress : " + message);
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
                        float d = (Constant.time2Float(time) * 1000) / mAudioTime;
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
                    LogUtils.d(Constant.TAG, "execute onFailure : " + message);
                    File file = new File(outFile);
                    if (file.exists()) {
                        file.delete();
                    }
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(AudioVolumeActivity.this, "生成出现错误，请稍后重试", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onStart() {
                    LogUtils.d(Constant.TAG, "execute onStart : ");
                    btStartVolume.setEnabled(false);
//                    Toast.makeText(AudioVolumeActivity.this, "正在生成请稍等", Toast.LENGTH_SHORT).show();
//                    progressDialog = new ProgressDialog(AudioVolumeActivity.this).getProgressDialog();
                    progressDialog.show();
                    if (progressDialog != null) {
                        ProgressBar progressBar = progressDialog.findViewById(R.id.pb_progress);
                        progressBar.setProgress(0);
                    }
                    fFmpegTimer = new Timer();
                    fFmpegTimerTask = new MTimerTask();
                    fFmpegTimer.schedule(fFmpegTimerTask, 0, 1000);
                }

                @Override
                public void onFinish() {
                    LogUtils.d(Constant.TAG, "execute onFinish : ");
                    btStartVolume.setEnabled(true);
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

    private String getVol(int vol) {
        String volString = "";
        if (vol < 0) {
            volString = vol + "dB";
        } else {
            volString = "+" + vol + "dB";
        }
        return volString;
    }

//    private void getLength(){
//        ArrayList<String> cmdd = new ArrayList<>();
//        cmdd.add("-i");
//        cmdd.add(mAudioPath);
//        cmdd.add("-filter:a");
//        cmdd.add("volumedetect");
//        cmdd.add("-f");
//        cmdd.add("null");
//        cmdd.add("/dev/null");
//        final String[] cmd = cmdd.toArray(new String[0]);
//        try {
//            fFmpeg.killRunningProcesses();
//            fFmpeg.execute(cmd, new FFmpegExecuteResponseHandler() {
//                @Override
//                public void onSuccess(String message) {
//                    if(message.contains("Duration:")){
//                        String time = message.substring(message.indexOf("Duration: ") + 9, message.indexOf("Duration: ") + 21);
//                        mAudioTime = Constant.time2Float(time);
//                        LogUtils.d(Constant.TAG, "current mAudioTime : " + mAudioTime);
//                    }
//                }
//
//                @Override
//                public void onProgress(String message) {
//
//                }
//
//                @Override
//                public void onFailure(String message) {
//
//                }
//
//                @Override
//                public void onStart() {
//
//                }
//
//                @Override
//                public void onFinish() {
//
//                }
//            });
//        } catch (FFmpegCommandAlreadyRunningException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_plus:
                vol++;
                if (vol >= volMax) {
                    vol = volMax;
                }
                tvVol.setText(getVol(vol));
                seekBar.setProgress(vol + 30);
                break;
            case R.id.iv_reduce:
                vol--;
                if (vol <= volMin) {
                    vol = volMin;
                }
                tvVol.setText(getVol(vol));
                seekBar.setProgress(vol + 30);
                break;
            case R.id.btn_start_volume:
                startVolume();
                break;
        }
    }

    private void startVolume() {
        run(mAudioPath, mOutPath, tvVol.getText().toString(), mOutType);
    }

    @Override
    protected void stopFFmpeg() {
        super.stopFFmpeg();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intentData) {
        super.onActivityResult(requestCode, resultCode, intentData);
        LogUtils.d(Constant.TAG, " AudioMergeActivity onActivityResult : " + requestCode);
        if (requestCode == MainActivity.CODE_TO_FUNC && resultCode == RESULT_OK && null != intentData) {
            if (intentData.getExtras() != null) {
                data = (ArrayList<AudioInfo>) intentData.getSerializableExtra("data");
                mAudioPath = data.get(0).getPath();
//                mOutPath = Constant.getMusicPath() + data.get(0).getName().substring(0, data.get(0).getName().lastIndexOf("."));
                mOutPath = Constant.getMusicPath() + "音量调整_" + System.currentTimeMillis();
                mOutType = data.get(0).getName().substring(data.get(0).getName().lastIndexOf("."));
                mAudioTime = FileDurationUtil.getDuration(mAudioPath);
//                LogUtils.d(Constant.TAG, "AudioMergeActivity data" + data.size());
            } else {
                finish();
            }
        } else {
            finish();
        }
    }
}
