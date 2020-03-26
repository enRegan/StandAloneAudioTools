package com.regan.saata.bean;

public class TestBean {
    //    {"code":"0000","msg":"成功","count":null,}
    public <T> TestBean(T t) {
        data = t;
    }

    private String code;
    private String msg;
    private long count;
    private Object data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    //    public JSONObject getData() {
//        return data;
//    }
//
//    public void setData(JSONObject data) {
//        this.data = data;
//    }
}
