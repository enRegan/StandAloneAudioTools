package com.regan.saata.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
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
import com.regan.saata.util.MathUtil;
import com.regan.saata.util.MediaTool;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Timer;

import nl.bravobit.ffmpeg.FFcommandExecuteResponseHandler;


public class AudioSpeedActivity extends FuncActivity implements View.OnClickListener {
    //    private FFmpeg fFmpeg;
    private SeekBar seekBar;
    private ImageView ivPlus, ivReduce;
    private Button btStartSpeed;
    private TextView tvSpeed;
    private double speed;
    private String mAudioPath, mOutPath, mOutType;
    private int mAudioTime;
    private double speedMax = 2.0D;
    private double speedMin = 0.5D;
    private ArrayList<AudioInfo> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_speed);

        init();
        getData();
    }

    private void init() {
        speed = MathUtil.round(1D, 1, BigDecimal.ROUND_HALF_UP);
        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);
        seekBar = findViewById(R.id.seekbar);
        ivPlus = findViewById(R.id.iv_plus);
        ivReduce = findViewById(R.id.iv_reduce);
        btStartSpeed = findViewById(R.id.btn_start_speed);
        tvSpeed = findViewById(R.id.tv_speed);
        tvSpeed.setText(getSpeed(speed));

        ivPlus.setOnClickListener(this);
        ivReduce.setOnClickListener(this);
        btStartSpeed.setOnClickListener(this);

        showBack(ivBack, true);
        tvTitle.setText("音频调速");
//        progressDialog = new ProgressDialog(this, fFmpeg, new ProgressDialog.FfmpegKill() {
//            @Override
//            public void kill() {
//                if (fFmpeg.isCommandRunning(fFtask)){
//                    LogUtils.d(Constant.TAG, " fFtask isCommandRunning ");
//                    fFtask.sendQuitSignal();
//                }
//            }
//        }).getProgressDialog();
//        fFmpeg = FFmpeg.getInstance(this);
//        fFmpeg.setTimeout(Constant.FFMPEG_TIMEOUT);

        seekBar.setPadding(0, 0, 0, 0);
        seekBar.setThumbOffset(0);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                LogUtils.d(Constant.TAG, " onProgressChanged progress : " + progress);
                speed = seek2Speed(progress);
