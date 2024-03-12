package com.ranfeng.mediationsdkdemo.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.ranfeng.mediationsdk.ad.RFNativeAd;
import com.ranfeng.mediationsdk.ad.data.RFNativeAdInfo;
import com.ranfeng.mediationsdk.ad.error.RFError;
import com.ranfeng.mediationsdk.ad.listener.RFNativeAdListener;
import com.ranfeng.mediationsdk.util.RFDisplayUtil;
import com.ranfeng.mediationsdk.util.RFToastUtil;
import com.ranfeng.mediationsdkdemo.adapter.NativeAdAdapter;
import com.ranfeng.mediationsdkdemo.entity.NativeAdSampleData;
import com.ranfeng.mediationsdkdemo.widget.MySmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

import com.ranfeng.mediationsdkdemo.R;

import com.ranfeng.mediationsdkdemo.constant.ADRFDemoConstant;

import com.ranfeng.mediationsdk.ad.entity.AdSize;
import com.ranfeng.mediationsdk.ad.entity.ExtraParams;

/**
 * @description 信息流广告示例
 */
public class NativeAdActivity extends BaseAdActivity implements OnRefreshLoadMoreListener {
    private MySmartRefreshLayout refreshLayout;
    private NativeAdAdapter nativeAdAdapter;
    private RFNativeAd rfNativeAd;
    private List<NativeAdSampleData> tempDataList = new ArrayList<>();
    private int refreshType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_ad);
        initView();
        initListener();
        initAd();
    }

    private void initView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        refreshLayout = findViewById(R.id.refreshLayout);

        nativeAdAdapter = new NativeAdAdapter(this);
        recyclerView.setAdapter(nativeAdAdapter);
    }

    private void initListener() {
        refreshLayout.setOnRefreshLoadMoreListener(this);
    }

    private void initAd() {
        int widthPixels = getResources().getDisplayMetrics().widthPixels - RFDisplayUtil.dp2px(20);
        // 创建信息流广告实例
        rfNativeAd = new RFNativeAd(this);

        // 创建额外参数实例
        ExtraParams extraParams = new ExtraParams.Builder()
                // 设置整个广告视图预期宽高(目前仅头条平台需要，没有接入头条可不设置)，单位为px，高度如果小于等于0则高度自适应
                .adSize(new AdSize(widthPixels, 0))
//                .nativeStyle(adNativeStyle)
                // 设置信息流广告适配播放是否静音，默认静音，目前优量汇、百度、汇量、快手、Admobile支持修改
                .nativeAdPlayWithMute(ADRFDemoConstant.NATIVE_AD_PLAY_WITH_MUTE)
                .build();
        // 设置一些额外参数，有些平台的广告可能需要传入一些额外参数，如果有接入头条平台，如果包含这些平台该参数必须设置
        rfNativeAd.setLocalExtraParams(extraParams);

        // 设置仅支持的广告平台，设置了这个值，获取广告时只会去获取该平台的广告，null或空字符串为不限制，默认为null，方便调试使用，上线时建议不设置。注：仅debug模式为true时生效。
        rfNativeAd.setOnlySupportPlatform(ADRFDemoConstant.NATIVE_AD_ONLY_SUPPORT_PLATFORM);
        // 设置广告监听
        rfNativeAd.setListener(new RFNativeAdListener() {
            @Override
            public void onRenderFailed(RFNativeAdInfo nativeAdInfo, RFError error) {
                Log.d(ADRFDemoConstant.TAG, "onRenderFailed: " + error.toString());
                // 广告渲染失败，释放和移除RFNativeAdInfo
                nativeAdAdapter.removeData(nativeAdInfo);
            }

            @Override
            public void onAdReceive(List<RFNativeAdInfo> adInfoList) {
                Log.d(ADRFDemoConstant.TAG, "onAdReceive: " + adInfoList.size());
                for (int i = 0; i < adInfoList.size(); i++) {
                    int index = i * 5;
                    RFNativeAdInfo nativeAdInfo = adInfoList.get(i);
                    if (index >= tempDataList.size()) {
                        tempDataList.add(new NativeAdSampleData(nativeAdInfo));
                    } else {
                        tempDataList.add(index, new NativeAdSampleData(nativeAdInfo));
                    }
                    Log.d(ADRFDemoConstant.TAG, "onAdReceive hash code: " + adInfoList.get(i).hashCode());
                }
                nativeAdAdapter.addData(tempDataList);
                refreshLayout.finish(refreshType, true, false);
            }

            @Override
            public void onAdExpose(RFNativeAdInfo nativeAdInfo) {
                Log.d(ADRFDemoConstant.TAG, "onAdExpose: " + nativeAdInfo.hashCode());
            }

            @Override
            public void onAdClick(RFNativeAdInfo nativeAdInfo) {
                Log.d(ADRFDemoConstant.TAG, "onAdClick: " + nativeAdInfo.hashCode());
            }

            @Override
            public void onAdClose(RFNativeAdInfo nativeAdInfo) {
                Log.d(ADRFDemoConstant.TAG, "onAdClose: " + nativeAdInfo.hashCode());
                // 广告被关闭，释放和移除RFNativeAdInfo
                nativeAdAdapter.removeData(nativeAdInfo);
            }

            @Override
            public void onAdFailed(RFError error) {
                if (error != null) {
                    Log.d(ADRFDemoConstant.TAG, "onAdFailed: " + error.toString());
                }
                nativeAdAdapter.addData(tempDataList);
                refreshLayout.finish(refreshType, false, false);
            }
        });

        // 触发刷新
        refreshLayout.autoRefresh();
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        refreshType = MySmartRefreshLayout.TYPE_LOAD_MORE;
        loadData();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        refreshType = MySmartRefreshLayout.TYPE_FRESH;
        nativeAdAdapter.clearData();
        loadData();
    }

    /**
     * 加载数据和广告
     */
    private void loadData() {
        tempDataList.clear();
        mockNormalDataRequest();
        // 信息流广告场景id（场景id非必选字段，如果需要可到开发者后台创建）
        rfNativeAd.setSceneId(ADRFDemoConstant.NATIVE_AD_SCENE_ID);
        // 请求广告数据，参数一广告位ID，参数二请求数量[1,3]
        rfNativeAd.loadAd(ADRFDemoConstant.NATIVE_AD_POS_ID2, ADRFDemoConstant.NATIVE_AD_COUNT);
    }

    /**
     * 模拟普通数据请求
     */
    private void mockNormalDataRequest() {
        for (int i = 0; i < 20; i++) {
            tempDataList.add(new NativeAdSampleData("模拟的普通数据 : " + (nativeAdAdapter == null ? 0 : nativeAdAdapter.getItemCount() + i)));
        }
    }

    private void showAdTypeCheckDialog() {
        new AlertDialog.Builder(this)
                .setItems(R.array.native_ad_list, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String toastContent = "设置完成，生效中";
                        switch (which) {
                            case 0:
                                ADRFDemoConstant.NATIVE_AD_POS_ID = ADRFDemoConstant.NATIVE_AD_POS_ID1;
                                break;
                            case 1:
                                ADRFDemoConstant.NATIVE_AD_POS_ID = ADRFDemoConstant.NATIVE_AD_POS_ID2;;
                                break;
                            default:
                                break;
                        }
                        RFToastUtil.show(NativeAdActivity.this, toastContent);
                        dialog.dismiss();
                        nativeAdAdapter.clearData();
                        loadData();
                    }
                }).create().show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        initAd();
    }
}
