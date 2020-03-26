package com.regan.saata.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;


import com.regan.saata.Constant;
import com.regan.saata.R;
import com.regan.saata.bean.AudioInfo;
import com.regan.saata.util.FileDurationUtil;
import com.regan.saata.util.LogUtils;
import com.regan.saata.util.MediaTool;
import com.regan.saata.util.PxUtils;
import com.regan.saata.util.TimeUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;

import nl.bravobit.ffmpeg.FFcommandExecuteResponseHandler;


public class AudioCutActivity extends FuncActivity implements View.OnClickListener {
    //    private FFmpeg fFmpeg;
    private TextView tvSelected, tvTotal, tvStart, tvStop;
    private ImageView ivStartPlus, ivStartReduce, ivStopPlus, ivStopReduce, ivPlay;// ivStop;
    private Button btStartCut;
    private String mAudioPath, mOutPath, mOutType;
    private int startTime, stopTime, selectedTime;
    private MediaPlayer mediaPlayer;
    private int mAudioTime;
    private View vLeftBg, vRightBg;
    private ImageView ivLeftLine, ivRightLine, ivCut;
//    private ProgressDialog progressDialog;

    private ArrayList<AudioInfo> data;
    private boolean isPlay = false;

    private float downX;
    private float downY;
    private int parentXLeft;
    private int parentXRight;
    private int parentX;
    private int cutCurrentLeft;
    private int cutCurrentRight;
    private int lineWidth;
    private int oneSecondeWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_cut);

        init();
        getData();
    }


    @SuppressLint("ClickableViewAccessibility")
    private void init() {
        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);
        tvSelected = findViewById(R.id.tv_selected);
        tvTotal = findViewById(R.id.tv_total);
        tvStart = findViewById(R.id.tv_start);
        tvStop = findViewById(R.id.tv_stop);

        ivStartPlus = findViewById(R.id.iv_start_plus);
        ivStartReduce = findViewById(R.id.iv_start_reduce);
        ivStopPlus = findViewById(R.id.iv_stop_plus);
        ivStopReduce = findViewById(R.id.iv_stop_reduce);
        ivPlay = findViewById(R.id.iv_play);
