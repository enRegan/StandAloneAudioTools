package com.regan.saata.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.annotation.Nullable;


import com.regan.saata.Constant;
import com.regan.saata.util.LogUtils;
import com.regan.saata.view.ProgressDialog;

import java.util.Timer;
import java.util.TimerTask;

import nl.bravobit.ffmpeg.FFtask;

public abstract class BaseFunctionActivity extends BaseActivity {
    public ProgressDialog progressDialog;
    public FFtask fFtask;
    public Timer fFmpegTimer;
    public TimerTask fFmpegTimerTask;
    public int pauseTime;
    public Message progressMsg;
    public Message timeMsg;
    public Message stopMsg;
    public final int pMsg = 1;
    public final int tMsg = 2;
    public final int sMsg = 3;
    public boolean isKill = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        init();
        progressDialog = new ProgressDialog(this, fFmpeg, new ProgressDialog.FfmpegKill() {
            @Override
            public void kill() {
                fFtask.sendQuitSignal();
                fFmpegTimerTask.cancel();
                pauseTime = 0;
                isKill = true;
                LogUtils.d(Constant.TAG, " sendQuitSignal ");
            }
        }).getProgressDialog();
        fFmpegTimer = new Timer();
        pauseTime = 0;
        progressMsg = new Message();
        progressMsg.arg1 = pMsg;
        timeMsg = new Message();
        timeMsg.arg1 = tMsg;
        stopMsg = new Message();
        stopMsg.arg1 = sMsg;
    }

//    protected abstract void init();

    public Handler timerHandler = new Handler() {


        @Override
        public void handleMessage(Message msg) {
            switch (msg.arg1) {
                case pMsg:
                    pauseTime = 0;
//                    LogUtils.d(Constant.TAG, "progressMsg : " + pauseTime);
                    break;
                case tMsg:
                    pauseTime++;
//                    LogUtils.d(Constant.TAG, "timeMsg : " + pauseTime);
                    if (pauseTime >= 10) {
                        fFtask.sendQuitSignal();
                        fFmpegTimerTask.cancel();
                        stopFFmpeg();
                        pauseTime = 0;
                        progressDialog.dismiss();
                    }
                    break;
                case sMsg:
//                    LogUtils.d(Constant.TAG, "stopMsg : " + pauseTime);
                    pauseTime = 0;
                    fFmpegTimer.cancel();
                    break;
            }
        }
    };

    /**
     * 任务超时结束调用
     */
    protected void stopFFmpeg() {
        Toast.makeText(this, "转换失败，请更换其他参数重试。", Toast.LENGTH_SHORT).show();
    }

    public class MTimerTask extends TimerTask {

        @Override
        public void run() {
            timeMsg = new Message();
            timeMsg.arg1 = tMsg;
            timerHandler.sendMessage(timeMsg);
            isKill = false;
        }
    }

    @Override
    public void onBackPressed() {
        LogUtils.d(Constant.TAG, " onBackPressed ");
        super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtils.d(Constant.TAG, " onKeyDown " + keyCode);
        if (keyCode == KeyEvent.KEYCODE_BACK) { //按下的如果是BACK，同时没有重复
            onBackPressed();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressDialog = null;
        fFtask = null;
    }
}
