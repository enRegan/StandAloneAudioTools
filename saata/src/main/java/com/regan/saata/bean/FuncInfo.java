package com.regan.saata.bean;

public class FuncInfo {
    public String name;
    public int iconSrc;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIconSrc() {
        return iconSrc;
    }

    public void setIconSrc(int iconSrc) {
        this.iconSrc = iconSrc;
    }

    @Override
    public String toString() {
        return "name : " + name
                + " \niconSrc : " + iconSrc;
    }
}
