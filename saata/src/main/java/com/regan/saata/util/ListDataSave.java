package com.regan.saata.util;

import android.content.Context;
import android.content.SharedPreferences;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.regan.saata.bean.AudioInfo;

import java.util.ArrayList;
import java.util.List;

public class ListDataSave {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public ListDataSave(Context mContext, String preferenceName) {
        preferences = mContext.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    /**
     * 保存List
     *
     * @param tag
     * @param datalist
     */
    public <T> void setDataList(String tag, List<T> datalist) {
        if (null == datalist || datalist.size() <= 0)
            return;

        //转换成json数据，再保存
        String strJson = JSON.toJSONString(datalist);
        editor.clear();
        editor.putString(tag, strJson);
        editor.commit();

    }

    /**
     * 获取List
     *
     * @param tag
     * @return
     */
    public List<AudioInfo> getDataList(String tag) {
        List<AudioInfo> datalist = new ArrayList<AudioInfo>();
        String strJson = preferences.getString(tag, null);
        if (null == strJson) {
            return datalist;
        }

        datalist = JSONArray.parseArray(strJson, AudioInfo.class);
        return datalist;

    }

    public <T> List<T> getDataList(String tag, Class<T> t) {
        List<T> datalist = new ArrayList<T>();
        String strJson = preferences.getString(tag, null);
//        LogUtils.d(Constant.TAG, " strJson: " + strJson);
        if (null == strJson) {
            return datalist;
        }

        datalist = JSONArray.parseArray(strJson, t);
        return datalist;

    }
}
