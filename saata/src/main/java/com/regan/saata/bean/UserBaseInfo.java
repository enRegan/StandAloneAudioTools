package com.regan.saata.bean;

public class UserBaseInfo extends BaseBean {
    //    {
//        "code": "返回码",
//            "count": "",
//            "data": {
//        "avatarUrl": "用户头像地址",
//                "createTime": "创建时间",
//                "gender": "用户性别",
//                "loginNo": "用户号",
//                "loginType": "登录方式（LOGIN_MOBILE(\"手机号登录\"),LOGIN_WX(\"微信登录\"),LOGIN_VISITOR(\"游客登录\")）",
//                "nickName": "昵称",
//                "phone": "手机号",
//                "state": "用户状态(启用：ENABLE,禁用：DISABLE)",
//                "unionid": "微信开放平台的唯一标识符",
//                "updateTime": "更新时间",
//                "userId": "用户ID"
//    },
//        "msg": "返回码描述"
//    }
    private String avatarUrl;
    private String createTime;
    private String gender;
    private String loginNo;
    private String loginType;
    private String nickName;
    private String phone;
    private String state;
    private String unionid;
    private String updateTime;
    private String userId;

    @Override
    public String toString() {
        return super.toString() + "  avatarUrl : " + getAvatarUrl()
                + "\n createTime : " + getCreateTime()
                + "\n gender : " + getGender()
                + "\n loginNo : " + getLoginNo()
                + "\n loginType : " + getLoginType()
                + "\n nickName : " + getNickName()
                + "\n phone : " + getPhone()
                + "\n state : " + getState()
                + "\n unionid : " + getUnionid()
                + "\n updateTime : " + getUpdateTime()
                + "\n userId : " + getUserId();
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLoginNo() {
        return loginNo;
    }

    public void setLoginNo(String loginNo) {
        this.loginNo = loginNo;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
