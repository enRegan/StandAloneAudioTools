package com.regan.saata.activity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.regan.saata.R;


public class VideoShowActivity extends AppCompatActivity implements View.OnClickListener {
    private SurfaceView sfv;
    private Button btPlay;
    private Button btPause;
    private Button btStop;
    private Button btSwitch;
    private MediaPlayer mediaPlayer;
    boolean isPlay = false;
    private int position;
    private String url1 = "http://flashmedia.eastday.com/newdate/news/2016-11/shznews1125-19.mp4";
    private String url2 = "rtsp://184.72.239.149/vod/mp4:BigBuckBunny_115k.mov";
    private String url3 = "http://42.96.249.166/live/388.m3u8";
    private String url4 = "http://61.129.89.191/ThroughTrain/download.html?id=4035&flag=-org-"; //音频url
    private String url5 = "http://ivi.bupt.edu.cn/hls/cctv1hd.m3u8"; //
    private String url6 = "rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_show);
        init();
    }

    private void init() {
        sfv = findViewById(R.id.sfv);
        btPlay = findViewById(R.id.btn_play);
        btPause = findViewById(R.id.btn_pause);
        btStop = findViewById(R.id.btn_stop);
        btSwitch = findViewById(R.id.btn_switch);

        mediaPlayer = new MediaPlayer();

        btPlay.setOnClickListener(this);
        btPause.setOnClickListener(this);
        btStop.setOnClickListener(this);
        btSwitch.setOnClickListener(this);

        // 设置SurfaceView自己不管理的缓冲区
        sfv.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        sfv.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (position > 0) {
                    try {
                        // 开始播放
                        play();
                        // 并直接从指定位置开始播放
                        mediaPlayer.seekTo(position);
                        position = 0;
                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_play:
                play();
                break;

            case R.id.btn_pause:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                } else {
                    mediaPlayer.start();
                }
                break;

            case R.id.btn_stop:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                break;
            case R.id.btn_switch:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    switchVideo();
                }
                break;
            default:
                break;
        }

    }

    @Override
    protected void onPause() {
        // 先判断是否正在播放
        if (mediaPlayer.isPlaying()) {
            // 如果正在播放我们就先保存这个播放位置
            position = mediaPlayer.getCurrentPosition();
            mediaPlayer.stop();
        }
        super.onPause();
    }

    private void play() {
        try {
            mediaPlayer.reset();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            // 设置需要播放的视频
            Uri uri = Uri.parse(url1);
            mediaPlayer.setDataSource(getApplicationContext(), uri);
            // 把视频画面输出到SurfaceView
            mediaPlayer.setDisplay(sfv.getHolder());
            mediaPlayer.prepare();
            // 播放
            mediaPlayer.start();
            Toast.makeText(this, "开始播放！", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
        }
    }

    private void switchVideo() {
        try {
            mediaPlayer.reset();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            // 设置需要播放的视频
//            Uri uri = Uri.parse(url6);
            mediaPlayer.setDataSource(url5);
            // 把视频画面输出到SurfaceView
            mediaPlayer.setDisplay(sfv.getHolder());
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Log.d(VideoShowActivity.class.getName(), "onPrepared");
                    // 播放
                    mp.start();
//                    Toast.makeText(VideoShowActivity.this, "开始播放！", Toast.LENGTH_LONG).show();
                }
            });

        } catch (Exception e) {
            Log.d(VideoShowActivity.class.getName(), " : " + e.getMessage());
        }
    }
}
