package com.regan.saata.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
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
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.engine.impl.PicassoEngine;
import com.zhihu.matisse.filter.Filter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import nl.bravobit.ffmpeg.FFmpeg;

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
    private LinearLayout llList;
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

    private Button btnGif;
    private Button btnTrans;

    private int type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //必须在super 之前调用,不然无效。因为那时候fragment已经被恢复了。
        if (savedInstanceState != null) {
            // FRAGMENTS_TAG
            savedInstanceState.remove("android:support:fragments");
            savedInstanceState.remove("android:fragments");
        }
        boolean isFirst = true;
        isFirst = SharedPrefrencesUtil.getBooleanByKey(this, "isFirst", true);
        if (isFirst) {
            FFmpeg fFmpeg = FFmpeg.getInstance(this);
            fFmpeg.isSupported();
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

        btnGif = findViewById(R.id.btn_gif);
        btnTrans = findViewById(R.id.btn_trans);
        btnGif.setOnClickListener(this);
        btnTrans.setOnClickListener(this);

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


    public void switchContent(View view, int postion) {
        Fragment fragment;
        if (view == llHome) {
            if (homeFragment == null) {
                homeFragment = HomeFragment.newInstance();
            }
            fragment = homeFragment;
            llHome.setBackgroundResource(R.drawable.home_buttom_selected);
            llList.setBackgroundResource(R.color.transparent);
            llMine.setBackgroundResource(R.color.transparent);
            tvHome.setTextColor(getResources().getColor(R.color.white));
            tvList.setTextColor(getResources().getColor(R.color.home_buttom_text));
            tvMine.setTextColor(getResources().getColor(R.color.home_buttom_text));
            ivHome.setImageResource(R.drawable.home_selected);
            ivList.setImageResource(R.drawable.list_normal);
            ivMine.setImageResource(R.drawable.mine_normal);
        } else if (view == llList) {
            if (listFragment == null) {
                listFragment = ListFragment.newInstance();
            }
            fragment = listFragment;
            if (postion != 0) {
                listFragment.switchTab(postion);
            }
            llHome.setBackgroundResource(R.color.transparent);
            llList.setBackgroundResource(R.drawable.home_buttom_selected);
            llMine.setBackgroundResource(R.color.transparent);
            tvHome.setTextColor(getResources().getColor(R.color.home_buttom_text));
            tvList.setTextColor(getResources().getColor(R.color.white));
            tvMine.setTextColor(getResources().getColor(R.color.home_buttom_text));
            ivHome.setImageResource(R.drawable.home_normal);
            ivList.setImageResource(R.drawable.list_selected);
            ivMine.setImageResource(R.drawable.mine_normal);
        } else if (view == llMine) {
            if (mineFragment == null) {
                mineFragment = MineFragment.newInstance();
            }
            fragment = mineFragment;
            llHome.setBackgroundResource(R.color.transparent);
            llList.setBackgroundResource(R.color.transparent);
            llMine.setBackgroundResource(R.drawable.home_buttom_selected);
            tvHome.setTextColor(getResources().getColor(R.color.home_buttom_text));
            tvList.setTextColor(getResources().getColor(R.color.home_buttom_text));
            tvMine.setTextColor(getResources().getColor(R.color.white));
            ivHome.setImageResource(R.drawable.home_normal);
            ivMine.setImageResource(R.drawable.list_normal);
            ivMine.setImageResource(R.drawable.mine_selected);
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
                switchContent(llHome, 0);
                break;
            case R.id.ll_list:
                switchContent(llList, 0);
                break;
            case R.id.ll_mine:
                switchContent(llMine, 0);
                break;
            case R.id.btn_gif:
                type = 1;
                Matisse.from(this)
                        .choose(MimeType.of(MimeType.MP4))
                        .countable(true)
                        .maxSelectable(1)
                        //是否只显示选择的类型的缩略图，就不会把所有图片视频都放在一起，而是需要什么展示什么
                        .showSingleMediaType(true)
//                        .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
//                        .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                        .thumbnailScale(0.8f)
                        .imageEngine(new GlideEngine())
                        .showPreview(true) // Default is `true`
                        .forResult(PICK_VIDEO_REQUEST);
                break;
            case R.id.btn_trans:
                type = 2;
                Matisse.from(this)
                        .choose(MimeType.ofVideo())
                        .countable(true)
                        .maxSelectable(1)
//                        .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
//                        .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                        .thumbnailScale(0.5f)
                        .imageEngine(new GlideEngine())
                        .showPreview(true) // Default is `true`
                        .forResult(PICK_VIDEO_REQUEST);
                break;
        }
    }

    public void switchList(int position) {
        switchContent(llList, position);
    }

    private final int PICK_VIDEO_REQUEST = 0x2;

    //    List<Uri> mSelected;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.d(Constant.TAG, " MainActivity onActivityResult : " + requestCode);
        homeFragment.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && null != data) {
//            mSelected = Matisse.obtainResult(data);
//            Uri selectedVideo = mSelected.get(0);
//            String[] filePathColumn = {MediaStore.Video.Media.DATA};
//
////            Cursor cursor = mActivity.getContentResolver().query(selectedVideo,
////                    filePathColumn, null, null, null);
////            cursor.moveToFirst();
//            String path = "";
//            if (DocumentsContract.isDocumentUri(this, selectedVideo)) {
//                // 如果是document类型的Uri，则通过document id处理
//                String docId = DocumentsContract.getDocumentId(selectedVideo);
//                if ("com.android.providers.media.documents".equals(selectedVideo.getAuthority())) {
//                    String id = docId.split(":")[1]; // 解析出数字格式的id
//                    String selection = MediaStore.Video.Media._ID + "=" + id;
//                    path = getPathFromUri(this, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, selection);
//                } else if ("com.android.providers.downloads.documents".equals(selectedVideo.getAuthority())) {
//                    Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
//                    path = getPathFromUri(this, contentUri, null);
//                }
//            } else if ("content".equalsIgnoreCase(selectedVideo.getScheme())) {
//                // 如果是content类型的Uri，则使用普通方式处理
//                path = getPathFromUri(this, selectedVideo, null);
//            } else if ("file".equalsIgnoreCase(selectedVideo.getScheme())) {
//                // 如果是file类型的Uri，直接获取图片路径即可
//                path = selectedVideo.getPath();
//            }
//            String mVideoPath = path;
////            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
////            String mVideoPath = cursor.getString(columnIndex);
////            mVideoPath = "/sdcard/Download/gamepp/KSP.mkv";
////            cursor.close();
////            String mOutPath = Constant.getFilePath() + MediaTool.getVideoName(mVideoPath);
//            String mOutPath;
//            Intent intent;
//            if (type == 1) {
//                mOutPath = Constant.getFilePath() + "gif_" + System.currentTimeMillis();
//                intent = new Intent(this, Video2GifActivity.class);
//            } else {
//                mOutPath = Constant.getFilePath() + "视频转码_" + System.currentTimeMillis();
//                intent = new Intent(this, VideoExtractActivity.class);
//            }
////            mOutPath = Constant.getFilePath() + "音频提取_" + System.currentTimeMillis();
//            intent.putExtra("mVideoPath", mVideoPath);
//            intent.putExtra("mOutPath", mOutPath);
////            startActivity(extract);
//            startActivityForResult(intent, MainActivity.CODE_TO_FUNC);
//        }
    }

    /**
     * 通过Uri和selection来获取真实的图片路径
     *
     * @param act
     * @param uri
     * @param selection
     * @return
     */
    private static String getPathFromUri(Activity act, Uri uri, String selection) {
        String path = null;
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = act.getContentResolver().query(uri, projection, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
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