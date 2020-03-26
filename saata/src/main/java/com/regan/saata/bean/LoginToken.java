package com.regan.saata.bean;

public class LoginToken extends BaseBean {
    private String loginToken;

    public String getLoginToken() {
        return loginToken;
    }

    public void setLoginToken(String loginToken) {
        this.loginToken = loginToken;
    }

    @Override
    public String toString() {
        return super.toString() + " loginToken: " + loginToken;
    }
}
