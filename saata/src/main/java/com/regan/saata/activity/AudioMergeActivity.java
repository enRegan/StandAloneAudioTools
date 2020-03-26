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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.regan.saata.Constant;
import com.regan.saata.R;
import com.regan.saata.adapter.AudioMergeAdapter;
import com.regan.saata.bean.AudioInfo;
import com.regan.saata.util.FileDurationUtil;
import com.regan.saata.util.ListDataSave;
import com.regan.saata.util.LogUtils;
import com.regan.saata.util.MediaTool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import nl.bravobit.ffmpeg.FFcommandExecuteResponseHandler;


public class AudioMergeActivity extends FuncActivity implements View.OnClickListener {
    private RecyclerView rvAudio;
    private Button btStartMerge;
    private ListDataSave listDataSave;
    private ArrayList<AudioInfo> data;
    private AudioMergeAdapter audioListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_merge);
        init();
        getData();
        LogUtils.d(Constant.TAG, "AudioMergeActivity onCreate");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.d(Constant.TAG, "AudioMergeActivity onResume");
    }

    private void getData() {
        startActivityForResult(new Intent(this, AudioMultiSelectActivity.class), MainActivity.CODE_TO_FUNC);
    }

    private void init() {
        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);
        btStartMerge = findViewById(R.id.btn_start_merge);
        rvAudio = findViewById(R.id.rv_audio);
        listDataSave = new ListDataSave(AudioMergeActivity.this, Constant.AUDIO_LIST);

        showBack(ivBack, true);
        tvTitle.setText("音频合并");
        data = new ArrayList<>();
//        if(getIntent() != null && getIntent().getExtras() != null){
//            data = (ArrayList<AudioInfo>) getIntent().getSerializableExtra("data");
//        }

//        List<AudioInfo> data = listDataSave.getDataList(Constant.AUDIO);
//        List<AudioInfo> data = listDataSave.getDataList(Constant.AUDIO, AudioInfo.class);
        audioListAdapter = new AudioMergeAdapter(AudioMergeActivity.this, data);
        rvAudio.setLayoutManager(new LinearLayoutManager(AudioMergeActivity.this));
        rvAudio.setAdapter(audioListAdapter);
//        audioListAdapter.refresh(data);

        btStartMerge.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_merge:
                //音频合并
                startMerge();
                break;
        }
    }

    private void startMerge() {
        run(data, Constant.getMusicPath(), ".aac");
    }

    public void run(ArrayList<AudioInfo> data, String outPath, String outType) {
        //        cmdd.add("-i");
//        cmdd.add(srcPath);
//        cmdd.add("-i");
//        cmdd.add("/storage/emulated/0/Music/环州路 2.m4a");
//        cmdd.add("-i");
//        cmdd.add("/storage/emulated/0/yunmai/trainMusic/background/Motion Invasion.mp3");
//        cmdd.add("-filter_complex");
//        cmdd.add("[0:0][1:0][2:0]concat=n=3:v=0:a=1[out]");
//        cmdd.add("-map");
//        cmdd.add("[out]");
        FileDurationUtil fileDurationUtil = new FileDurationUtil();
        float time = 0;
        for (AudioInfo info : data) {
            time += fileDurationUtil.getDuration(info.getPath());
        }
        final float totalTime = time;
        String[] cmd;
        ArrayList<String> cmdd = new ArrayList<>();
        StringBuilder filterComplex = new StringBuilder();
        for (int i = 0; i < data.size(); i++) {
            cmdd.add("-i");
            cmdd.add(data.get(i).getPath());
            filterComplex.append("[").append(i).append(":0]");
        }
        filterComplex.append("concat=n=").append(data.size()).append(":v=0:a=1[out]");
        cmdd.add("-filter_complex");
        cmdd.add(filterComplex.toString());
        cmdd.add("-map");
        cmdd.add("[out]");
        final String outFile = outPath + "音频合并_" + System.currentTimeMillis() + outType;
        cmdd.add(outFile);
        cmd = cmdd.toArray(new String[0]);
        LogUtils.d(Constant.TAG, " ffmpeg : " + cmdd.toString());
        try {
            fFtask = fFmpeg.execute(cmd, new FFcommandExecuteResponseHandler() {
                @Override
                public void onSuccess(String message) {
                    LogUtils.d(Constant.TAG, "execute onSuccess : " + message);
                    Toast.makeText(AudioMergeActivity.this, "生成成功", Toast.LENGTH_SHORT).show();
                    MediaTool.insertMedia(AudioMergeActivity.this, outFile);
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(AudioMergeActivity.this, MainActivity.class);
                            intent.putExtra("toList", true);
                            setResult(RESULT_OK, intent);
//                            startActivity(intent);
                            finish();
                        }
                    }, 2000);
                }

                @Override
                public void onProgress(String message) {
//                    LogUtils.d(Constant.TAG, "execute onProgress : " + message);
                    if (!TextUtils.isEmpty(message) && message.contains("time=") && message.contains("bitrate")) {
                        String time = message.substring(message.lastIndexOf("time=") + 5, message.lastIndexOf("bitrate"));
//                        LogUtils.d(Constant.TAG, "onProgress : " + time);
//                        LogUtils.d(Constant.TAG, "onProgress : " + Constant.time2Float(time));
//                        LogUtils.d(Constant.TAG, "totalTime : " + totalTime);
                        float d = (Constant.time2Float(time) * 1000) / totalTime;
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
                    Toast.makeText(AudioMergeActivity.this, "生成出现错误，请稍后重试", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onStart() {
                    LogUtils.d(Constant.TAG, "execute onStart : ");
//                    Toast.makeText(AudioMergeActivity.this, "正在生成请稍等", Toast.LENGTH_SHORT).show();
//                    progressDialog = new ProgressDialog(AudioMergeActivity.this).getProgressDialog();
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
                audioListAdapter.refresh(data);
//                LogUtils.d(Constant.TAG, "AudioMergeActivity data" + data.size());
            } else {
                finish();
            }
        } else {
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.d(Constant.TAG, " AudioMergeActivity onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.d(Constant.TAG, " AudioMergeActivity onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.d(Constant.TAG, " AudioMergeActivity onDestroy");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        LogUtils.d(Constant.TAG, " AudioMergeActivity onBackPressed");
    }
}
