package com.regan.saata.bean;

import com.regan.saata.util.FileSizeUtil;
import com.regan.saata.util.TimeUtils;

import java.io.Serializable;

public class VideoInfo implements Serializable {
    public int id;
    public String path;
    public String name;
    public String size;
    public String time;
    public String params;
    public String type;
    public long lastModified;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public String toString() {
        return " \nname : " + name
                + " \npath : " + path
                + " \nsize : " + FileSizeUtil.getAutoFileOrFilesSize(path)
                + " \ntime : " + TimeUtils.secondToTime(Long.parseLong(time) / 1000)
                + " \nparams : " + params
                + " \ntype : " + type
                + " \nlastModified : " + lastModified;
    }

}