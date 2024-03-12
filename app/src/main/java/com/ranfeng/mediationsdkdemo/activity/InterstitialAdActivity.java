package com.ranfeng.mediationsdkdemo.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.ranfeng.mediationsdk.ad.RFInterstitialAd;
import com.ranfeng.mediationsdk.ad.data.RFInterstitialAdInfo;
import com.ranfeng.mediationsdk.ad.entity.ExtraParams;
import com.ranfeng.mediationsdk.ad.error.RFError;
import com.ranfeng.mediationsdk.ad.listener.RFInterstitialAdListener;
import com.ranfeng.mediationsdk.util.RFAdUtil;
import com.ranfeng.mediationsdk.util.RFToastUtil;
import com.ranfeng.mediationsdkdemo.R;
import com.ranfeng.mediationsdkdemo.constant.ADRFDemoConstant;

/**
 * @description 插屏广告示例
 */
public class InterstitialAdActivity extends BaseAdActivity implements View.OnClickListener {
    private RFInterstitialAd rfInterstitialAd;
    private RFInterstitialAdInfo rfInterstitialAdInfo;

    private boolean loadAndShow;

    private boolean isLoad;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interstitial_ad);

        initListener();
        initAd();
    }

    private void initListener() {
        Button btnLoadAd = findViewById(R.id.btnLoadAd);
        Button btnShowAd = findViewById(R.id.btnShowAd);
        Button btnLoadAndShowAd = findViewById(R.id.btnLoadAndShowAd);

        btnLoadAd.setOnClickListener(this);
        btnShowAd.setOnClickListener(this);
        btnLoadAndShowAd.setOnClickListener(this);
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
                // 建议在该回调之后展示广告
                Log.d(ADRFDemoConstant.TAG, "onAdReady...");
            }

            @Override
            public void onAdReceive(RFInterstitialAdInfo interstitialAdInfo) {
                InterstitialAdActivity.this.rfInterstitialAdInfo = interstitialAdInfo;
                Log.d(ADRFDemoConstant.TAG, "onAdReceive...");

                if (loadAndShow) {
                    RFAdUtil.showInterstitialAdConvenient(InterstitialAdActivity.this, interstitialAdInfo);
                }

                isLoad = false;
            }

            @Override
            public void onAdExpose(RFInterstitialAdInfo interstitialAdInfo) {
                Log.d(ADRFDemoConstant.TAG, "onAdExpose...");
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

                isLoad = false;
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
                RFAdUtil.showInterstitialAdConvenient(this, rfInterstitialAdInfo);
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
        if (isLoad) {
            RFToastUtil.show(this, "广告加载中...");
            return;
        }
        isLoad = true;

        // 插屏广告场景id（场景id非必选字段，如果需要可到开发者后台创建）
        rfInterstitialAd.setSceneId(ADRFDemoConstant.INTERSTITIAL_AD_SCENE_ID);
        // 加载插屏广告，参数为广告位ID
        rfInterstitialAd.loadAd(ADRFDemoConstant.INTERSTITIAL_AD_POS_ID);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_setting:
                showAdTypeCheckDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void showAdTypeCheckDialog() {
        new AlertDialog.Builder(this)
                .setItems(R.array.interstitial_ad_list, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String toastContent = "设置完成，已生效";
                        switch (which) {
                            case 0:
                                ADRFDemoConstant.INTERSTITIAL_AD_POS_ID = ADRFDemoConstant.INTERSTITIAL_AD_POS_ID1;
                                break;
                            case 1:
                                ADRFDemoConstant.INTERSTITIAL_AD_POS_ID = ADRFDemoConstant.INTERSTITIAL_AD_POS_ID2;
                                ;
                                break;
                            default:
                                break;
                        }
                        RFToastUtil.show(InterstitialAdActivity.this, toastContent);
                        dialog.dismiss();
                    }
                }).create().show();
    }


}
