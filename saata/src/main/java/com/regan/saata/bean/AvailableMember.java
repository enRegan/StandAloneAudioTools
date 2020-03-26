package com.regan.saata.bean;

public class AvailableMember extends BaseBean {
//    "createTime": "创建时间",
//            "memberCount": "会员次数（按次数会员返回）",
//            "memberId": "商品ID",
//            "memberName": "商品名称",
//            "memberPrice": "会员（商品金额，单位元）",
//            "memberTime": "会员时长（单位：月）",
//            "memberType": "会员类型：NORMAL_VIP(\"普通会员\"),  \tFOREVER_VIP(\"永久会员\"),  \tEXPREIENCE_VIP(\"体验会员\")",
//            "recommend": "是否推荐",
//            "remark1": "备注1",
//            "remark2": "备注2",
//            "state": "状态：UP_SHOP(\"上架\"),  \tDOWN_SHOP(\"下架\"), \t \tNOT_FOR_SALE(\"非卖品\"),  \tDISABLE(\"禁用\")",
//            "str1": "预留字段1",
//            "str2": "预留字段2",
//            "updateTime": "更新时间"

    private String createTime;
    private String memberCount;
    private String memberId;
    private String memberName;
    private String memberPrice;
    private String memberTime;
    private String memberType;
    private boolean recommend;
    private String remark1;
    private String remark2;
    private String state;
    private String str1;
    private String str2;
    private String updateTime;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(String memberCount) {
        this.memberCount = memberCount;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getMemberPrice() {
        return memberPrice;
    }

    public void setMemberPrice(String memberPrice) {
        this.memberPrice = memberPrice;
    }

    public String getMemberTime() {
        return memberTime;
    }

    public void setMemberTime(String memberTime) {
        this.memberTime = memberTime;
    }

    public String getMemberType() {
        return memberType;
    }

    public void setMemberType(String memberType) {
        this.memberType = memberType;
    }

    public boolean isRecommend() {
        return recommend;
    }

    public void setRecommend(boolean recommend) {
        this.recommend = recommend;
    }

    public String getRemark1() {
        return remark1;
    }

    public void setRemark1(String remark1) {
        this.remark1 = remark1;
    }

    public String getRemark2() {
        return remark2;
    }

    public void setRemark2(String remark2) {
        this.remark2 = remark2;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStr1() {
        return str1;
    }

    public void setStr1(String str1) {
        this.str1 = str1;
    }

    public String getStr2() {
        return str2;
    }

    public void setStr2(String str2) {
        this.str2 = str2;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return super.toString() + " \n createTime : " + getCreateTime()
                + "\n memberCount : " + getMemberCount()
                + "\n memberId : " + getMemberId()
                + "\n memberName : " + getMemberName()
                + "\n memberPrice : " + getMemberPrice()
                + "\n memberTime : " + getMemberTime()
                + "\n memberType : " + getMemberType()
                + "\n recommend : " + isRecommend()
                + "\n remark1 : " + getRemark1()
                + "\n remark2 : " + getRemark2()
                + "\n state : " + getState()
                + "\n str1 : " + getStr1()
                + "\n str2 : " + getStr2()
                + "\n updateTime : " + getUpdateTime();
    }
}
