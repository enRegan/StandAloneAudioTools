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
import android.widget.ProgressBar;
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

import static com.regan.saata.activity.MainActivity.getDetailTime;


public class AudioTranscodeActivity extends FuncActivity implements View.OnClickListener {
    private final static int CODE_REQUEST_WRITE_EXTERNAL = 0x100;
    private int PICK_AUDIO_REQUEST = 0x2;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    private String mAudioPath, mAudioType;
    private String mAudioName;
    private Button btnStartTranscode;
    private Button btMp3, btWav, btWma, btFlac, btAac, btM4A;
    private Button bt96000, bt50400, bt48000, bt44100, bt32000, bt22050, bt11025, bt8000, bt0;
    private Button bt320, bt224, bt192, bt160, bt96, bt32;
    private Button btSource, btSTrack, btDTrack;
    private String hz, kbps, channel, mOutPath, mOutType;

    private float mAudioTime;
    private ArrayList<AudioInfo> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_transcode);

        init();
        getData();
    }

    private void init() {
        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);
        btnStartTranscode = findViewById(R.id.btn_start_transcode);

        btMp3 = findViewById(R.id.bt_mp3);
        btWav = findViewById(R.id.bt_wav);
        btWma = findViewById(R.id.bt_wma);
        btFlac = findViewById(R.id.bt_flac);
        btAac = findViewById(R.id.bt_aac);
        btM4A = findViewById(R.id.bt_m4a);

        bt0 = findViewById(R.id.bt_0);
        bt8000 = findViewById(R.id.bt_8000);
        bt11025 = findViewById(R.id.bt_11025);
        bt22050 = findViewById(R.id.bt_22050);
        bt32000 = findViewById(R.id.bt_32000);
        bt44100 = findViewById(R.id.bt_44100);
        bt48000 = findViewById(R.id.bt_48000);
        bt50400 = findViewById(R.id.bt_50400);
        bt96000 = findViewById(R.id.bt_96000);

        bt320 = findViewById(R.id.bt_320);
        bt224 = findViewById(R.id.bt_224);
        bt192 = findViewById(R.id.bt_192);
        bt160 = findViewById(R.id.bt_160);
        bt96 = findViewById(R.id.bt_96);
        bt32 = findViewById(R.id.bt_32);

        btSource = findViewById(R.id.bt_source);
        btSTrack = findViewById(R.id.bt_s_track);
        btDTrack = findViewById(R.id.bt_d_track);

        btMp3.setSelected(true);
        mOutType = "mp3";
        btMp3.setOnClickListener(this);
        btWav.setOnClickListener(this);
        btWma.setOnClickListener(this);
        btFlac.setOnClickListener(this);
        btAac.setOnClickListener(this);
        btM4A.setOnClickListener(this);

        bt44100.setSelected(true);
        hz = "44100";
        bt96000.setOnClickListener(this);
        bt50400.setOnClickListener(this);
        bt48000.setOnClickListener(this);
        bt44100.setOnClickListener(this);
        bt32000.setOnClickListener(this);
        bt22050.setOnClickListener(this);
        bt11025.setOnClickListener(this);
        bt8000.setOnClickListener(this);
        bt0.setOnClickListener(this);

        bt192.setSelected(true);
        kbps = "192k";
        bt320.setOnClickListener(this);
        bt224.setOnClickListener(this);
        bt192.setOnClickListener(this);
        bt160.setOnClickListener(this);
        bt96.setOnClickListener(this);
        bt32.setOnClickListener(this);

        btSource.setSelected(true);
        channel = "2";
        btSource.setOnClickListener(this);
        btSTrack.setOnClickListener(this);
        btDTrack.setOnClickListener(this);

        btnStartTranscode.setOnClickListener(this);

        showBack(ivBack, true);
        tvTitle.setText("音频转换");

//        final String[] audios = getResources().getStringArray(R.array.audio);
//        outAudioType = audios[1];
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_selected_preset, audios);
//        adapter.setDropDownViewResource(R.layout.item_presets);
//        mSpinnerAudio.setSelection(1);

//        if(getIntent() != null && getIntent().getExtras() != null){
//            mAudioPath = getIntent().getStringExtra("mAudioPath");
//            mOutPath = getIntent().getStringExtra("mOutPath");
//            LogUtils.d(Constant.TAG, " mAudioPath : " + mAudioPath + " mOutPath : " + mOutPath);
//        }

