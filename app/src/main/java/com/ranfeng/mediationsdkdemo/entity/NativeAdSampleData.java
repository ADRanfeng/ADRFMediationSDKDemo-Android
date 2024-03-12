package com.ranfeng.mediationsdkdemo.entity;


import com.ranfeng.mediationsdk.ad.data.RFNativeAdInfo;

/**
 * @description 信息流广告示例数据
 */
public class NativeAdSampleData {
    /**
     * 普通数据
     */
    private String normalData;
    /**
     * 信息流广告对象
     */
    private RFNativeAdInfo nativeAdInfo;

    public NativeAdSampleData(String normalData) {
        this.normalData = normalData;
    }

    public NativeAdSampleData(RFNativeAdInfo nativeAdInfo) {
        this.nativeAdInfo = nativeAdInfo;
    }

    public String getNormalData() {
        return normalData;
    }

    public RFNativeAdInfo getNativeAdInfo() {
        return nativeAdInfo;
    }
}
