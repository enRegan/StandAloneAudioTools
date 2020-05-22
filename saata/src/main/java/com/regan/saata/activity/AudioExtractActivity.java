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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.regan.saata.Constant;
import com.regan.saata.R;
import com.regan.saata.bean.VideoInfo;
import com.regan.saata.util.FileDurationUtil;
import com.regan.saata.util.LogUtils;
import com.regan.saata.util.MediaTool;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;

import nl.bravobit.ffmpeg.FFcommandExecuteResponseHandler;


public class AudioExtractActivity extends FuncActivity implements View.OnClickListener {
    private Button mBtnStartExtract;
    private LinearLayout llQuality1, llQuality2, llQuality3, llQuality4;
    private Button btMp3, btWav, btWma, btFlac, btAac, btM4A;
    private String hz, kbps, channel, mOutPath, mOutType;
    private String mVideoPath;
    private float mVideoTime;
    private ArrayList<VideoInfo> data;

//    private FFmpeg fFmpeg;
//    private HttpManager httpManager;
//    private ProgressDialog progressDialog;
//    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_extract);

        init();
//        getData();
    }

    private void init() {
        llQuality1 = findViewById(R.id.ll_quality_1);
        llQuality2 = findViewById(R.id.ll_quality_2);
        llQuality3 = findViewById(R.id.ll_quality_3);
        llQuality4 = findViewById(R.id.ll_quality_4);
        btMp3 = findViewById(R.id.bt_mp3);
        btWav = findViewById(R.id.bt_wav);
        btWma = findViewById(R.id.bt_wma);
        btFlac = findViewById(R.id.bt_flac);
        btAac = findViewById(R.id.bt_aac);
        btM4A = findViewById(R.id.bt_m4a);

        llQuality1.setSelected(true);
        llQuality1.setOnClickListener(this);
        llQuality2.setOnClickListener(this);
        llQuality3.setOnClickListener(this);
        llQuality4.setOnClickListener(this);
        btMp3.setSelected(true);
        mOutType = "gif";
        btMp3.setOnClickListener(this);
        btWav.setOnClickListener(this);
        btWma.setOnClickListener(this);
        btFlac.setOnClickListener(this);
        btAac.setOnClickListener(this);
        btM4A.setOnClickListener(this);

        mBtnStartExtract = findViewById(R.id.btn_start_extract);
        mBtnStartExtract.setOnClickListener(this);
//        mEditSavePath = findViewById(R.id.edit_save_path);

        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);

        showBack(ivBack, true);
        tvTitle.setText("音频提取");

        if (getIntent() != null && getIntent().getExtras() != null) {
            mVideoPath = getIntent().getStringExtra("mVideoPath");
            mOutPath = getIntent().getStringExtra("mOutPath");
            mVideoTime = FileDurationUtil.getDuration(mVideoPath);
            LogUtils.d(Constant.TAG, " mVideoPath : " + mVideoPath + " mOutPath : " + mOutPath);
        }

        //        if(getIntent() != null && getIntent().getExtras() != null){
