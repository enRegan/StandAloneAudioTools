package com.regan.saata;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;


import com.regan.saata.bean.MediaInfo;

import java.io.File;
import java.util.List;

public class Constant {
    public static String TAG = "Video";
    // APP_ID 替换为你的应用从官方网站申请到的合法appID
    public static String WX_APPID = "wx_appid";
    public static String VIDEO_LIST = "video_list";
    public static String GIF_LIST = "gif_list";
    public static String AUDIO_LIST = "audio_list";
    public static String VIDEO = "video";
    public static String GIF = "gif";
    public static String AUDIO = "audio";
    public static int FFMPEG_TIMEOUT = 6000 * 1000;
    public static String USER_PROTOCOL = "http://guanwang.fengdunsh.com/pro/fengdun_audioconver_userProtocol.html";
    public static String SAFE_PROTOCOL = "http://guanwang.fengdunsh.com/pro/fengdun_audioconver_privacyProtocol.html";

    public static String getChannel(Context context) {
        String channel = "";
        PackageManager pm = context.getPackageManager();
        try {
            ApplicationInfo ai = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            String value = ai.metaData.getString("UMENG_CHANNEL");
            if (value != null) {
                channel = value;
            }
        } catch (Exception e) {
            // 忽略找不到包信息的异常
        }
        return channel;
    }

    public static String getVersion(Context context) {//获取版本号
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static int getVersionCode(Context context) {//获取版本号(内部识别号)
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }


    public static String getFilePath() {
        String path = null;
        File folder = new File(Environment.getExternalStorageDirectory().getPath() + "/视频转音频文件夹/transcode/");
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


    public static float time2Float(String time) {
        float pro = -1f;
        String[] p = time.split(":");
        float hour = Float.parseFloat(p[0]);
        float min = Float.parseFloat(p[1]);
        float seconds = Float.parseFloat(p[2]);
        pro = hour * 60 * 60 + min * 60 + seconds;
        return pro;
    }

    public static int time2Int(String time) {
        int pro = -1;
        String[] p = time.split(":");
        float hour = Float.parseFloat(p[0]);
        float min = Float.parseFloat(p[1]);
        float seconds = Float.parseFloat(p[2]);
        pro = Float.valueOf(hour * 3600 + min * 60 + seconds * 1).intValue();
        return pro;
    }
}
