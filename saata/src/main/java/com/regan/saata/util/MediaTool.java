package com.regan.saata.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.content.FileProvider;


import com.regan.saata.Constant;

import java.io.File;

public class MediaTool {

    public static Bitmap getVideoFrame(String path, long timeUs) {
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        try {
            media.setDataSource(path);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return media.getFrameAtTime(timeUs);
    }

    public static String getVideoName(String path) {
        return path.substring(path.lastIndexOf("/") + 1, path.indexOf("."));
    }

    public static void insertMedia(final Context context, final String filePath) {
        if (!checkFile(filePath))
            return;
        //保存图片后发送广播通知更新数据库
        Uri uri = Uri.fromFile(new File(filePath));
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
//        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//            @Override
//            public void run() {
////                Uri uri = Uri.parse(filePath);
////                Intent intent = new Intent(Intent.ACTION_VIEW);
////                intent.setDataAndType(uri, "audio/*");
////                try {
////                    ((BaseActivity)context).startActivity(intent);
////                } catch (Exception e) {
////                    e.printStackTrace();
////                }
//                openFile(context, filePath);
//            }
//        }, 10);
    }

    public static void openFile(Context context, String filePath) {
        Intent intent = new Intent();
        File cameraPhoto = new File(filePath);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //  此处注意替换包名，
            Uri contentUri = FileProvider.getUriForFile(
                    context,
                    context.getPackageName() + ".fileprovider",
                    cameraPhoto);
            LogUtils.d(Constant.TAG, " uri   " + contentUri.getPath());
            intent.setDataAndType(contentUri, "audio/*");
//            intent.setDataAndType(contentUri, "image/*");
        } else {
            intent.setDataAndType(Uri.fromFile(cameraPhoto), "audio/*");//也可使用 Uri.parse("file://"+file.getAbsolutePath());
        }

        //以下设置都不是必须的
        intent.setAction(Intent.ACTION_VIEW);// 系统根据不同的Data类型，通过已注册的对应Application显示匹配的结果。
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//系统会检查当前所有已创建的Task中是否有该要启动的Activity的Task
        //若有，则在该Task上创建Activity；若没有则新建具有该Activity属性的Task，并在该新建的Task上创建Activity。
        intent.addCategory(Intent.CATEGORY_DEFAULT);//按照普通Activity的执行方式执行
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);
    }

    private static boolean checkFile(String filePath) {
        File file = new File(filePath);
        boolean result = file.exists();
        if (!result) {
            Log.e("MediaTool", "文件不存在 path = " + filePath);
        }
        return result;
    }


}
