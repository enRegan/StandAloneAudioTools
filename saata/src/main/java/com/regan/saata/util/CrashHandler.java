package com.regan.saata.util;


import android.content.Context;
import android.os.Environment;
import android.os.Looper;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    public static final String TAG = "CrashHandler";
    private static CrashHandler instance = new CrashHandler();
    private Context mContext;
    private Thread.UncaughtExceptionHandler mDefaultHandler = null;
    public static final String CRASH_DIR = Environment.getExternalStorageDirectory().getPath() + "/audiotools/crash/";

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        return instance;
    }

    public void init(Context ctx) {
        mContext = ctx;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, final Throwable ex) {
//        if (ex == null || mContext == null) {
//            return;
//        }

        if (!handleException(ex) && mDefaultHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        }

    }


    private boolean handleException(Throwable ex) {
        if (ex == null || mContext == null) {
            return false;
        }
        //使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, "程序异常，正在重启", Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();

        //在此可执行其它操作，如获取设备信息、执行异常登出请求、保存错误日志到本地或发送至服务端
//        new Thread() {
//            @Override
//            public void run() {
        LogUtils.d(TAG, "全局Exception, 重启软件");

        saveCrashInfoToFile(ex);
//            }
//        }.start();
        return true;
    }

    private void saveCrashInfoToFile(Throwable ex) {
        PrintWriter printWriter = null;
        FileOutputStream trace = null;

        try {
            Writer info = new StringWriter();
            printWriter = new PrintWriter(info);
            ex.printStackTrace(printWriter);
            Throwable cause = ex.getCause();
            while (cause != null) {
                cause.printStackTrace(printWriter);
                cause = cause.getCause();
            }

            String result = info.toString();
            LogUtils.i(TAG, result);
            String fileName = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss").format(new Date()) + ".txt";
            String filePath = CRASH_DIR + fileName;
            new File(filePath).getParentFile().mkdirs();
            trace = new FileOutputStream(filePath);
            trace.write(result.getBytes());
            trace.flush();

        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.i(TAG, ExceptionsUtils.getStackTrace(e));
        } finally {
            if (printWriter != null) {
                printWriter.close();
            }
            if (trace != null) {
                try {
                    trace.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}