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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.regan.saata.util.TimeUtils;
import com.regan.saata.view.MyCutSeekBar;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;

import nl.bravobit.ffmpeg.FFcommandExecuteResponseHandler;

public class AudioCut2Activity extends FuncActivity implements View.OnClickListener {
    //    private FFmpeg fFmpeg;
    private TextView tvSelected, tvTotal, tvStart, tvStop;
    private Button btStartCut;
    private String mAudioPath, mOutPath, mOutType;
    private ImageView ivStartPlus, ivStartReduce, ivStopPlus, ivStopReduce, ivPlay;// ivStop;
    private int startTime, stopTime, selectedTime;
    private MediaPlayer mediaPlayer;
    private int mAudioTime;
//    private ProgressDialog progressDialog;

    private ArrayList<AudioInfo> data;
    private boolean isPlay = false;

    private MyCutSeekBar myCutSeekBarPressures;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_cut2);

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
        myCutSeekBarPressures = findViewById(R.id.seekBar_tg2);

        ivStartPlus = findViewById(R.id.iv_start_plus);
        ivStartReduce = findViewById(R.id.iv_start_reduce);
        ivStopPlus = findViewById(R.id.iv_stop_plus);
        ivStopReduce = findViewById(R.id.iv_stop_reduce);
        ivPlay = findViewById(R.id.iv_play);
//        ivStop = findViewById(R.id.iv_stop);
        btStartCut = findViewById(R.id.btn_start_cut);

        DisplayMetrics dm = new DisplayMetrics();
        dm = getResources().getDisplayMetrics();
        int screenX = dm.widthPixels;

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

        myCutSeekBarPressures.setOnSeekBarChangeListener(new MyCutSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressBefore() {

            }

            @Override
            public void onProgressChanged(MyCutSeekBar seekBar, double progressLow, double progressHigh) {
                startTime = (int) ((progressLow / 1000) * mAudioTime);
                stopTime = (int) ((progressHigh / 1000) * mAudioTime);
                tvStart.setText(TimeUtils.secondToTime(startTime));
                tvStop.setText(TimeUtils.secondToTime(stopTime));
                toSelected(startTime, stopTime);
            }

            @Override
            public void onProgressAfter() {

            }
        });
    }

    private void getData() {
        startActivityForResult(new Intent(AudioCut2Activity.this, AudioSingleSelectActivity.class), MainActivity.CODE_TO_FUNC);
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
                if (startTime >= stopTime) {
                    startTime--;
                    return;
                }
                tvStart.setText(TimeUtils.secondToTime(startTime));
                toSelected(startTime, stopTime);
                myCutSeekBarPressures.setProgressLow((float) startTime * 1000 / mAudioTime);
                break;
            case R.id.iv_start_reduce:
                startTime--;
                if (startTime < 0) {
                    startTime = 0;
                    return;
                }
                tvStart.setText(TimeUtils.secondToTime(startTime));
                toSelected(startTime, stopTime);
                myCutSeekBarPressures.setProgressLow((float) startTime * 1000 / mAudioTime);
                break;
            case R.id.iv_stop_plus:
                stopTime++;
                if (stopTime > mAudioTime) {
                    stopTime--;
                    return;
                }
                tvStop.setText(TimeUtils.secondToTime(stopTime));
                toSelected(startTime, stopTime);
                myCutSeekBarPressures.setProgressHigh((float) stopTime * 1000 / mAudioTime);
                break;
            case R.id.iv_stop_reduce:
                stopTime--;
                if (stopTime <= 0) {
                    stopTime = 0;
                }
                if (stopTime <= startTime) {
                    stopTime++;
                    return;
                }
                tvStop.setText(TimeUtils.secondToTime(stopTime));
                toSelected(startTime, stopTime);
                myCutSeekBarPressures.setProgressHigh((float) stopTime * 1000 / mAudioTime);
                break;
            case R.id.btn_start_cut:
                startCut();
                break;
        }
    }

    private void startCut() {
        loadingDialog.show();
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
                    Toast.makeText(AudioCut2Activity.this, "生成成功", Toast.LENGTH_SHORT).show();
                    MediaTool.insertMedia(AudioCut2Activity.this, outFile);
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(AudioCut2Activity.this, MainActivity.class);
                            intent.putExtra("toList", true);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }, 2000);
                }

                @Override
                public void onProgress(String message) {
                    if (!TextUtils.isEmpty(message) && message.contains("time=") && message.contains("bitrate")) {
                        String time = message.substring(message.lastIndexOf("time=") + 5, message.lastIndexOf("bitrate"));
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
                    Toast.makeText(AudioCut2Activity.this, "生成出现错误，请稍后重试", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onStart() {
                    LogUtils.d(Constant.TAG, "execute onStart : ");
                    btStartCut.setEnabled(false);
//                    Toast.makeText(AudioCut2Activity.this, "正在生成请稍等", Toast.LENGTH_SHORT).show();
//                    progressDialog = new ProgressDialog(AudioCut2Activity.this).getProgressDialog();
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
                mAudioTime = FileDurationUtil.getDuration(mAudioPath) / 1000;
                if (mAudioTime < 15) {
                    stopTime = mAudioTime;
                } else {
                    stopTime = 15;
                }
                tvStop.setText(TimeUtils.secondToTime(stopTime));
                toSelected(startTime, stopTime);
                tvTotal.setText(TimeUtils.secondToTime(mAudioTime));
//                LogUtils.d(Constant.TAG, "AudioMergeActivity data" + data.size());
                myCutSeekBarPressures.setProgressLow(startTime);
                myCutSeekBarPressures.setProgressHigh((float) stopTime * 1000 / mAudioTime);
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