//                LogUtils.d(Constant.TAG, " onProgressChanged speed : " + speed);
                tvSpeed.setText(getSpeed(speed));
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
        startActivityForResult(new Intent(AudioSpeedActivity.this, AudioSingleSelectActivity.class), MainActivity.CODE_TO_FUNC);
    }

    private String getSpeed(double speed) {
        String speedString = "";
        BigDecimal bigDecimal = new BigDecimal(speed);
        speedString = bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue() + "x";
        return speedString;
    }

    private double seek2Speed(int seek) {
        return ((double) (seek * 10 + 50) / 100);
    }

    private int speed2Seek(double speed) {
        return ((int) (MathUtil.sub(speed, 0.5D) * 10));
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
//                        LogUtils.d(Constant.TAG, "current mAudioTime / speed : " + mAudioTime / speed);
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


    public void run(String srcPath, String outPath, final double speed, String outType) {
        String[] cmd;
        ArrayList<String> cmdd = new ArrayList<>();

        //        //调整速度
//        cmdd.add("-i");
//        cmdd.add(srcPath);
//        cmdd.add("-filter:a");
//        cmdd.add("atempo=2.0");
//        cmdd.add(outPath);

        cmdd.add("-i");
        cmdd.add(srcPath);
        cmdd.add("-filter:a");
        cmdd.add("atempo=" + speed);
//        final String outFile = outPath + "-" +speed + "x" + outType;
        final String outFile = outPath + outType;
        cmdd.add(outFile);


        cmd = cmdd.toArray(new String[0]);

        runFF(cmd, outFile);

    }

    private void runFF(String[] cmd, final String outFile) {
        try {
            fFtask = fFmpeg.execute(cmd, new FFcommandExecuteResponseHandler() {
                @Override
                public void onSuccess(String message) {
                    LogUtils.d(Constant.TAG, "execute onSuccess : " + message);
                    Toast.makeText(AudioSpeedActivity.this, "生成成功", Toast.LENGTH_SHORT).show();
                    MediaTool.insertMedia(AudioSpeedActivity.this, outFile);
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(AudioSpeedActivity.this, MainActivity.class);
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
//                        LogUtils.d(Constant.TAG, "current onProgress : " + Constant.time2Float(time) / (mAudioTime / speed));
//                        if(progressDialog != null){
//                            ProgressBar progressBar = progressDialog.findViewById(R.id.pb_progress);
//                            progressBar.setProgress(Double.valueOf(Constant.time2Float(time) / (mAudioTime / speed) * 100).intValue());
//                        }
//                    }
                    if (!TextUtils.isEmpty(message) && message.contains("time=") && message.contains("bitrate")) {
                        String time = message.substring(message.lastIndexOf("time=") + 5, message.lastIndexOf("bitrate"));
//                        LogUtils.d(Constant.TAG, "onProgress : " + time);
//                        LogUtils.d(Constant.TAG, "onProgress : " + Constant.time2Float(time));
//                        LogUtils.d(Constant.TAG, "mAudioTime : " + (mAudioTime / speed));
                        float d = (Constant.time2Float(time) * 1000) / (mAudioTime / (float) speed);
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
//                    if(!message.contains("Process.exitValue()")){
//                        Toast.makeText(AudioSpeedActivity.this, "生成出现错误，请稍后重试", Toast.LENGTH_SHORT).show();
//                    }
                }

                @Override
                public void onStart() {
                    LogUtils.d(Constant.TAG, "execute onStart : ");
//                    btStartSpeed.setEnabled(false);
//                    Toast.makeText(AudioSpeedActivity.this, "正在生成请稍等", Toast.LENGTH_SHORT).show();
//                    progressDialog = new ProgressDialog(AudioSpeedActivity.this).getProgressDialog();
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
//                    btStartSpeed.setEnabled(true);
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
            LogUtils.d(Constant.TAG, "execute exception : " + e.getMessage());
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_plus:
                speed = MathUtil.sum(speed, 0.1D);
                if (speed >= speedMax) {
                    speed = speedMax;
                }
                LogUtils.d(Constant.TAG, " iv_plus speed : " + speed + " seek " + speed2Seek(speed));
                tvSpeed.setText(getSpeed(speed));
                seekBar.setProgress(speed2Seek(speed));
                break;
            case R.id.iv_reduce:
                speed = MathUtil.sub(speed, 0.1D);
                if (speed <= speedMin) {
                    speed = speedMin;
                }
                LogUtils.d(Constant.TAG, " iv_reduce speed : " + speed + " seek " + speed2Seek(speed));
                tvSpeed.setText(getSpeed(speed));
                seekBar.setProgress(speed2Seek(speed));
                break;
            case R.id.btn_start_speed:
                run(mAudioPath, mOutPath, speed, mOutType);
                break;
        }
    }

    @Override
    public void stopFFmpeg() {
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
                mOutPath = Constant.getMusicPath() + "音频调速_" + System.currentTimeMillis();
                mOutType = data.get(0).getName().substring(data.get(0).getName().lastIndexOf("."));
                mAudioTime = FileDurationUtil.getDuration(mAudioPath);
                LogUtils.d(Constant.TAG, " mAudioTime : " + mAudioTime);
                if (mAudioTime == 0) {
                    mAudioTime = Integer.valueOf(data.get(0).getTime());
                }
                LogUtils.d(Constant.TAG, " mAudioTime : " + mAudioTime);
//                LogUtils.d(Constant.TAG, "AudioMergeActivity data" + data.size());
            } else {
                finish();
            }
        } else {
            finish();
        }
    }
}
