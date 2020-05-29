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

import com.bumptech.glide.Glide;
import com.regan.saata.Constant;
import com.regan.saata.R;
import com.regan.saata.util.FileDurationUtil;
import com.regan.saata.util.FileManager;
import com.regan.saata.util.LogUtils;
import com.regan.saata.util.MediaTool;
import com.regan.saata.util.TimeUtils;
import com.regan.saata.view.MySetDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;

import nl.bravobit.ffmpeg.FFcommandExecuteResponseHandler;

public class VideoExtractActivity extends BaseFunctionActivity implements View.OnClickListener {
    private String mVideoPath, mOutPath, mOutType;
    private Button btnStartTranscode;
    private float mVideoTime;
    //    private VideoView videoView;
    private TextView tvName, tvContent;
    private ImageView ivStart, ivPreview;
    private LinearLayout llSetRate;
    private LinearLayout llSetBit;
    private TextView tvRate;
    private TextView tvBit;
    private String rate;
    private String bit;
    private int ratePosition = -1;
    private int bitPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_extract);
        tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText("视频转音频");
        ivBack = findViewById(R.id.iv_back);
        btnStartTranscode = findViewById(R.id.btn_start_transcode);
//        videoView = findViewById(R.id.vv_video);
        tvName = findViewById(R.id.tv_name);
        tvContent = findViewById(R.id.tv_content);
        ivPreview = findViewById(R.id.iv_preview);
        ivStart = findViewById(R.id.iv_video_start);
        llSetRate = findViewById(R.id.ll_set_rate);
        llSetBit = findViewById(R.id.ll_set_bit);
        tvRate = findViewById(R.id.tv_rate);
        tvBit = findViewById(R.id.tv_bit);

        if (getIntent() != null && getIntent().getExtras() != null) {
            mVideoPath = getIntent().getStringExtra("mVideoPath");
            mOutPath = getIntent().getStringExtra("mOutPath");
            mOutType = getIntent().getStringExtra("mOutType");
            tvTitle.setText("视频转" + mOutType);
            mVideoTime = FileDurationUtil.getDuration(mVideoPath);
            LogUtils.d(Constant.TAG, " mVideoPath : " + mVideoPath + " mOutPath : " + mOutPath);
            Glide.with(this).load(mVideoPath).into(ivPreview);
            tvName.setText(mVideoPath.substring(mVideoPath.lastIndexOf("/") + 1));
            tvContent.setText(TimeUtils.secondToTime(FileDurationUtil.getDuration(mVideoPath) / 1000));
        }
        ivStart.setOnClickListener(this);
        rate = "";
        bit = "";
        btnStartTranscode.setOnClickListener(this);
        llSetRate.setOnClickListener(this);
        llSetBit.setOnClickListener(this);
        ivBack.setOnClickListener(this);
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
//                ivPreview.setVisibility(View.GONE);
//                ivStart.setVisibility(View.GONE);
                FileManager.openFile(VideoExtractActivity.this, mVideoPath, "mp4");