//        ivStop = findViewById(R.id.iv_stop);
        btStartCut = findViewById(R.id.btn_start_cut);

        vLeftBg = findViewById(R.id.v_left_bg);
        vRightBg = findViewById(R.id.v_right_bg);
        ivLeftLine = findViewById(R.id.iv_left_line);
        ivRightLine = findViewById(R.id.iv_right_line);
        ivCut = findViewById(R.id.iv_cut);

        DisplayMetrics dm = new DisplayMetrics();
        dm = getResources().getDisplayMetrics();
        int screenX = dm.widthPixels;
        parentXLeft = PxUtils.dp2px(this, 15);
        parentXRight = screenX - PxUtils.dp2px(this, 15);
        parentX = parentXRight - parentXLeft;
        lineWidth = PxUtils.dp2px(this, 10);
        LogUtils.d(Constant.TAG, " parentXLeft : " + parentXLeft);
        LogUtils.d(Constant.TAG, " lineWidth : " + lineWidth);

        ivLeftLine.setOnTouchListener(new View.OnTouchListener() {
            int oldCutX = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downX = event.getRawX();
                        downY = event.getRawY();
                        oldCutX = ivCut.getLayoutParams().width;
//                        cutCurrentLeft = (int)downX;
                        LogUtils.d(Constant.TAG, "ivLeftLine downX: " + downX + " downY: " + downY);
                        LogUtils.d(Constant.TAG, "ivCut oldCutX : " + oldCutX);
                        LogUtils.d(Constant.TAG, "vLeftBg oldCutX : " + vLeftBg.getLayoutParams().width);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (event.getRawX() <= (parentXLeft + lineWidth)) {
                            return true;
                        }
                        if (event.getRawX() >= (cutCurrentRight - lineWidth)) {
                            return true;
                        }

//                        LogUtils.d(Constant.TAG, "ivLeftLine ACTION_MOVE x: " + event.getRawX() + " y: " + event.getRawY());
                        LinearLayout.LayoutParams leftBgParams = new LinearLayout.LayoutParams(PxUtils.dp2px(AudioCutActivity.this, 10), ViewGroup.LayoutParams.MATCH_PARENT);
                        leftBgParams.width = (int) event.getRawX() - parentXLeft - lineWidth;
                        LogUtils.d(Constant.TAG, "vLeftBg leftBgParams.width : " + leftBgParams.width);
                        vLeftBg.setLayoutParams(leftBgParams);

                        LinearLayout.LayoutParams cutParams = new LinearLayout.LayoutParams(PxUtils.dp2px(AudioCutActivity.this, 10), ViewGroup.LayoutParams.MATCH_PARENT);
//                        cutParams.width = oldCutX - ((int)event.getRawX() - (int)downX);// - lineWidth/2;
                        if (cutCurrentLeft == parentXLeft) {
                            cutParams.width = cutCurrentRight - leftBgParams.width - parentXLeft - lineWidth;// - lineWidth/2;
                        } else {
                            cutParams.width = cutCurrentRight - lineWidth - leftBgParams.width - parentXLeft - lineWidth;// - lineWidth/2;
                        }
                        if (cutParams.width <= 0) {
                            cutParams.width = 0;
                        }
                        if (event.getRawX() >= (cutCurrentRight - lineWidth)) {
                            cutParams.width = 0;
                        }
                        if (cutParams.width <= 0) {
                            cutParams.width = 0;
                        }
                        LogUtils.d(Constant.TAG, " cutParams.width : " + cutParams.width);
                        ivCut.setLayoutParams(cutParams);

                        float x = event.getRawX() - parentXLeft;
                        float bili = ((x / parentX) * mAudioTime) / 1000;
                        startTime = (int) bili;
                        tvStart.setText(TimeUtils.secondToTime(startTime));
                        toSelected(startTime, stopTime);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        cutCurrentLeft = (int) event.getRawX();
                        LogUtils.d(Constant.TAG, " cutCurrentLeft : " + cutCurrentLeft);
                        break;
                }
                return true;
            }
        });
        ivRightLine.setOnTouchListener(new View.OnTouchListener() {
            int oldCutX = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downX = event.getRawX();
                        downY = event.getRawY();
                        oldCutX = ivCut.getLayoutParams().width;
                        LogUtils.d(Constant.TAG, "ivRightLine downX: " + downX + " downY: " + downY);
                        LogUtils.d(Constant.TAG, "ivCut oldCutX : " + oldCutX);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (event.getRawX() >= (parentXRight - lineWidth / 2)) {
                            return true;
                        }
//                        LogUtils.d(Constant.TAG, "ivRightLine ACTION_MOVE x: " + event.getRawX() + " y: " + event.getRawY());

                        if (event.getRawX() <= (cutCurrentLeft + lineWidth)) {
                            return true;
                        }

                        LinearLayout.LayoutParams rightBgParams = new LinearLayout.LayoutParams(PxUtils.dp2px(AudioCutActivity.this, 10), ViewGroup.LayoutParams.MATCH_PARENT);
                        rightBgParams.width = parentXRight - (int) event.getRawX();
                        if (rightBgParams.width <= 0) {
                            rightBgParams.width = 0;
                        }
                        vRightBg.setLayoutParams(rightBgParams);

                        LinearLayout.LayoutParams cutParams = new LinearLayout.LayoutParams(PxUtils.dp2px(AudioCutActivity.this, 10), ViewGroup.LayoutParams.MATCH_PARENT);
//                        cutParams.width = oldCutX + (int)(event.getRawX() - downX - (downX - cutCurrentLeft));// - (lineWidth / 2);
                        cutParams.width = parentXRight - rightBgParams.width - lineWidth - cutCurrentLeft;// - (lineWidth / 2);
                        if (cutParams.width >= parentXRight) {
                            cutParams.width = parentXRight;
                        }
                        if (event.getRawX() <= (cutCurrentLeft + lineWidth)) {
                            cutParams.width = 0;
                        }
                        if (cutParams.width <= 0) {
                            cutParams.width = 0;
                        }
                        LogUtils.d(Constant.TAG, " cutParams.width : " + cutParams.width);
                        ivCut.setLayoutParams(cutParams);

//                        LinearLayout.LayoutParams rightBgParams = new LinearLayout.LayoutParams(PxUtils.dp2px(AudioCutActivity.this, 10), ViewGroup.LayoutParams.MATCH_PARENT);
//                        rightBgParams.width = parentXRight - cutParams.width;
//                        if(rightBgParams.width <= 0){
//                            rightBgParams.width = 0;
//                        }
//                        vRightBg.setLayoutParams(rightBgParams);
//                        if(event.getRawX() <= (cutCurrentLeft + lineWidth)){
//                            return true;
//                        }
                        float x = event.getRawX() - parentXLeft;
                        float bili = ((x / parentX) * mAudioTime) / 1000;
                        stopTime = (int) bili;
                        tvStop.setText(TimeUtils.secondToTime(stopTime));
                        toSelected(startTime, stopTime);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        cutCurrentRight = (int) event.getRawX();
                        LogUtils.d(Constant.TAG, " cutCurrentRight : " + cutCurrentRight);
                        break;
                }
                return true;
            }
        });
        ivPlay.setOnClickListener(this);