//        fFmpeg = FFmpeg.getInstance(this);
//        fFmpeg.setTimeout(Constant.FFMPEG_TIMEOUT);
//        loadingDialog =  new MyLoadingDialog().getLodingDialog(AudioTranscodeActivity.this);
//        getLength();
    }

    private void getData() {
        startActivityForResult(new Intent(AudioTranscodeActivity.this, AudioSingleSelectActivity.class), MainActivity.CODE_TO_FUNC);
    }

    public void run(String srcPath, final String outPath, final String mAudioType, final String outType, final String hz, final String kbps, final String channel) {
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

        if (mAudioType.equals(mOutType)) {
            cmdd.add("-codec:a");
            cmdd.add("copy");
        } else {
            cmdd.add("-codec:a");
            if ("wav".equals(outType)) {
                cmdd.add("adpcm_ima_wav");
            } else if ("wma".equals(outType)) {
                cmdd.add("wmav2");
            } else if ("m4a".equals(outType)) {
                cmdd.add("aac");
            } else if ("mp3".equals(outType)) {
                cmdd.add("libmp3lame");
            } else {
                cmdd.add(outType);
            }
        }
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
                    Toast.makeText(AudioTranscodeActivity.this, "转码成功", Toast.LENGTH_SHORT).show();
                    MediaTool.insertMedia(getApplicationContext(), outFile);
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(AudioTranscodeActivity.this, MainActivity.class);
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
                    Log.d(Constant.TAG, "execute onFailure : " + message);
                    File file = new File(outFile);
                    if (file.exists()) {
                        file.delete();
                    }
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    if (!isKill) {
                        Toast.makeText(AudioTranscodeActivity.this, "该音频不支持该参数，请修改后重试", Toast.LENGTH_SHORT).show();
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
    public void onClick(View v) {
        switch (v.getId()) {
            //格式
            case R.id.bt_mp3:
                btMp3.setSelected(true);
                btWav.setSelected(false);
                btWma.setSelected(false);
                btFlac.setSelected(false);
                btAac.setSelected(false);
                btM4A.setSelected(false);
                mOutType = "mp3";
                break;
            case R.id.bt_wav:
                btWav.setSelected(true);
                btMp3.setSelected(false);
                btWma.setSelected(false);
                btFlac.setSelected(false);
                btAac.setSelected(false);
                btM4A.setSelected(false);
                mOutType = "wav";
                break;
            case R.id.bt_wma:
                btWma.setSelected(true);
                btMp3.setSelected(false);
                btWav.setSelected(false);
                btFlac.setSelected(false);
                btAac.setSelected(false);
                btM4A.setSelected(false);
                mOutType = "wma";
                break;
            case R.id.bt_flac:
                btFlac.setSelected(true);
                btMp3.setSelected(false);
                btWav.setSelected(false);
                btWma.setSelected(false);
                btAac.setSelected(false);
                btM4A.setSelected(false);
                mOutType = "flac";
                break;
            case R.id.bt_aac:
                btAac.setSelected(true);
                btMp3.setSelected(false);
                btWav.setSelected(false);
                btWma.setSelected(false);
                btFlac.setSelected(false);
                btM4A.setSelected(false);
                mOutType = "aac";
                break;
            case R.id.bt_m4a:
                btM4A.setSelected(true);
                btMp3.setSelected(false);
                btWav.setSelected(false);
                btWma.setSelected(false);
                btFlac.setSelected(false);
                btAac.setSelected(false);
                mOutType = "m4a";
                break;
            //采样率
            case R.id.bt_0:
                bt0.setSelected(true);
                bt8000.setSelected(false);
                bt11025.setSelected(false);
                bt22050.setSelected(false);
                bt32000.setSelected(false);
                bt44100.setSelected(false);
                bt48000.setSelected(false);
                bt50400.setSelected(false);
                bt96000.setSelected(false);
                hz = "0";
                break;
            case R.id.bt_8000:
                bt0.setSelected(false);
                bt8000.setSelected(true);
                bt11025.setSelected(false);
                bt22050.setSelected(false);
                bt32000.setSelected(false);
                bt44100.setSelected(false);
                bt48000.setSelected(false);
                bt50400.setSelected(false);
                bt96000.setSelected(false);
                hz = "8000";
                break;
            case R.id.bt_11025:
                bt0.setSelected(false);
                bt8000.setSelected(false);
                bt11025.setSelected(true);
                bt22050.setSelected(false);
                bt32000.setSelected(false);
                bt44100.setSelected(false);
                bt48000.setSelected(false);
                bt50400.setSelected(false);
                bt96000.setSelected(false);
                hz = "11025";
                break;
            case R.id.bt_22050:
                bt0.setSelected(false);
                bt8000.setSelected(false);
                bt11025.setSelected(false);
                bt22050.setSelected(true);
                bt32000.setSelected(false);
                bt44100.setSelected(false);
                bt48000.setSelected(false);
                bt50400.setSelected(false);
                bt96000.setSelected(false);
                hz = "22050";
                break;
            case R.id.bt_32000:
                bt0.setSelected(false);
                bt8000.setSelected(false);
                bt11025.setSelected(false);
                bt22050.setSelected(false);
                bt32000.setSelected(true);
                bt44100.setSelected(false);
                bt48000.setSelected(false);
                bt50400.setSelected(false);
                bt96000.setSelected(false);
                hz = "32000";
                break;
            case R.id.bt_44100:
                bt0.setSelected(false);
                bt8000.setSelected(false);
                bt11025.setSelected(false);
                bt22050.setSelected(false);
                bt32000.setSelected(false);
                bt44100.setSelected(true);
                bt48000.setSelected(false);
                bt50400.setSelected(false);
                bt96000.setSelected(false);
                hz = "44100";
                break;
            case R.id.bt_48000:
                bt0.setSelected(false);
                bt8000.setSelected(false);
                bt11025.setSelected(false);
                bt22050.setSelected(false);
                bt32000.setSelected(false);
                bt44100.setSelected(false);
                bt48000.setSelected(true);
                bt50400.setSelected(false);
                bt96000.setSelected(false);
                hz = "48000";
                break;
            case R.id.bt_50400:
                bt0.setSelected(false);
                bt8000.setSelected(false);
                bt11025.setSelected(false);
                bt22050.setSelected(false);
                bt32000.setSelected(false);
                bt44100.setSelected(false);
                bt48000.setSelected(false);
                bt50400.setSelected(true);
                bt96000.setSelected(false);
                hz = "50400";
                break;
            case R.id.bt_96000:
                bt0.setSelected(false);
                bt8000.setSelected(false);
                bt11025.setSelected(false);
                bt22050.setSelected(false);
                bt32000.setSelected(false);
                bt44100.setSelected(false);
                bt48000.setSelected(false);
                bt50400.setSelected(false);
                bt96000.setSelected(true);
                hz = "96000";
                break;
            //比特率
            case R.id.bt_32:
                bt32.setSelected(true);
                bt96.setSelected(false);
                bt160.setSelected(false);
                bt192.setSelected(false);
                bt224.setSelected(false);
                bt320.setSelected(false);
                kbps = "32k";
                break;
            case R.id.bt_96:
                bt32.setSelected(false);
                bt96.setSelected(true);
                bt160.setSelected(false);
                bt192.setSelected(false);
                bt224.setSelected(false);
                bt320.setSelected(false);
                kbps = "96k";
                break;
            case R.id.bt_160:
                bt32.setSelected(false);
                bt96.setSelected(false);
                bt160.setSelected(true);
                bt192.setSelected(false);
                bt224.setSelected(false);
                bt320.setSelected(false);
                kbps = "160k";
                break;
            case R.id.bt_192:
                bt32.setSelected(false);
                bt96.setSelected(false);
                bt160.setSelected(false);
                bt192.setSelected(true);
                bt224.setSelected(false);
                bt320.setSelected(false);
                kbps = "192k";
                break;
            case R.id.bt_224:
                bt32.setSelected(false);
                bt96.setSelected(false);
                bt160.setSelected(false);
                bt192.setSelected(false);
                bt224.setSelected(true);
                bt320.setSelected(false);
                kbps = "224k";
                break;
            case R.id.bt_320:
                bt32.setSelected(false);
                bt96.setSelected(false);
                bt160.setSelected(false);
                bt192.setSelected(false);
                bt224.setSelected(false);
                bt320.setSelected(true);
                kbps = "320k";
                break;
            //声道
            case R.id.bt_source:
                btSource.setSelected(true);
                btSTrack.setSelected(false);
                btDTrack.setSelected(false);
                channel = "2";
                break;
            case R.id.bt_s_track:
                btSource.setSelected(false);
                btSTrack.setSelected(true);
                btDTrack.setSelected(false);
                channel = "1";
                break;
            case R.id.bt_d_track:
                btSource.setSelected(false);
                btSTrack.setSelected(false);
                btDTrack.setSelected(true);
                channel = "2";
                break;
            case R.id.btn_start_transcode:
                startTranscode();
                break;
        }
    }

    private void startTranscode() {
        run(mAudioPath, mOutPath, mAudioType, mOutType, hz, kbps, channel);
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

    private void updateAudio() {
        mOutPath = Constant.getMusicPath() + mAudioName + getDetailTime() + "." + mOutType;
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
                mAudioType = mAudioPath.substring(mAudioPath.lastIndexOf(".") + 1);
//                mOutPath = Constant.getMusicPath() + data.get(0).getName().substring(0, data.get(0).getName().lastIndexOf("."));
                mOutPath = Constant.getMusicPath() + "音频转码_" + System.currentTimeMillis();
//                mOutType = data.get(0).getName().substring(data.get(0).getName().lastIndexOf("."));
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
