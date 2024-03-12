package com.ranfeng.mediationsdkdemo.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.ranfeng.mediationsdk.ad.RFRewardVodAd;
import com.ranfeng.mediationsdk.ad.data.RFRewardVodAdInfo;
import com.ranfeng.mediationsdk.ad.entity.ExtraParams;
import com.ranfeng.mediationsdk.ad.entity.RewardExtra;
import com.ranfeng.mediationsdk.ad.error.RFError;
import com.ranfeng.mediationsdk.ad.listener.RFRewardVodAdListener;
import com.ranfeng.mediationsdk.util.RFAdUtil;
import com.ranfeng.mediationsdkdemo.R;
import com.ranfeng.mediationsdkdemo.constant.ADRFDemoConstant;

/**
 * @description 激励视频广告示例
 */
public class RewardVodAdActivity extends BaseAdActivity implements View.OnClickListener {
    private RFRewardVodAdInfo rfRewardVodAdInfo;
    private RFRewardVodAd rfRewardVodAd;

    private boolean loadAndShow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_vod);
        initListener();
        initAd();
    }

    private void initListener() {
        findViewById(R.id.btnLoadAd).setOnClickListener(this);
        findViewById(R.id.btnShowAd).setOnClickListener(this);
        findViewById(R.id.btnLoadAndShowAd).setOnClickListener(this);
    }

    private void initAd() {
        // 创建激励视频广告实例
        rfRewardVodAd = new RFRewardVodAd(this);
        RewardExtra rewardExtra = new RewardExtra("userId");
        rewardExtra.setCustomData("额外参数");
        rewardExtra.setRewardName("激励名称");
        rewardExtra.setRewardAmount(1);

        // 创建额外参数实例
        ExtraParams extraParams = new ExtraParams.Builder()
                .rewardExtra(rewardExtra)
                // 设置视频类广告是否静音
                .setVideoWithMute(ADRFDemoConstant.REWARD_AD_PLAY_WITH_MUTE)
                .build();

        rfRewardVodAd.setLocalExtraParams(extraParams);
        // 设置仅支持的广告平台，设置了这个值，获取广告时只会去获取该平台的广告，null或空字符串为不限制，默认为null，方便调试使用，上线时建议不设置。注：仅debug模式为true时生效。
        rfRewardVodAd.setOnlySupportPlatform(ADRFDemoConstant.REWARD_VOD_AD_ONLY_SUPPORT_PLATFORM);
        // 设置激励视频广告监听
        rfRewardVodAd.setListener(new RFRewardVodAdListener() {
            @Override
            public void onVideoCache(RFRewardVodAdInfo adRewardVodAdInfo) {
                // 目前汇量走了该回调之后才准备好
                Log.d(ADRFDemoConstant.TAG, "onVideoCache...");
            }

            @Override
            public void onVideoComplete(RFRewardVodAdInfo adRewardVodAdInfo) {
                Log.d(ADRFDemoConstant.TAG, "onVideoComplete...");
            }

            @Override
            public void onVideoError(RFRewardVodAdInfo adRewardVodAdInfo, RFError error) {
                Log.d(ADRFDemoConstant.TAG, "onVideoError..." + error.toString());
            }

            @Override
            public void onReward(RFRewardVodAdInfo adRewardVodAdInfo) {
                Log.d(ADRFDemoConstant.TAG, "onReward...");
            }

            @Override
            public void onAdReceive(RFRewardVodAdInfo rewardVodAdInfo) {
                RewardVodAdActivity.this.rfRewardVodAdInfo = rewardVodAdInfo;
                Log.d(ADRFDemoConstant.TAG, "onAdReceive...");
                if (loadAndShow) {
                    RFAdUtil.showRewardVodAdConvenient(RewardVodAdActivity.this, rewardVodAdInfo, false);
                }
            }

            @Override
            public void onAdExpose(RFRewardVodAdInfo adRewardVodAdInfo) {
                Log.d(ADRFDemoConstant.TAG, "onAdExpose...");
            }

            @Override
            public void onAdClick(RFRewardVodAdInfo adRewardVodAdInfo) {
                Log.d(ADRFDemoConstant.TAG, "onAdClick...");
            }

            @Override
            public void onAdClose(RFRewardVodAdInfo adRewardVodAdInfo) {
                Log.d(ADRFDemoConstant.TAG, "onAdClose...");
            }

            @Override
            public void onAdFailed(RFError error) {
                if (error != null) {
                    String failedJosn = error.toString();
                    Log.d(ADRFDemoConstant.TAG, "onAdFailed..." + failedJosn);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLoadAd:
                loadAndShow = false;
                loadAd();
                break;
            case R.id.btnShowAd:
                RFAdUtil.showRewardVodAdConvenient(this, rfRewardVodAdInfo, false);
                break;
            case R.id.btnLoadAndShowAd:
                loadAndShow = true;
                loadAd();
                break;
            default:
                break;
        }
    }

    /**
     * 加载广告
     */
    private void loadAd() {
        // 激励广告场景id（场景id非必选字段，如果需要可到开发者后台创建）
        rfRewardVodAd.setSceneId(ADRFDemoConstant.REWARD_VOD_AD_SCENE_ID);
        // 加载激励视频广告，参数为广告位ID
        rfRewardVodAd.loadAd(ADRFDemoConstant.REWARD_VOD_AD_POS_ID);
    }

}