//        ivStop.setOnClickListener(this);
        ivStartPlus.setOnClickListener(this);
        ivStartReduce.setOnClickListener(this);
        ivStopPlus.setOnClickListener(this);
        ivStopReduce.setOnClickListener(this);
        btStartCut.setOnClickListener(this);

        showBack(ivBack, true);
        tvTitle.setText("音频裁剪");

//        if(getIntent() != null && getIntent().getExtras() != null){
//            mAudioPath = getIntent().getStringExtra("mAudioPath");
//            mOutPath = getIntent().getStringExtra("mOutPath");
//            mOutType = getIntent().getStringExtra("mOutType");
//            LogUtils.d(Constant.TAG, " mAudioPath : " + mAudioPath + " mOutPath : " + mOutPath + " mOutType : " + mOutType);
//        }

        startTime = 0;
        stopTime = 15;
        toSelected(startTime, stopTime);
        tvStart.setText(TimeUtils.secondToTime(startTime));
        tvStop.setText(TimeUtils.secondToTime(stopTime));

//        fFmpeg = FFmpeg.getInstance(this);
//        fFmpeg.setTimeout(Constant.FFMPEG_TIMEOUT);
//        getLength();
        mediaPlayer = new MediaPlayer();
    }

    private void getData() {
        startActivityForResult(new Intent(AudioCutActivity.this, AudioSingleSelectActivity.class), MainActivity.CODE_TO_FUNC);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_play:
                if (isPlay) {
                    isPlay = false;
                    ivPlay.setImageResource(R.drawable.cut_play);
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }
                } else {
                    isPlay = true;
                    ivPlay.setImageResource(R.drawable.cut_stop);
                    try {
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(mAudioPath);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        mediaPlayer.pause();
                        mediaPlayer.seekTo(startTime * 1000);
                        mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                            @Override
                            public void onSeekComplete(MediaPlayer mp) {
                                mp.start();
                            }
                        });
