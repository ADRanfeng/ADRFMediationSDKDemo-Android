package com.ranfeng.mediationsdkdemo.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.ranfeng.mediationsdk.ad.RFInterstitialAd;
import com.ranfeng.mediationsdk.ad.data.RFInterstitialAdInfo;
import com.ranfeng.mediationsdk.ad.entity.ExtraParams;
import com.ranfeng.mediationsdk.ad.error.RFError;
import com.ranfeng.mediationsdk.ad.listener.RFInterstitialAdListener;
import com.ranfeng.mediationsdk.util.RFAdUtil;
import com.ranfeng.mediationsdkdemo.R;
import com.ranfeng.mediationsdkdemo.constant.ADRFDemoConstant;
import com.ranfeng.mediationsdkdemo.manager.RFInterstitialManager;


/**
 * @description 插屏广告示例
 */
public class InterstitialAdAutoCloseActivity extends BaseAdActivity implements View.OnClickListener {
    private RFInterstitialAd rfInterstitialAd;
    private RFInterstitialAdInfo rfInterstitialAdInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interstitial_ad_auto_close);

        initListener();
        initAd();
    }

    private void initListener() {
        Button btnLoadAd = findViewById(R.id.btnLoadAd);
        Button btnShowAd = findViewById(R.id.btnShowAd);
        CheckBox cbAutoClose = findViewById(R.id.cbAutoClose);

        btnLoadAd.setText("获取插屏广告");
        btnShowAd.setText("展示插屏广告");

        btnLoadAd.setOnClickListener(this);
        btnShowAd.setOnClickListener(this);

        cbAutoClose.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                ADRFDemoConstant.INTERSTITIAL_AD_AUTO_CLOSE = b;
            }
        });
    }

    private void initAd() {
        rfInterstitialAd = new RFInterstitialAd(this);
        // 设置仅支持的广告平台，设置了这个值，获取广告时只会去获取该平台的广告，null或空字符串为不限制，默认为null，方便调试使用，上线时建议不设置。注：仅debug模式为true时生效。
        rfInterstitialAd.setOnlySupportPlatform(ADRFDemoConstant.INTERSTITIAL_AD_ONLY_SUPPORT_PLATFORM);
        // 创建额外参数实例
        ExtraParams extraParams = new ExtraParams.Builder()
                // 设置视频类广告是否静音
                .setVideoWithMute(ADRFDemoConstant.INTERSTITIAL_AD_PLAY_WITH_MUTE)
                .build();
        rfInterstitialAd.setLocalExtraParams(extraParams);
        // 设置插屏广告监听
        rfInterstitialAd.setListener(new RFInterstitialAdListener() {
            @Override
            public void onAdReady(RFInterstitialAdInfo interstitialAdInfo) {
                Log.d(ADRFDemoConstant.TAG, "onAdReady...");
            }

            @Override
            public void onAdReceive(RFInterstitialAdInfo interstitialAdInfo) {
                // TODO 插屏广告对象一次成功拉取的广告数据只允许展示一次
                InterstitialAdAutoCloseActivity.this.rfInterstitialAdInfo = interstitialAdInfo;
                Log.d(ADRFDemoConstant.TAG, "onAdReceive...");
            }

            @Override
            public void onAdExpose(RFInterstitialAdInfo interstitialAdInfo) {
                Log.d(ADRFDemoConstant.TAG, "onAdExpose...");

                if (ADRFDemoConstant.INTERSTITIAL_AD_AUTO_CLOSE) {
                    RFInterstitialManager.getInstance().addJumpView(interstitialAdInfo, InterstitialAdAutoCloseActivity.this);
                }
            }

            @Override
            public void onAdClick(RFInterstitialAdInfo interstitialAdInfo) {
                Log.d(ADRFDemoConstant.TAG, "onAdClick...");
            }

            @Override
            public void onAdClose(RFInterstitialAdInfo interstitialAdInfo) {
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLoadAd:
                loadAd();
                break;
            case R.id.btnShowAd:
                RFAdUtil.showInterstitialAdConvenient(this, rfInterstitialAdInfo);
                break;
            default:
                break;
        }
    }

    /**
     * 加载广告
     */
    private void loadAd() {
        // 插屏广告场景id（场景id非必选字段，如果需要可到开发者后台创建）
        rfInterstitialAd.setSceneId(ADRFDemoConstant.INTERSTITIAL_AD_SCENE_ID);
        // 加载插屏广告，参数为广告位ID
        rfInterstitialAd.loadAd(ADRFDemoConstant.INTERSTITIAL_AD_POS_ID);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ADRFDemoConstant.INTERSTITIAL_AD_AUTO_CLOSE = false;
    }
}
