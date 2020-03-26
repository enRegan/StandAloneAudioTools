package com.regan.saata.bean;

public class UserVipInfo extends BaseBean {
    //    {
//        "code": "返回码",
//            "count": "",
//            "data": {
//        "createTime": "创建时间",
//                "endTime": "vip结束时间",
//                "memberType": "会员类型：NORMAL_VIP(\"普通会员\"),FOREVER_VIP(\"永久会员\"),EXPREIENCE_VIP(\"体验会员\")",
//                "remainCount": "vip剩余次数",
//                "startTime": "vip开始时间",
//                "state": "会员状态：NORMAL(\"正常\"),  EXPIRE(\"过期\")",
//                "updateTime": "更新时间",
//                "userId": "用户ID"
//    },
//        "msg": "返回码描述"
//    }
    private String createTime;
    private String endTime;
    private String memberType;
    private String remainCount;
    private String startTime;
    private String state;
    private String updateTime;
    private String userId;

    @Override
    public String toString() {
        return super.toString() + "  createTime : " + getCreateTime()
                + "\n endTime : " + getEndTime()
                + "\n memberType : " + getMemberType()
                + "\n remainCount : " + getRemainCount()
                + "\n startTime : " + getStartTime()
                + "\n state : " + getState()
                + "\n updateTime : " + getUpdateTime()
                + "\n userId : " + getUserId();
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getMemberType() {
        return memberType;
    }

    public void setMemberType(String memberType) {
        this.memberType = memberType;
    }

    public String getRemainCount() {
        return remainCount;
    }

    public void setRemainCount(String remainCount) {
        this.remainCount = remainCount;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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
