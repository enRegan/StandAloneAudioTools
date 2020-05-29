package com.regan.saata.activity;

import androidx.annotation.Nullable;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
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

public class VideoTranscodeActivity extends BaseFunctionActivity implements View.OnClickListener {
    private String mVideoPath, mOutPath, mOutType;
    private Button btnStartTranscode;
    private float mVideoTime;
    //    private VideoView videoView;
    private TextView tvName, tvContent;
    private ImageView ivStart, ivPreview;
    private LinearLayout llSetBit;
    private LinearLayout llSetResolution;
    private TextView tvBit;
    private TextView tvResolution;
    private String bit;
    private String resolution;
    private int bitPosition = -1;
    private int resolutionPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_transcode);
        tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText("视频转其他格式");
        ivBack = findViewById(R.id.iv_back);
        btnStartTranscode = findViewById(R.id.btn_start_transcode);
//        videoView = findViewById(R.id.vv_video);
        tvName = findViewById(R.id.tv_name);
        tvContent = findViewById(R.id.tv_content);
        ivPreview = findViewById(R.id.iv_preview);
        ivStart = findViewById(R.id.iv_video_start);
        llSetBit = findViewById(R.id.ll_set_bit);
        llSetResolution = findViewById(R.id.ll_set_resolution);
        tvBit = findViewById(R.id.tv_bit);
        tvResolution = findViewById(R.id.tv_resolution);

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
        bit = "";
        resolution = "";
        btnStartTranscode.setOnClickListener(this);
        llSetBit.setOnClickListener(this);
        llSetResolution.setOnClickListener(this);
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
                FileManager.openFile(VideoTranscodeActivity.this, mVideoPath, "mp4");
//                videoView.start();
                break;
            case R.id.ll_set_bit:
                LogUtils.d(Constant.TAG, "ll_set_bit");
                MySetDialog setBitDialog = new MySetDialog(VideoTranscodeActivity.this, R.style.my_set_dialog);
                Window setBitDialogWindow = setBitDialog.getWindow();
                //设置弹出位置
                setBitDialogWindow.setGravity(Gravity.BOTTOM);
                setBitDialog.setCanceledOnTouchOutside(false);
                setBitDialog.setData(4, bitPosition);
                setBitDialog.setDialogListener(new MySetDialog.DialogListener() {
                    @Override
                    public void getResult(final int result) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                bitPosition = result;
                                switch (result) {
                                    case 0:
                                        bit = "128k";
                                        tvBit.setText(bit);
                                        break;
                                    case 1:
                                        bit = "512k";
                                        tvBit.setText(bit);
                                        break;
                                    case 2:
                                        bit = "1M";
                                        tvBit.setText(bit);
                                        break;
                                    case 3:
                                        bit = "2M";
                                        tvBit.setText(bit);
                                        break;
                                    case 4:
                                        bit = "3M";
                                        tvBit.setText(bit);
                                        break;
                                    case 5:
                                        bit = "5M";
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
            case R.id.ll_set_resolution:
                LogUtils.d(Constant.TAG, "ll_set_resolution");
                MySetDialog setRateDialog = new MySetDialog(VideoTranscodeActivity.this, R.style.my_set_dialog);
                Window setRateDialogWindow = setRateDialog.getWindow();
                //设置弹出位置
                setRateDialogWindow.setGravity(Gravity.BOTTOM);
                setRateDialog.setCanceledOnTouchOutside(false);
                setRateDialog.setData(5, resolutionPosition);
                setRateDialog.setDialogListener(new MySetDialog.DialogListener() {
                    @Override
                    public void getResult(final int result) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                resolutionPosition = result;
                                switch (result) {
                                    case 0:
                                        resolution = "240";
                                        tvResolution.setText("240p");
                                        break;
                                    case 1:
                                        resolution = "320";
                                        tvResolution.setText("320p");
                                        break;
                                    case 2:
                                        resolution = "480";
                                        tvResolution.setText("480p");
                                        break;
                                    case 3:
                                        resolution = "720";
                                        tvResolution.setText("720p");
                                        break;
                                    case 4:
                                        resolution = "1080";
                                        tvResolution.setText("1080p");
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
//        cmdd.add("-encoders");
        cmdd.add("-i");
        cmdd.add(srcPath);
        if (!TextUtils.isEmpty(bit)) {
            cmdd.add("-b:v");//-ab bitrate 设置视频码率
            cmdd.add(bit);
        }
//        if (!TextUtils.isEmpty(resolution)) {
//            cmdd.add("-s");//-ab bitrate 设置分辨率
//            cmdd.add(resolution);
//        }
        if (!TextUtils.isEmpty(resolution)) {
            cmdd.add("-vf");//-ab bitrate 设置分辨率
            cmdd.add("scale=" + resolution + ":-2");
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
                    Toast.makeText(VideoTranscodeActivity.this, "生成成功，存储在" + outFile, Toast.LENGTH_LONG).show();
                    MediaTool.insertMedia(getApplicationContext(), outFile);
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(VideoTranscodeActivity.this, SuccessActivity.class);
                            intent.putExtra("toList", true);
                            intent.putExtra("mVideoPath", outFile);
                            intent.putExtra("mOutType", outType);
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
//                        Toast.makeText(Video2GifActivity.this, "该音频不支持该参数，请修改后重试", Toast.LENGTH_SHORT).show();
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
