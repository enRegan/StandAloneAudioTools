package com.regan.saata.application;

import android.app.Application;

import com.regan.saata.Constant;
import com.regan.saata.util.CrashHandler;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

public class AudioToolsApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
        // 初始化SDK
        UMConfigure.init(this, "5ed128b9570df309ce0000de", Constant.getChannel(this), UMConfigure.DEVICE_TYPE_PHONE, null);
        UMConfigure.setLogEnabled(true);
        // 选用AUTO页面采集模式
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.MANUAL);
    }

}
