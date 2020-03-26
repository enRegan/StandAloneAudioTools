package com.regan.saata.bean;

public class SmsToken extends BaseBean {
    private String smsToken;

    public String getSmsToken() {
        return smsToken;
    }

    public void setSmsToken(String smsToken) {
        this.smsToken = smsToken;
    }

    @Override
    public String toString() {
        return super.toString() + " smsToken : " + smsToken;
    }
}
