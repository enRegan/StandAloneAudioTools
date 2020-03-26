package com.regan.saata.bean;

public class OrderMember extends BaseBean {
    private String orderId;//系统内部订单号
    private String orderString;//支付宝/微信订单字符串,可根据该订单字符串直接使用sdk调用支付宝/微信发起支付
    private String payType;//ALIAPP:支付宝app支付,WXAPP:微信APP支付

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderString() {
        return orderString;
    }

    public void setOrderString(String orderString) {
        this.orderString = orderString;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    @Override
    public String toString() {
        return super.toString() + " orderId : " + orderId
                + " \norderString : " + orderString
                + " \npayType : " + payType;
    }
}
