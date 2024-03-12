package com.ranfeng.mediationsdkdemo.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;


import com.ranfeng.mediationsdk.ad.RFBannerAd;
import com.ranfeng.mediationsdk.ad.data.AdInfo;
import com.ranfeng.mediationsdk.ad.error.RFError;
import com.ranfeng.mediationsdk.ad.listener.RFBannerAdListener;
import com.ranfeng.mediationsdkdemo.R;
import com.ranfeng.mediationsdkdemo.constant.ADRFDemoConstant;

/**
 * @description Banner广告示例
 */
public class BannerAdActivity extends BaseAdActivity {

    private FrameLayout flContainer;
    private Button jumpTestBannerPause;
    private RFBannerAd rfBannerAd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);
        flContainer = findViewById(R.id.flContainer);
        jumpTestBannerPause = findViewById(R.id.jumpTestBannerPause);

        loadBanner();

        initListener();
    }

    private void initListener() {
        jumpTestBannerPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BannerAdActivity.this, NativeAdActivity.class);
                startActivity(intent);
            }
        });
    }

    public void loadBanner() {
        if (rfBannerAd != null) {
            rfBannerAd.release();
            rfBannerAd = null;
        }
        // 创建Banner广告实例，第一个参数可以是Activity或Fragment，第二个参数是广告容器（请保证容器不会拦截点击、触摸等事件）
        rfBannerAd = new RFBannerAd(this, flContainer);
        // 设置自刷新时间间隔，0为不自动刷新（部分平台无效，如百度），其他取值范围为[30,120]，单位秒
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
                flContainer.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        ViewTreeObserver vto = flContainer.getViewTreeObserver();
                        if (!vto.isAlive()) {
                            return true;
                        }
                        int height = flContainer.getHeight();
                        int width = flContainer.getWidth();

                        Log.d("flContainer", "width:" + width + " height:" + height);

                        vto.removeOnPreDrawListener(this);

                        return false;
                    }

                });
            }

            @Override
            public void onAdClick(AdInfo adInfo) {
                Log.d(ADRFDemoConstant.TAG, "onAdClick...");
            }

            @Override
            public void onAdClose(AdInfo adInfo) {
                Log.d(ADRFDemoConstant.TAG, "onAdClose...");
                if (flContainer != null) {
                    flContainer.removeAllViews();
                    // flContainer.setVisibility(View.GONE);
                }

                if (rfBannerAd != null) {
                    rfBannerAd.release();
                }
                if (adInfo != null) {
                    adInfo.release();
                }
            }

            @Override
            public void onAdFailed(RFError error) {
                if (error != null) {
                    String failedJson = error.toString();
                    Log.d(ADRFDemoConstant.TAG, "onAdFailed..." + failedJson);
                }
            }
        });
        // banner广告场景id（场景id非必选字段，如果需要可到开发者后台创建）
        rfBannerAd.setSceneId(ADRFDemoConstant.BANNER_AD_SCENE_ID);
        // 加载Banner广告，参数为广告位ID，同一个RFBannerAd只有一次loadAd有效
        rfBannerAd.loadAd(ADRFDemoConstant.BANNER_AD_POS_ID);
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
            .setItems(R.array.banner_ad_list, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String toastContent = "设置完成，生效中";
                    switch (which) {
                        case 0:
                            ADRFDemoConstant.BANNER_AD_POS_ID = ADRFDemoConstant.BANNER_AD_POS_ID1;
                            break;
                        case 1:
                            ADRFDemoConstant.BANNER_AD_POS_ID = ADRFDemoConstant.BANNER_AD_POS_ID2;;
                            break;
                        case 2:
                            ADRFDemoConstant.BANNER_AD_POS_ID = ADRFDemoConstant.BANNER_AD_POS_ID3;
                        default:
                            break;
                    }
                    Toast.makeText(BannerAdActivity.this, toastContent, Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    loadBanner();
                }
            }).create().show();
    }

}