//                videoView.start();
                break;
            case R.id.ll_set_rate:
                LogUtils.d(Constant.TAG, "ll_set_rate");
                MySetDialog setRateDialog = new MySetDialog(VideoExtractActivity.this, R.style.my_set_dialog);
                Window setRateDialogWindow = setRateDialog.getWindow();
                //设置弹出位置
                setRateDialogWindow.setGravity(Gravity.BOTTOM);
                setRateDialog.setCanceledOnTouchOutside(false);
                setRateDialog.setData(2, ratePosition);
                setRateDialog.setDialogListener(new MySetDialog.DialogListener() {
                    @Override
                    public void getResult(final int result) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ratePosition = result;
                                switch (result) {
                                    case 0:
                                        rate = "96000";
                                        if (mOutType.equals("mp3")) {
                                            ratePosition = 1;
                                            rate = "48000";
                                            tvRate.setText(rate);
                                            Toast.makeText(VideoExtractActivity.this, "该格式采样率最大支持48000", Toast.LENGTH_SHORT).show();
                                        }
                                        if (mOutType.equals("wma")) {
                                            ratePosition = 1;
                                            rate = "48000";
                                            tvRate.setText(rate);
                                            Toast.makeText(VideoExtractActivity.this, "该格式采样率最大支持48000", Toast.LENGTH_SHORT).show();
                                        }
                                        tvRate.setText(rate);
                                        break;
                                    case 1:
                                        rate = "48000";
                                        tvRate.setText(rate);
                                        break;
                                    case 2:
                                        rate = "44100";
                                        tvRate.setText(rate);
                                        break;
                                    case 3:
                                        rate = "32000";
                                        tvRate.setText(rate);
                                        break;
                                    case 4:
                                        rate = "11025";
                                        tvRate.setText(rate);
                                        break;
                                    case 5:
                                        rate = "8000";
                                        tvRate.setText(rate);
                                        break;
                                }
                            }
                        });
                    }
                });
                setRateDialog.show();
                /** * 设置dialog宽度全屏 */
                WindowManager m = getWindowManager();
                Display d = m.getDefaultDisplay(); //为获取屏幕宽、高
                android.view.WindowManager.LayoutParams params = setRateDialog.getWindow().getAttributes(); //获取对话框当前的参数值、
                params.width = (int) (d.getWidth()); //宽度设置全屏宽度
                setRateDialog.getWindow().setAttributes(params);

                break;
            case R.id.ll_set_bit:
                LogUtils.d(Constant.TAG, "ll_set_speed");
                MySetDialog setBitDialog = new MySetDialog(VideoExtractActivity.this, R.style.my_set_dialog);
                Window setBitDialogWindow = setBitDialog.getWindow();
                //设置弹出位置
                setBitDialogWindow.setGravity(Gravity.BOTTOM);
                setBitDialog.setCanceledOnTouchOutside(false);
                setBitDialog.setData(3, bitPosition);
                setBitDialog.setDialogListener(new MySetDialog.DialogListener() {
                    @Override
                    public void getResult(final int result) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                bitPosition = result;
                                switch (result) {
                                    case 0:
                                        bit = "320k";
                                        tvBit.setText(bit);
                                        break;
                                    case 1:
                                        bit = "256k";
                                        tvBit.setText(bit);
                                        break;
                                    case 2:
                                        bit = "224k";
                                        tvBit.setText(bit);
                                        break;
                                    case 3:
                                        bit = "192k";
                                        tvBit.setText(bit);
                                        break;
                                    case 4:
                                        bit = "160k";
                                        tvBit.setText(bit);
                                        break;
                                    case 5:
                                        bit = "32k";
                                        tvBit.setText(bit);
                                        break;
                                }
                            }
                        });
                    }
                });
                setBitDialog.show();
                /** * 设置dialog宽度全屏 */
                WindowManager m2 = getWindowManager();
                Display d2 = m2.getDefaultDisplay(); //为获取屏幕宽、高
                android.view.WindowManager.LayoutParams params2 = setBitDialog.getWindow().getAttributes(); //获取对话框当前的参数值、
                params2.width = (int) (d2.getWidth()); //宽度设置全屏宽度
                setBitDialog.getWindow().setAttributes(params2);
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    private void startTranscode() {
        run(mVideoPath, mOutPath, mOutType);
    }

    public void run(String srcPath, final String outPath, final String outType) {
        String[] cmd;
        ArrayList<String> cmdd = new ArrayList<>();
        cmdd.add("-i");
        cmdd.add(srcPath);
        cmdd.add("-vn");
        cmdd.add("-y");
        if (!TextUtils.isEmpty(rate)) {
            cmdd.add("-ar");//-ar freq 设置音频采样率
            cmdd.add(rate);
        }
        if (!TextUtils.isEmpty(bit)) {
            cmdd.add("-ab");//-ab bitrate 设置音频码率
            cmdd.add(bit);
        }
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
        final String outFile = outPath + "." + outType;
        cmdd.add(outFile);

        LogUtils.d(Constant.TAG, " ffmpeg: " + cmdd.toString());
        cmd = cmdd.toArray(new String[0]);
        try {
            fFtask = fFmpeg.execute(cmd, new FFcommandExecuteResponseHandler() {
                @Override
                public void onSuccess(String message) {
                    Log.d(Constant.TAG, "execute onSuccess : " + message);
                    Toast.makeText(VideoExtractActivity.this, "生成成功，存储在" + outFile, Toast.LENGTH_LONG).show();
                    MediaTool.insertMedia(getApplicationContext(), outFile);
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(VideoExtractActivity.this, SuccessActivity.class);
                            intent.putExtra("toList", true);
                            intent.putExtra("mVideoPath", outFile);
                            intent.putExtra("mOutType", outType);
                            startActivityForResult(intent, MainActivity.CODE_TO_FUNC);
                        }
                    }, 100);
                }

                @Override
                public void onProgress(String message) {
                    if (!TextUtils.isEmpty(message) && message.contains("time=") && message.contains("bitrate")) {
                        String time = message.substring(message.lastIndexOf("time=") + 5, message.lastIndexOf("bitrate"));
                        float d = (Constant.time2Float(time) * 1000) / mVideoTime;
                        LogUtils.d(Constant.TAG, "onProgress time : " + time);
                        LogUtils.d(Constant.TAG, "onProgress : " + d);
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
                        Toast.makeText(VideoExtractActivity.this, "转换失败，请更换其他参数重试。", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onStart() {
                    Log.d(Constant.TAG, "execute onStart : ");
                    LogUtils.d(Constant.TAG, "mVideoTime : " + mVideoTime);
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
//if (!TextUtils.isEmpty(hz)) {
//        cmdd.add("-ar");//-ar freq 设置音频采样率
//        cmdd.add(hz);
//        }
//        if (!TextUtils.isEmpty(kbps)) {
//        cmdd.add("-b:a");//-ab bitrate 设置音频码率
//        cmdd.add(kbps);
//        }
//        if (!TextUtils.isEmpty(channel)) {
//        cmdd.add("-ac");//设定声道数，1就是单声道，2就是立体声
//        cmdd.add(channel);
//        }
//
//        cmdd.add("-b:v");//-ab bitrate 设置视频码率
//        cmdd.add(kbps);