package com.regan.saata.bean;

import java.util.List;

public class AvailableMemberList extends BaseBean {
    List<AvailableMember> data;

    public List<AvailableMember> getData() {
        return data;
    }

    public void setData(List<AvailableMember> data) {
        this.data = data;
    }

//    @Override
//    public String toString() {
//        return super.toString()
//                + " data : " + data.toString();
//    }
}