//                    mediaPlayer.start();
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (mediaPlayer != null) {
                                    mediaPlayer.stop();
                                    isPlay = false;
                                    ivPlay.setImageResource(R.drawable.cut_play);
                                }
                            }
                        }, selectedTime * 1000);
                        LogUtils.d(Constant.TAG, " selectedTime * 1000  " + selectedTime * 1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
//            case R.id.iv_stop:
//                if(mediaPlayer != null && mediaPlayer.isPlaying()){
//                    mediaPlayer.stop();
//                }
//                break;
            case R.id.iv_start_plus:
                startTime++;
                if (startTime >= stopTime - 3) {
                    startTime--;
                    return;
                }
                tvStart.setText(TimeUtils.secondToTime(startTime));
                toSelected(startTime, stopTime);
                LinearLayout.LayoutParams leftBgPlusParams = new LinearLayout.LayoutParams(PxUtils.dp2px(AudioCutActivity.this, 10), ViewGroup.LayoutParams.MATCH_PARENT);
                leftBgPlusParams.width = vLeftBg.getLayoutParams().width + oneSecondeWidth;
                vLeftBg.setLayoutParams(leftBgPlusParams);
                LinearLayout.LayoutParams cutPlusParams = new LinearLayout.LayoutParams(PxUtils.dp2px(AudioCutActivity.this, 10), ViewGroup.LayoutParams.MATCH_PARENT);
                cutPlusParams.width = ivCut.getLayoutParams().width - oneSecondeWidth;
                ivCut.setLayoutParams(cutPlusParams);
                break;
            case R.id.iv_start_reduce:
                startTime--;
                if (startTime <= 0) {
                    startTime = 0;
                    return;
                }
                tvStart.setText(TimeUtils.secondToTime(startTime));
                toSelected(startTime, stopTime);
                LinearLayout.LayoutParams leftBgReduceParams = new LinearLayout.LayoutParams(PxUtils.dp2px(AudioCutActivity.this, 10), ViewGroup.LayoutParams.MATCH_PARENT);
                leftBgReduceParams.width = vLeftBg.getLayoutParams().width - oneSecondeWidth;
                vLeftBg.setLayoutParams(leftBgReduceParams);
                LinearLayout.LayoutParams cutReduceParams = new LinearLayout.LayoutParams(PxUtils.dp2px(AudioCutActivity.this, 10), ViewGroup.LayoutParams.MATCH_PARENT);
                cutReduceParams.width = ivCut.getLayoutParams().width + oneSecondeWidth;
                ivCut.setLayoutParams(cutReduceParams);
                break;
            case R.id.iv_stop_plus:
                stopTime++;
                if (stopTime > (mAudioTime / 1000)) {
                    stopTime--;
                    return;
                }
                tvStop.setText(TimeUtils.secondToTime(stopTime));
                toSelected(startTime, stopTime);
                LinearLayout.LayoutParams rightBgPlusParams = new LinearLayout.LayoutParams(PxUtils.dp2px(AudioCutActivity.this, 10), ViewGroup.LayoutParams.MATCH_PARENT);
                rightBgPlusParams.width = vRightBg.getLayoutParams().width - oneSecondeWidth;
                vRightBg.setLayoutParams(rightBgPlusParams);
                LinearLayout.LayoutParams cutRightPlusParams = new LinearLayout.LayoutParams(PxUtils.dp2px(AudioCutActivity.this, 10), ViewGroup.LayoutParams.MATCH_PARENT);
                cutRightPlusParams.width = ivCut.getLayoutParams().width + oneSecondeWidth;
                ivCut.setLayoutParams(cutRightPlusParams);
                break;
            case R.id.iv_stop_reduce:
                stopTime--;
                if (stopTime <= 0) {
                    stopTime = 0;
                }
                if (stopTime <= startTime + 3) {
                    stopTime++;
                    return;
                }
                tvStop.setText(TimeUtils.secondToTime(stopTime));
                toSelected(startTime, stopTime);
                LinearLayout.LayoutParams rightBgReduceParams = new LinearLayout.LayoutParams(PxUtils.dp2px(AudioCutActivity.this, 10), ViewGroup.LayoutParams.MATCH_PARENT);
                rightBgReduceParams.width = vRightBg.getLayoutParams().width + oneSecondeWidth;
                vRightBg.setLayoutParams(rightBgReduceParams);
                LinearLayout.LayoutParams cutRightReduceParams = new LinearLayout.LayoutParams(PxUtils.dp2px(AudioCutActivity.this, 10), ViewGroup.LayoutParams.MATCH_PARENT);
                cutRightReduceParams.width = ivCut.getLayoutParams().width - oneSecondeWidth;
                ivCut.setLayoutParams(cutRightReduceParams);
                break;
            case R.id.btn_start_cut:
                startCut();
                break;
        }
    }

    private void startCut() {
        run(mAudioPath, mOutPath, tvStart.getText().toString(), tvSelected.getText().toString(), mOutType);
    }

    private void toSelected(int startTime, int stopTime) {
        selectedTime = stopTime - startTime;
        tvSelected.setText(TimeUtils.secondToTime(selectedTime));
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
//                        mAudioTime = Constant.time2Int(time);
//                        tvTotal.setText(TimeUtils.secondToTime(mAudioTime));
//                        LogUtils.d(Constant.TAG, "current mAudioTime : " + mAudioTime);
//                    }
//                }
//
//                @Override
//                public void onProgress(String message) {
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

    public void run(String srcPath, String outPath, String startTime, String stopTime, String outType) {
        String[] cmd;
        ArrayList<String> cmdd = new ArrayList<>();
        //剪切
//        cmdd.add("-i");
//        cmdd.add(srcPath);
//        cmdd.add("-ss");
//        cmdd.add("00:10.00");
//        cmdd.add("-vn");
//        cmdd.add("-acodec");
//        cmdd.add("copy");
//        cmdd.add("-t");
//        cmdd.add("00:31.25");
//        cmdd.add(outPath);

        //剪切
//        cmdd.add("-ss");
//        cmdd.add(startTime);
//        cmdd.add("-i");
//        cmdd.add(srcPath);
//        cmdd.add("-vn");
////        cmdd.add("-acodec");
////        cmdd.add("copy");
//        cmdd.add("-to");
//        cmdd.add(stopTime);
//        cmdd.add("-c");
//        cmdd.add("copy");

        cmdd.add("-y");
        cmdd.add("-vn");
        cmdd.add("-ss");
        cmdd.add(startTime);
//        cmdd.add("-acodec");
//        cmdd.add("copy");
        cmdd.add("-t");
        cmdd.add(stopTime);
        cmdd.add("-accurate_seek");
        cmdd.add("-i");
        cmdd.add(srcPath);
//        cmdd.add("-acodec");
//        cmdd.add("copy");
//        cmdd.add("-avoid_negative_ts");
//        cmdd.add("1");
//        final String outFile = outPath + "-" +startTime + "-" + stopTime + outType;
        final String outFile = outPath + outType;
        cmdd.add(outFile);

        cmd = cmdd.toArray(new String[0]);
        LogUtils.d(Constant.TAG, " cut : " + cmdd.toString());
        try {
            fFtask = fFmpeg.execute(cmd, new FFcommandExecuteResponseHandler() {
                @Override
                public void onSuccess(String message) {
                    LogUtils.d(Constant.TAG, "execute onSuccess : " + message);
                    Toast.makeText(AudioCutActivity.this, "生成成功", Toast.LENGTH_SHORT).show();
                    MediaTool.insertMedia(AudioCutActivity.this, outFile);
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(AudioCutActivity.this, MainActivity.class);
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
//                        LogUtils.d(Constant.TAG, "selectedTime : " + (selectedTime * 1000));
                        float d = (Constant.time2Float(time) * 1000) / (selectedTime * 1000);
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
                    Toast.makeText(AudioCutActivity.this, "生成出现错误，请稍后重试", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onStart() {
                    LogUtils.d(Constant.TAG, "execute onStart : ");
                    btStartCut.setEnabled(false);
//                    Toast.makeText(AudioCutActivity.this, "正在生成请稍等", Toast.LENGTH_SHORT).show();
//                    progressDialog = new ProgressDialog(AudioCutActivity.this).getProgressDialog();
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
                    btStartCut.setEnabled(true);
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
                mOutPath = Constant.getMusicPath() + "音频裁剪_" + System.currentTimeMillis();
                mOutType = data.get(0).getName().substring(data.get(0).getName().lastIndexOf("."));
                mAudioTime = FileDurationUtil.getDuration(mAudioPath);
                if (mAudioTime < 15 * 1000) {
                    stopTime = (mAudioTime / 1000);
                    tvStop.setText(TimeUtils.secondToTime(stopTime));
                    toSelected(startTime, stopTime);
                }
                tvTotal.setText(TimeUtils.secondToTime(mAudioTime / 1000));
//                LogUtils.d(Constant.TAG, "AudioMergeActivity data" + data.size());

                LinearLayout.LayoutParams leftBgParams = new LinearLayout.LayoutParams(PxUtils.dp2px(AudioCutActivity.this, 10), ViewGroup.LayoutParams.MATCH_PARENT);
                leftBgParams.width = 0;
                if (leftBgParams.width <= 0) {
                    leftBgParams.width = 0;
                }
                vLeftBg.setLayoutParams(leftBgParams);

                LinearLayout.LayoutParams cutParams = new LinearLayout.LayoutParams(PxUtils.dp2px(AudioCutActivity.this, 10), ViewGroup.LayoutParams.MATCH_PARENT);
                if ((15000f / mAudioTime) >= 1f) {
                    cutParams.width = parentX - lineWidth * 2;
                } else {
                    cutParams.width = (int) ((15000f / mAudioTime) * (parentX - lineWidth * 2));
                }
                LogUtils.d(Constant.TAG, " AudioMergeActivity mAudioTime " + mAudioTime
                        + " ((15 * 1000) / mAudioTime ) : " + (15000f / mAudioTime)
                        + " parentX : " + parentX
                        + " cutParams.width : " + cutParams.width);
                if (cutParams.width <= 0) {
                    cutParams.width = 0;
                }
                ivCut.setLayoutParams(cutParams);
                cutCurrentLeft = parentXLeft;
                cutCurrentRight = parentXLeft + cutParams.width + lineWidth;

                LinearLayout.LayoutParams rightBgParams = new LinearLayout.LayoutParams(PxUtils.dp2px(AudioCutActivity.this, 10), ViewGroup.LayoutParams.MATCH_PARENT);
                rightBgParams.width = parentXRight - cutParams.width;
                if (rightBgParams.width <= 0) {
                    rightBgParams.width = 0;
                }
                vRightBg.setLayoutParams(rightBgParams);

                oneSecondeWidth = (int) ((1000f / mAudioTime) * parentX) - 3;
            } else {
                finish();
            }
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
