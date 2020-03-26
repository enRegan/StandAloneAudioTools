package com.regan.saata.bean;

public class OrderMemberSuc extends BaseBean {
    String orderState;//订单状态（CREATED("订单创建"),
    // WAIT_BUYER_PAY("交易创建，等待买家付款"),
    // TRADE_SUCCESS("交易支付成功"),
    // TRADE_FINISHED("交易结束，不可退款"),
    // TRADE_CLOSED("未付款交易超时关闭，或支付完成后全额退款")）

    public String getOrderState() {
        return orderState;
    }

    public void setOrderState(String orderState) {
        this.orderState = orderState;
    }

    @Override
    public String toString() {
        return super.toString() + " orderState : " + orderState;
    }
}
