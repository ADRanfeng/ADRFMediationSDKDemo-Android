package com.ranfeng.mediationsdkdemo.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ranfeng.mediationsdk.ad.RFBannerAd;
import com.ranfeng.mediationsdk.ad.error.RFError;
import com.ranfeng.mediationsdk.ad.listener.RFBannerAdListener;
import com.ranfeng.mediationsdkdemo.constant.ADRFDemoConstant;

import com.ranfeng.mediationsdk.ad.data.AdInfo;
import com.ranfeng.mediationsdkdemo.R;

/**
 * @description Banner广告Fragment示例
 */
public class BannerAdFragment extends BaseFragment {
    private FrameLayout flContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.activity_banner, null);
        flContainer = inflate.findViewById(R.id.flContainer);

        initBannerAd();
        return inflate;
    }

    private void initBannerAd() {
        // 创建Banner广告实例，第一个参数可以是Activity或Fragment，第二个参数是广告容器（请保证容器不会拦截点击、触摸等事件）
        RFBannerAd rfBannerAd = new RFBannerAd(this, flContainer);
        // 设置自刷新时间间隔，0为不自动刷新，其他取值范围为[30,120]，单位秒
        rfBannerAd.setAutoRefreshInterval(ADRFDemoConstant.BANNER_AD_AUTO_REFRESH_INTERVAL);
        // 设置仅支持的广告平台，设置了这个值，获取广告时只会去获取该平台的广告，null或空字符串为不限制，默认为null，方便调试使用，上线时建议不设置。注：仅debug模式为true时生效。
        rfBannerAd.setOnlySupportPlatform(ADRFDemoConstant.BANNER_AD_ONLY_SUPPORT_PLATFORM);
        // 设置Banner广告监听
        rfBannerAd.setListener(new RFBannerAdListener() {
            @Override
            public void onAdReceive(AdInfo adInfo) {
                Log.d(ADRFDemoConstant.TAG, "onAdReceive...");
            }

            @Override
            public void onAdExpose(AdInfo adInfo) {
                Log.d(ADRFDemoConstant.TAG, "onAdExpose...");
            }

            @Override
            public void onAdClick(AdInfo adInfo) {
                Log.d(ADRFDemoConstant.TAG, "onAdClick...");
            }

            @Override
            public void onAdClose(AdInfo adInfo) {
                Log.d(ADRFDemoConstant.TAG, "onAdClose...");
            }

            @Override
            public void onAdFailed(RFError error) {
                if (error != null) {
                    String failedJson = error.toString();
                    Log.d(ADRFDemoConstant.TAG, "onAdFailed..." + failedJson);
                }
            }
        });
        // 加载Banner广告，参数为广告位ID，同一个RFBannerAd只有一次loadAd有效
        rfBannerAd.loadAd(ADRFDemoConstant.BANNER_AD_POS_ID);
    }

    @Override
    public String getTitle() {
        return "Banner";
    }
}
