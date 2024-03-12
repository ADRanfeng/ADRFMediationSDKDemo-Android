package com.ranfeng.mediationsdkdemo;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;

import com.bytedance.sdk.openadsdk.stub.activity.Stub_Standard_Landscape_Activity;
import com.bytedance.sdk.openadsdk.stub.activity.Stub_Standard_Portrait_Activity;
import com.ranfeng.mediationsdkdemo.activity.SettingActivity;
import com.ranfeng.mediationsdkdemo.manager.RFInterstitialManager;
import com.ranfeng.mediationsdkdemo.util.SPUtil;

import com.ranfeng.mediationsdkdemo.constant.ADRFDemoConstant;


public class ADRFApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setOnlySupportPlatform();

        interstitialAddClose();
    }

    private void interstitialAddClose() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, Bundle savedInstanceState) {
                if (ADRFDemoConstant.INTERSTITIAL_AD_AUTO_CLOSE) {
                    if (activity instanceof Stub_Standard_Portrait_Activity
                            || activity instanceof Stub_Standard_Landscape_Activity) {
                        // 将头条activity放置到插屏管理类中，用于倒计时
                        RFInterstitialManager.getInstance().setAdInterstitialActivity(activity);
                    }
                }
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {}

            @Override
            public void onActivityResumed(@NonNull Activity activity) {}

            @Override
            public void onActivityPaused(@NonNull Activity activity) {}

            @Override
            public void onActivityStopped(@NonNull Activity activity) {}

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {}

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {}
        });
    }

    /**
     * 设置仅仅支持平台
     */
    private void setOnlySupportPlatform() {
        String onlySupportPlatform = SPUtil.getString(this, SettingActivity.KEY_ONLY_SUPPORT_PLATFORM, null);
        ADRFDemoConstant.SPLASH_AD_ONLY_SUPPORT_PLATFORM = onlySupportPlatform;
        ADRFDemoConstant.BANNER_AD_ONLY_SUPPORT_PLATFORM = onlySupportPlatform;
        ADRFDemoConstant.NATIVE_AD_ONLY_SUPPORT_PLATFORM = onlySupportPlatform;
        ADRFDemoConstant.REWARD_VOD_AD_ONLY_SUPPORT_PLATFORM = onlySupportPlatform;
        ADRFDemoConstant.INTERSTITIAL_AD_ONLY_SUPPORT_PLATFORM = onlySupportPlatform;
    }

}
