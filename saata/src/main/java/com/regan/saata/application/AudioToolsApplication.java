package com.regan.saata.application;

import android.app.Application;

import com.regan.saata.util.CrashHandler;

public class AudioToolsApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
    }

}
