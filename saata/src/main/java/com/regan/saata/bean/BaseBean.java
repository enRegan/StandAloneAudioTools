package com.regan.saata.bean;

public class BaseBean {
    //    {"code":"0000","msg":"成功","count":null,}
    private String code;
    private String msg;
    private long count;
//    private JSONObject data;

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

    @Override
    public String toString() {
        return "code :" + code;
    }

    //    public JSONObject getData() {
//        return data;
//    }
//
//    public void setData(JSONObject data) {
//        this.data = data;
//    }
}
