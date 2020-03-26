package com.regan.saata.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import com.regan.saata.Constant;
import com.regan.saata.R;
import com.regan.saata.fragment.HomeFragment;
import com.regan.saata.fragment.ListFragment;
import com.regan.saata.fragment.MineFragment;
import com.regan.saata.util.LogUtils;
import com.regan.saata.util.SharedPrefrencesUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class MainActivity extends BaseActivity implements View.OnClickListener, HomeFragment.OnFragmentInteractionListener {
    private final static int CODE_REQUEST_WRITE_EXTERNAL = 0x100;
    public final static int CODE_TO_FUNC = 50;

    //    private FFmpeg fFmpeg;
    private String TAG = Constant.TAG;


    private FrameLayout frameLayout;

    private HomeFragment homeFragment;
    private ListFragment listFragment;
    private MineFragment mineFragment;

    private LinearLayout llHome;
    private RelativeLayout llList;
    private LinearLayout llMine;

    private TextView tvHome;
    private TextView tvList;
    private TextView tvMine;
    private ImageView ivHome;
    private ImageView ivList;
    private ImageView ivMine;

    private Fragment mContent;

    //    private ParamContants.Logger logger;
    private String smscode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //必须在super 之前调用,不然无效。因为那时候fragment已经被恢复了。
        if (savedInstanceState != null) {
            // FRAGMENTS_TAG
            savedInstanceState.remove("android:support:fragments");
            savedInstanceState.remove("android:fragments");
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        checkPermission();

        frameLayout = findViewById(R.id.frame_all);
        llHome = findViewById(R.id.ll_home);
        llList = findViewById(R.id.ll_list);
        llMine = findViewById(R.id.ll_mine);

        tvHome = findViewById(R.id.tv_home);
        tvList = findViewById(R.id.tv_list);
        tvMine = findViewById(R.id.tv_mine);
        ivHome = findViewById(R.id.iv_home);
        ivList = findViewById(R.id.iv_list);
        ivMine = findViewById(R.id.iv_mine);

        llHome.setOnClickListener(this);
        llList.setOnClickListener(this);
        llMine.setOnClickListener(this);

        String loginTkoen = SharedPrefrencesUtil.getStringByKey(MainActivity.this, SharedPrefrencesUtil.LOGIN_TOKEN);
        if (!TextUtils.isEmpty(loginTkoen)) {
            Constant.loginChange(true);
        }


//        fFmpeg = FFmpeg.getInstance(this);
//        try {
//            fFmpeg.loadBinary(new FFmpegLoadBinaryResponseHandler() {
//                @Override
//                public void onFailure() {
//                    LogUtils.d(TAG, "loadBinary onFailure");
//                }
//
//                @Override
//                public void onSuccess() {
//                    LogUtils.d(TAG, "loadBinary onSuccess");
//                }
//
//                @Override
//                public void onStart() {
//                    LogUtils.d(TAG, "loadBinary onStart");
//                }
//
//                @Override
//                public void onFinish() {
//                    LogUtils.d(TAG, "loadBinary onFinish");
//                }
//            });
//        } catch (FFmpegNotSupportedException e) {
//            LogUtils.d(TAG, "FFmpegNotSupportedException : " + e.getMessage());
//        }


        initFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.d(Constant.TAG, " MainActivity onResume  ");
        if (getIntent() != null) {
            LogUtils.d(Constant.TAG, " MainActivity onResume getIntent ");
            if (getIntent().getExtras() != null) {
                LogUtils.d(Constant.TAG, " MainActivity onResume getIntent getExtras : " + getIntent().getBooleanExtra("toList", false));
            }
        }
    }

//    public void run() {
//        String[] cmd = new String[1];
//        cmd[0] = "-version";
//        try {
//            fFmpeg.execute(cmd, new FFmpegExecuteResponseHandler() {
//                @Override
//                public void onSuccess(String message) {
//                    Log.d(TAG, "execute onSuccess : " + message);
//                }
//
//                @Override
//                public void onProgress(String message) {
//                    Log.d(TAG, "execute onProgress : " + message);
//                }
//
//                @Override
//                public void onFailure(String message) {
//                    Log.d(TAG, "execute onFailure : " + message);
//                }
//
//                @Override
//                public void onStart() {
//                    Log.d(TAG, "execute onStart : ");
//                }
//
//                @Override
//                public void onFinish() {
//                    Log.d(TAG, "execute onFinish : ");
//                }
//            });
//        } catch (FFmpegCommandAlreadyRunningException e) {
//            Log.d(TAG, "execute exception : " + e.getMessage());
//        }
//    }

//    public void run() {
//        String dir = Environment.getExternalStorageDirectory().getPath() + "/ffmpegTest/";
//
//        //ffmpeg -i source_mp3.mp3 -ss 00:01:12 -t 00:01:42 -acodec copy output_mp3.mp3
//        String[] commands = new String[10];
//        commands[0] = "ffmpeg";
//        commands[1] = "-i";
//        commands[2] = dir+"paomo.mp3";
//        commands[3] = "-ss";
//        commands[4] = "00:01:00";
//        commands[5] = "-t";
//        commands[6] = "00:01:00";
//        commands[7] = "-acodec";
//        commands[8] = "copy";
//        commands[9] = dir+"paomo_cut_mp3.mp3";
//
//        int result = FFmpegJni.run(commands);
//        Toast.makeText(MainActivity.this, "命令行执行完成 result="+result, Toast.LENGTH_SHORT).show();
//    }

    public static String getDetailTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault());
        return sdf.format(System.currentTimeMillis());
    }

    public static String getVideoPath() {
        String path = null;
        File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
        if (!folder.exists()) {
            boolean mkdirs = folder.mkdirs();
        }
        path = folder.getAbsolutePath();
        if (!path.endsWith("/")) {
            return path + "/";
        } else {
            return path;
        }
    }

    private void initFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (homeFragment != null && homeFragment.isAdded()) {
            transaction.remove(homeFragment);
        }
        if (listFragment != null && listFragment.isAdded()) {
            transaction.remove(listFragment);
        }
        if (mineFragment != null && mineFragment.isAdded()) {
            transaction.remove(mineFragment);
        }
        transaction.commitAllowingStateLoss();
        homeFragment = null;
        listFragment = null;
        mineFragment = null;
        llHome.performClick();
    }


    public void switchContent(View view) {
        Fragment fragment;
        if (view == llHome) {
            if (homeFragment == null) {
                homeFragment = HomeFragment.newInstance();
            }
            fragment = homeFragment;
            tvHome.setTextColor(getResources().getColor(R.color.white));
            tvMine.setTextColor(getResources().getColor(R.color.buttomTextColor));
            ivHome.setImageResource(R.drawable.buttom_main_selected);
            ivMine.setImageResource(R.drawable.buttom_mine_normal);
        } else if (view == llList) {
            if (listFragment == null) {
                listFragment = ListFragment.newInstance();
            }
            fragment = listFragment;
            tvHome.setTextColor(getResources().getColor(R.color.buttomTextColor));
            tvMine.setTextColor(getResources().getColor(R.color.buttomTextColor));
            ivHome.setImageResource(R.drawable.buttom_main_normal);
            ivMine.setImageResource(R.drawable.buttom_mine_normal);
        } else if (view == llMine) {
            if (mineFragment == null) {
                mineFragment = MineFragment.newInstance();
            }
            fragment = mineFragment;
            tvHome.setTextColor(getResources().getColor(R.color.buttomTextColor));
            tvMine.setTextColor(getResources().getColor(R.color.white));
            ivHome.setImageResource(R.drawable.buttom_main_normal);
            ivMine.setImageResource(R.drawable.buttom_mine_selected);
        } else {
            return;
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (mContent == null) {
            transaction.add(frameLayout.getId(), fragment).commit();
            mContent = fragment;
        }
        if (mContent != fragment) {
            if (!fragment.isAdded()) {
                transaction.hide(mContent).add(frameLayout.getId(), fragment).commitAllowingStateLoss();
            } else {
                transaction.hide(mContent).show(fragment).commitAllowingStateLoss();
            }
            mContent = fragment;
        }
        llHome.setSelected(false);
        llList.setSelected(false);
        llMine.setSelected(false);
        view.setSelected(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_home:
                switchContent(llHome);
                break;
            case R.id.ll_list:
                switchContent(llList);
                break;
            case R.id.ll_mine:
                switchContent(llMine);
                break;
        }
    }

    public void switchList() {
        switchContent(llList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.d(Constant.TAG, " MainActivity onActivityResult : " + requestCode);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void setStilGetInfo() {
        if (listFragment != null) {
            listFragment.setStilGetInfo(false);
        }
    }

    @Override
    public void onFragmentInteraction() {

    }
}