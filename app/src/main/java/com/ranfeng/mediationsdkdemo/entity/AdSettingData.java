package com.ranfeng.mediationsdkdemo.entity;


public class AdSettingData {


    private int type;
    private String title;

    public AdSettingData(int type, String title) {
        this.type = type;
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }
}