//            mAudioPath = getIntent().getStringExtra("mAudioPath");
//            mOutPath = getIntent().getStringExtra("mOutPath");
//            mOutType = getIntent().getStringExtra("mOutType");
//            LogUtils.d(Constant.TAG, " mAudioPath : " + mAudioPath + " mOutPath : " + mOutPath + " mOutType : " + mOutType);
//        }
//        fFmpeg = FFmpeg.getInstance(this);
//        fFmpeg.setTimeout(Constant.FFMPEG_TIMEOUT);
//        httpManager = new HttpManager(this);
//        loadingDialog = new MyLoadingDialog().getLodingDialog(AudioExtractActivity.this);
//        getLength();
    }

    private void getData() {
        startActivityForResult(new Intent(AudioExtractActivity.this, VideoSingleSelectActivity.class), MainActivity.CODE_TO_FUNC);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_quality_1:
                llQuality1.setSelected(true);
                llQuality2.setSelected(false);
                llQuality3.setSelected(false);
                llQuality4.setSelected(false);
                hz = "";
                kbps = "";
                channel = "";
                break;
            case R.id.ll_quality_2:
                llQuality1.setSelected(false);
                llQuality2.setSelected(true);
                llQuality3.setSelected(false);
                llQuality4.setSelected(false);
                hz = "44100";
                kbps = "256";
                channel = "2";
                break;
            case R.id.ll_quality_3:
                llQuality1.setSelected(false);
                llQuality2.setSelected(false);
                llQuality3.setSelected(true);
                llQuality4.setSelected(false);
                hz = "22050";
                kbps = "192";
                channel = "2";
                break;
            case R.id.ll_quality_4:
                llQuality1.setSelected(false);
                llQuality2.setSelected(false);
                llQuality3.setSelected(false);
                llQuality4.setSelected(true);
                hz = "11025";
                kbps = "128";
                channel = "2";
                break;
            case R.id.bt_mp3:
                btMp3.setSelected(true);
                btWav.setSelected(false);
                btWma.setSelected(false);
                btFlac.setSelected(false);
                btAac.setSelected(false);
                btM4A.setSelected(false);
                mOutType = "gif";
                break;
            case R.id.bt_wav:
                btWav.setSelected(true);
                btMp3.setSelected(false);
                btWma.setSelected(false);
                btFlac.setSelected(false);
                btAac.setSelected(false);
                btM4A.setSelected(false);
                mOutType = "gif";
                break;
            case R.id.bt_wma:
                btWma.setSelected(true);
                btMp3.setSelected(false);
                btWav.setSelected(false);
                btFlac.setSelected(false);
                btAac.setSelected(false);
                btM4A.setSelected(false);
                mOutType = "gif";
                break;
            case R.id.bt_flac:
                btFlac.setSelected(true);
                btMp3.setSelected(false);
                btWav.setSelected(false);
                btWma.setSelected(false);
                btAac.setSelected(false);
                btM4A.setSelected(false);
                mOutType = "gif";
                break;
            case R.id.bt_aac:
                btAac.setSelected(true);
                btMp3.setSelected(false);
                btWav.setSelected(false);
                btWma.setSelected(false);
                btFlac.setSelected(false);
                btM4A.setSelected(false);
                mOutType = "gif";
                break;
            case R.id.bt_m4a:
                btM4A.setSelected(true);
                btMp3.setSelected(false);
                btWav.setSelected(false);
                btWma.setSelected(false);
                btFlac.setSelected(false);
                btAac.setSelected(false);
                mOutType = "gif";
                break;
//            case R.id.iv_cover:
//                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(i, PICK_VIDEO_REQUEST);
//                Intent videoIntent = new Intent(Intent.ACTION_GET_CONTENT);
//                videoIntent.setType("video/*");
//                startActivityForResult(videoIntent, PICK_VIDEO_REQUEST);
//                break;
            case R.id.btn_start_extract:
//                if(!Constant.isLogin(this)){
//                    startActivity(new Intent(this, LoginActivity.class));
//                }else{
////                    mBtnStartExtract.setEnabled(false);
//                }
                startExtract();
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        startExtract();
//                    }
//                }).start();
                break;
        }
    }

    private void startExtract() {
        run(mVideoPath, mOutPath, mOutType, hz, kbps, channel);
//        FFmpegCmd.extract(mVideoPath,
//                mEditSavePath.getText().toString(),
//                (progress,timeRemaining) -> mHandler.post(() -> {
//                    mPbTranscode.setProgress(progress);
//                    mTvProgress.setText(progress + "%");
//                    int time = (int) ((System.currentTimeMillis() - startTime) / 1000);
//                    mTvTimeSpent.setText("耗时：" + Gutil.parseTime(time));
//                    mTvTimeRemaining.setText("剩余："+ Gutil.parseTime((int) (timeRemaining/1000)));
//                }));
    }

    public void run(String srcPath, final String outPath, final String outType, final String hz, final String kbps, final String channel) {
        String[] cmd;
        ArrayList<String> cmdd = new ArrayList<>();
//        cmdd.add("-encoders");
        cmdd.add("-i");
        cmdd.add(srcPath);
//        cmdd.add("-vn");
//        cmdd.add("-y");
        cmdd.add("-r");
        cmdd.add("15");
        cmdd.add("-s");
        cmdd.add("272x480");
        cmdd.add("-b:v");
        cmdd.add("200k");
//        if (!TextUtils.isEmpty(hz)) {
//            cmdd.add("-ar");//-ar freq 设置音频采样率
//            cmdd.add(hz);
//        }
//        if (!TextUtils.isEmpty(kbps)) {
//            cmdd.add("-ab");//-ab bitrate 设置音频码率
//            cmdd.add(kbps);
//        }
//        if (!TextUtils.isEmpty(channel)) {
//            cmdd.add("-ac");//设定声道数，1就是单声道，2就是立体声
//            cmdd.add(channel);
//        }
//        cmdd.add("-acodec");
//        if("wav".equals(outType) || "wma".equals(outType)){
//            cmdd.add("pcm_s16le");
//        }else if("m4a".equals(outType)){
//            cmdd.add("copy");
//        }else{
//            cmdd.add(outType);
//        }
//        cmdd.add("-codec:a");
//        if ("wav".equals(outType)) {
//            cmdd.add("adpcm_ima_wav");
//        } else if ("wma".equals(outType)) {
//            cmdd.add("wmav2");
//        } else if ("m4a".equals(outType)) {
//            cmdd.add("aac");
//        } else if ("mp3".equals(outType)) {
//            cmdd.add("libmp3lame");
//        } else {
//            cmdd.add(outType);
//        }
        final String outFile = outPath + "." + outType;
        cmdd.add(outFile);
        cmd = cmdd.toArray(new String[cmdd.size()]);
        LogUtils.d(Constant.TAG, " cut : " + cmdd.toString());
//        cmd[0] = "-i";
//        cmd[1] = srcPath;
//        cmd[2] = "-vn";
//        cmd[3] = "-y";
//        cmd[4] = "-acodec";
//        cmd[5] = "copy";
//        cmd[6] = mOutPath;
        try {
            fFtask = fFmpeg.execute(cmd, new FFcommandExecuteResponseHandler() {
                @Override
                public void onSuccess(String message) {
                    Log.d(Constant.TAG, "execute onSuccess : " + message);
                    Toast.makeText(AudioExtractActivity.this, "提取完成", Toast.LENGTH_SHORT).show();
                    MediaTool.insertMedia(getApplicationContext(), outFile);
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(AudioExtractActivity.this, MainActivity.class);
                            intent.putExtra("toList", true);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }, 2000);
                }

                @Override
                public void onProgress(String message) {
//                    if (!TextUtils.isEmpty(message) && message.contains("time=") && message.contains("bitrate")){
//                        String time = message.substring(message.lastIndexOf("time=") + 5, message.lastIndexOf("bitrate"));
//                        LogUtils.d(Constant.TAG, "onProgress : " + time);
//                        LogUtils.d(Constant.TAG, "onProgress : " + Constant.time2Float(time));
//                        LogUtils.d(Constant.TAG, "current onProgress : " + Constant.time2Float(time) / (mVideoTime));
//                        if(progressDialog != null){
//                            ProgressBar progressBar = progressDialog.findViewById(R.id.pb_progress);
//                            progressBar.setProgress(Double.valueOf(Constant.time2Float(time) / mVideoTime * 100).intValue());
//                        }
//                    }
                    if (!TextUtils.isEmpty(message) && message.contains("time=") && message.contains("bitrate")) {
                        String time = message.substring(message.lastIndexOf("time=") + 5, message.lastIndexOf("bitrate"));
//                        LogUtils.d(Constant.TAG, "onProgress : " + time);
//                        LogUtils.d(Constant.TAG, "onProgress : " + Constant.time2Float(time));
//                        LogUtils.d(Constant.TAG, "mVideoTime : " + mVideoTime);
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
                    Toast.makeText(AudioExtractActivity.this, "生成出现错误，请稍后重试", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onStart() {
                    Log.d(Constant.TAG, "execute onStart : ");
//                    mBtnStartExtract.setEnabled(false);
//                    progressDialog = new ProgressDialog(AudioExtractActivity.this).getProgressDialog();
                    progressDialog.show();
                    if (progressDialog != null) {
                        ProgressBar progressBar = progressDialog.findViewById(R.id.pb_progress);
                        progressBar.setProgress(0);
                    }
                    fFmpegTimer = new Timer();
                    fFmpegTimerTask = new MTimerTask();
                    fFmpegTimer.schedule(fFmpegTimerTask, 0, 1000);
//                    Toast.makeText(AudioExtractActivity.this, "正在提取请稍等", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFinish() {
//                    mBtnStartExtract.setEnabled(true);
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    stopMsg = new Message();
                    stopMsg.arg1 = sMsg;
                    timerHandler.sendMessage(stopMsg);
//                    mHandler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            mBtnStartExtract.setEnabled(true);
//                            Uri uri = Uri.parse(outPath + outType);
//                            Intent intent = new Intent(Intent.ACTION_VIEW);
//                            intent.setDataAndType(uri, "video/*");
//                            try {
//                                startActivity(intent);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }, 10);
                    Log.d(Constant.TAG, "execute onFinish : ");
                }
            });
        } catch (Exception e) {
            Log.d(Constant.TAG, "execute exception : " + e.getMessage());
        }
    }

//    private void getLength(){
//        ArrayList<String> cmdd = new ArrayList<>();
//        cmdd.add("-i");
//        cmdd.add(mVideoPath);
//        cmdd.add("-filter:a");
//        cmdd.add("volumedetect");
//        cmdd.add("-f");
//        cmdd.add("null");
//        cmdd.add("/dev/null");
//        final String[] cmd = cmdd.toArray(new String[0]);
//        try {
//            fFmpeg.execute(cmd, new FFmpegExecuteResponseHandler() {
//                @Override
//                public void onSuccess(String message) {
//                    if(message.contains("Duration:")){
//                        String time = message.substring(message.indexOf("Duration: ") + 9, message.indexOf("Duration: ") + 21);
//                        mVideoTime = Constant.time2Float(time);
//                        LogUtils.d(Constant.TAG, "current mVideoTime : " + mVideoTime);
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
    protected void stopFFmpeg() {
        super.stopFFmpeg();
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intentData) {
//        super.onActivityResult(requestCode, resultCode, intentData);
//        LogUtils.d(Constant.TAG, " AudioMergeActivity onActivityResult : " + requestCode);
//        if (requestCode == MainActivity.CODE_TO_FUNC && resultCode == RESULT_OK && null != intentData) {
//            if(intentData.getExtras() != null){
//                data = (ArrayList<VideoInfo>) intentData.getSerializableExtra("data");
//                mVideoPath = data.get(0).getPath();
////                mOutPath = Constant.getMusicPath() + data.get(0).getName().substring(0, data.get(0).getName().lastIndexOf("."));
//                mOutPath = Constant.getMusicPath() + "音频提取_" + System.currentTimeMillis();
//                mOutType = data.get(0).getName().substring(data.get(0).getName().lastIndexOf("."));
//                mVideoTime = FileDurationUtil.getDuration(mVideoPath);
////                LogUtils.d(Constant.TAG, "AudioMergeActivity data" + data.size());
//            }else {
//                finish();
//            }
//        }else {
//            finish();
//        }
//    }
}
