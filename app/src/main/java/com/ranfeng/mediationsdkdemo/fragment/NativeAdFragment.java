package com.ranfeng.mediationsdkdemo.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ranfeng.mediationsdk.ad.RFNativeAd;
import com.ranfeng.mediationsdk.ad.data.RFNativeAdInfo;
import com.ranfeng.mediationsdk.ad.error.RFError;
import com.ranfeng.mediationsdk.ad.listener.RFNativeAdListener;
import com.ranfeng.mediationsdkdemo.adapter.NativeAdAdapter;
import com.ranfeng.mediationsdkdemo.constant.ADRFDemoConstant;
import com.ranfeng.mediationsdkdemo.entity.NativeAdSampleData;
import com.ranfeng.mediationsdkdemo.widget.MySmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

import com.ranfeng.mediationsdk.ad.entity.AdSize;
import com.ranfeng.mediationsdk.ad.entity.ExtraParams;
import com.ranfeng.mediationsdkdemo.R;

/**
 * @description 信息流广告Fragment示例
 */
public class NativeAdFragment extends BaseFragment implements OnRefreshLoadMoreListener {
    private MySmartRefreshLayout refreshLayout;
    private NativeAdAdapter nativeAdAdapter;
    private RFNativeAd rfNativeAd;
    private int refreshType;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.activity_native_ad, null);

        initView(inflate);
        initListener();
        initData();

        return inflate;
    }

    @Override
    public String getTitle() {
        return "信息流广告";
    }

    private void initView(View inflate) {
        RecyclerView recyclerView = inflate.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflate.getContext()));
        refreshLayout = inflate.findViewById(R.id.refreshLayout);

        nativeAdAdapter = new NativeAdAdapter(inflate.getContext());
        recyclerView.setAdapter(nativeAdAdapter);
    }

    private void initListener() {
        refreshLayout.setOnRefreshLoadMoreListener(this);
    }

    private void initData() {
        // 创建信息流广告实例
        rfNativeAd = new RFNativeAd(this);
        int widthPixels = getResources().getDisplayMetrics().widthPixels;
        // 创建额外参数实例
        ExtraParams extraParams = new ExtraParams.Builder()
                // 设置整个广告视图预期宽高(目前仅头条平台需要，没有接入头条可不设置)，单位为px，高度如果小于等于0则高度自适应
                .adSize(new AdSize(widthPixels, 0))
                .build();
        // 设置一些额外参数，有些平台的广告可能需要传入一些额外参数，如果有接入头条平台，该参数必须设置
        rfNativeAd.setLocalExtraParams(extraParams);

        // 设置仅支持的广告平台，设置了这个值，获取广告时只会去获取该平台的广告，null或空字符串为不限制，默认为null，方便调试使用，上线时建议不设置。注：仅debug模式为true时生效。
        rfNativeAd.setOnlySupportPlatform(ADRFDemoConstant.NATIVE_AD_ONLY_SUPPORT_PLATFORM);
        // 设置广告监听
        rfNativeAd.setListener(new RFNativeAdListener() {
            @Override
            public void onRenderFailed(RFNativeAdInfo rfNativeAdInfo, RFError error) {
                Log.d(ADRFDemoConstant.TAG, "onRenderFailed: " + error.toString());
                nativeAdAdapter.removeData(rfNativeAdInfo);
            }

            @Override
            public void onAdReceive(List<RFNativeAdInfo> adInfoList) {
                Log.d(ADRFDemoConstant.TAG, "onAdReceive: " + adInfoList.size());
                List<NativeAdSampleData> nativeAdSampleDataList = new ArrayList<>();
                for (int i = 0; i < adInfoList.size(); i++) {
                    RFNativeAdInfo nativeAdInfo = adInfoList.get(i);
                    nativeAdSampleDataList.add(new NativeAdSampleData(nativeAdInfo));
                }
                nativeAdAdapter.addData(nativeAdSampleDataList);
                refreshLayout.finish(refreshType, true, false);
            }

            @Override
            public void onAdExpose(RFNativeAdInfo rfNativeAdInfo) {
                Log.d(ADRFDemoConstant.TAG, "onAdExpose: " + rfNativeAdInfo.hashCode());
            }

            @Override
            public void onAdClick(RFNativeAdInfo rfNativeAdInfo) {
                Log.d(ADRFDemoConstant.TAG, "onAdClick: " + rfNativeAdInfo.hashCode());
            }

            @Override
            public void onAdClose(RFNativeAdInfo rfNativeAdInfo) {
                Log.d(ADRFDemoConstant.TAG, "onAdClose: " + rfNativeAdInfo.hashCode());
                nativeAdAdapter.removeData(rfNativeAdInfo);
            }

            @Override
            public void onAdFailed(RFError error) {
                if (error != null) {
                    Log.d(ADRFDemoConstant.TAG, "onAdFailed: " + error.toString());
                }
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
        List<NativeAdSampleData> normalDataList = mockNormalDataRequest();
        nativeAdAdapter.addData(normalDataList);

        // 请求广告数据，参数一广告位ID，参数二请求数量[1,3]
        rfNativeAd.loadAd(ADRFDemoConstant.NATIVE_AD_POS_ID, ADRFDemoConstant.NATIVE_AD_COUNT);
    }

    /**
     * 模拟普通数据请求
     *
     * @return : 普通数据列表
     */
    private List<NativeAdSampleData> mockNormalDataRequest() {
        List<NativeAdSampleData> normalDataList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            normalDataList.add(new NativeAdSampleData("模拟的普通数据 : " + (nativeAdAdapter.getItemCount() + i)));
        }
        return normalDataList;
    }
}
