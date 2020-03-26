package com.regan.saata.bean;

public class AppidInfo extends BaseBean {
    private String wxAppId;
    private String zfbAppId;
    private String qqAppId;

    public String getWxAppId() {
        return wxAppId;
    }

    public void setWxAppId(String wxAppId) {
        this.wxAppId = wxAppId;
    }

    public String getZfbAppId() {
        return zfbAppId;
    }

    public void setZfbAppId(String zfbAppId) {
        this.zfbAppId = zfbAppId;
    }

    public String getQqAppId() {
        return qqAppId;
    }

    public void setQqAppId(String qqAppId) {
        this.qqAppId = qqAppId;
    }

    @Override
    public String toString() {
        return super.toString() + " wxAppId : " + wxAppId
                + " \nzfbAppId : " + zfbAppId
                + " \nqqAppId : " + qqAppId;
    }
}
