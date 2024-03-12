package com.ranfeng.mediationsdkdemo.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ranfeng.mediationsdk.ad.data.RFNativeAdInfo;
import com.ranfeng.mediationsdk.ad.data.RFNativeExpressAdInfo;
import com.ranfeng.mediationsdk.ad.data.RFNativeFeedAdInfo;
import com.ranfeng.mediationsdk.ad.error.RFError;
import com.ranfeng.mediationsdk.ad.listener.RFNativeVideoListener;
import com.ranfeng.mediationsdk.util.RFAdUtil;
import com.ranfeng.mediationsdk.util.RFViewUtil;
import com.ranfeng.mediationsdkdemo.entity.NativeAdSampleData;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;


import com.ranfeng.mediationsdkdemo.R;
import com.ranfeng.mediationsdkdemo.constant.ADRFDemoConstant;

/**
 * @description 信息流广告Adapter
 */
public class NativeAdAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    /**
     * 普通数据类型
     */
    private static final int ITEM_VIEW_TYPE_NORMAL_DATA = 0;
    /**
     * 信息流原生广告类型（没有MediaView）
     */
    private static final int ITEM_VIEW_TYPE_NATIVE_AD = 1;
    /**
     * 信息流原生广告类型（包含MediaView）
     */
    private static final int ITEM_VIEW_TYPE_NATIVE_AD_HAS_MEDIA_VIEW = 2;
    /**
     * 信息流模板广告类型
     */
    private static final int ITEM_VIEW_TYPE_EXPRESS_AD = 3;
    private final Context context;

    private List<NativeAdSampleData> dataList = new ArrayList<>();

    public NativeAdAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int itemViewType) {
        switch (itemViewType) {
            case ITEM_VIEW_TYPE_NATIVE_AD:
                return new NativeAdViewHolder(viewGroup);
            case ITEM_VIEW_TYPE_NATIVE_AD_HAS_MEDIA_VIEW:
                return new NativeAdMediaViewHolder(viewGroup);
            case ITEM_VIEW_TYPE_EXPRESS_AD:
                return new NativeExpressAdViewHolder(viewGroup);
            default:
                return new NormalDataViewHolder(viewGroup);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        NativeAdSampleData nativeAdSampleData = dataList.get(position);
        if (viewHolder instanceof NormalDataViewHolder) {
            ((NormalDataViewHolder) viewHolder).setData(nativeAdSampleData.getNormalData());
        } else if (viewHolder instanceof BaseNativeAdViewHolder) {
            // NativeAdViewHolder or NativeAdMediaViewHolder
            ((BaseNativeAdViewHolder) viewHolder).setData(context, (RFNativeFeedAdInfo) nativeAdSampleData.getNativeAdInfo());
        } else if (viewHolder instanceof NativeExpressAdViewHolder) {
            ((NativeExpressAdViewHolder) viewHolder).setData((RFNativeExpressAdInfo) nativeAdSampleData.getNativeAdInfo());
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        RFNativeAdInfo nativeAdInfo = dataList.get(position).getNativeAdInfo();
        if (nativeAdInfo == null) {
            return ITEM_VIEW_TYPE_NORMAL_DATA;
        } else if (nativeAdInfo.isNativeExpress()) {
            // nativeAdInfo instanceof RFNativeExpressAdInfo
            return ITEM_VIEW_TYPE_EXPRESS_AD;
        } else {
            // nativeAdInfo instanceof RFNativeFeedAdInfo
            RFNativeFeedAdInfo nativeFeedAdInfo = (RFNativeFeedAdInfo) nativeAdInfo;
            return nativeFeedAdInfo.hasMediaView() ? ITEM_VIEW_TYPE_NATIVE_AD_HAS_MEDIA_VIEW : ITEM_VIEW_TYPE_NATIVE_AD;
        }
    }

    /**
     * 移除广告所在的对象，一般模板广告有可能会渲染失败
     */
    public void removeData(RFNativeAdInfo rfNativeAdInfo) {
        for (int i = 0; i < dataList.size(); i++) {
            NativeAdSampleData nativeAdSampleData = dataList.get(i);
            if (nativeAdSampleData.getNativeAdInfo() == rfNativeAdInfo) {
                // 释放广告Info对象
                rfNativeAdInfo.release();
                // 从数据源中移除
                dataList.remove(nativeAdSampleData);
                // 通知刷新Adapter
                notifyDataSetChanged();
                break;
            }
        }
    }

    /**
     * 刷新数据
     */
    public void clearData() {
        dataList.clear();
        notifyDataSetChanged();
    }

    /**
     * 添加数据
     */
    public void addData(List<NativeAdSampleData> nativeAdSampleDataList) {
        int startPosition = dataList.size();
        dataList.addAll(nativeAdSampleDataList);
        if (startPosition <= 0) {
            notifyDataSetChanged();
        } else {
            notifyItemRangeInserted(startPosition + 1, dataList.size() - startPosition);
        }
    }

    /**
     * 普通数据ViewHolder
     */
    private static class NormalDataViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvNormalData;

        NormalDataViewHolder(@NonNull ViewGroup viewGroup) {
            super(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_native_ad_normal_data, viewGroup, false));
            tvNormalData = itemView.findViewById(R.id.tvNormalData);
        }

        public void setData(String normalData) {
            tvNormalData.setText(normalData);
        }
    }

    /**
     * 包含MediaView的ViewHolder
     */
    private static class NativeAdMediaViewHolder extends BaseNativeAdViewHolder {
        private final FrameLayout flMediaContainer;

        NativeAdMediaViewHolder(@NonNull ViewGroup viewGroup) {
            super(viewGroup, R.layout.item_native_ad_native_ad_media);
            flMediaContainer = itemView.findViewById(R.id.flMediaContainer);
        }

        @Override
        protected void setImageOrMediaData(Context context, RFNativeFeedAdInfo nativeFeedAdInfo) {
            // 当前信息流原生广告，获取的是多媒体视图（可能是视频、或者图片之类的），mediaView不为空时强烈建议进行展示
            View mediaView = nativeFeedAdInfo.getMediaView(flMediaContainer);
            // 将广告视图添加到容器中的便捷方法，mediaView为空会移除flMediaContainer的所有子View
            RFViewUtil.addAdViewToAdContainer(flMediaContainer, mediaView);
        }
    }

    /**
     * 没有MediaView的ViewHolder
     */
    private static class NativeAdViewHolder extends BaseNativeAdViewHolder {
        private final ImageView ivImage;

        NativeAdViewHolder(@NonNull ViewGroup viewGroup) {
            super(viewGroup, R.layout.item_native_ad_native_ad);
            ivImage = itemView.findViewById(R.id.ivImage);
        }

        @Override
        protected void setImageOrMediaData(Context context, RFNativeFeedAdInfo nativeFeedAdInfo) {
            Glide.with(context).load(nativeFeedAdInfo.getImageUrl()).into(ivImage);
        }
    }

    /**
     * 信息流原生广告BaseViewHolder
     */
    private static abstract class BaseNativeAdViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivIcon;
        private final RelativeLayout rlAdContainer;
        private final ImageView ivAdTarget;
        private final TextView tvTitle;
        private final TextView tvDesc;
        private final TextView tvAdType;
        private final ImageView ivClose;

        BaseNativeAdViewHolder(@NonNull ViewGroup viewGroup, @LayoutRes int layoutRes) {
            super(LayoutInflater.from(viewGroup.getContext()).inflate(layoutRes, viewGroup, false));
            rlAdContainer = itemView.findViewById(R.id.rlAdContainer);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            ivAdTarget = itemView.findViewById(R.id.ivAdTarget);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            tvAdType = itemView.findViewById(R.id.tvAdType);
            ivClose = itemView.findViewById(R.id.ivClose);
        }

        void setData(Context context, RFNativeFeedAdInfo nativeFeedAdInfo) {
            // 判断广告Info对象是否被释放（调用过RFNativeAd的release()或RFNativeAdInfo的release()会释放广告Info对象）
            // 释放后的广告Info对象不能再次使用
            if (!RFAdUtil.adInfoIsRelease(nativeFeedAdInfo)) {
                NativeAdAdapter.setVideoListener(nativeFeedAdInfo);

                // 交由子类实现加载图片还是MediaView
                setImageOrMediaData(context, nativeFeedAdInfo);

                Glide.with(context).load(nativeFeedAdInfo.getIconUrl()).into(ivIcon);
                ivAdTarget.setImageResource(nativeFeedAdInfo.getPlatformIcon());
                tvTitle.setText(nativeFeedAdInfo.getTitle());
                tvDesc.setText(nativeFeedAdInfo.getDesc());
                tvAdType.setText(nativeFeedAdInfo.getCtaText());

                // 注册广告交互, 必须调用
                // 注意：优量汇只会响应View...actionViews的点击事件，且这些View都应该是com.qq.e.ads.nativ.widget.NativeAdContainer的子View
                nativeFeedAdInfo.registerViewForInteraction((ViewGroup) itemView, rlAdContainer, tvAdType);

                // 注册关闭按钮，将关闭按钮点击事件交于SDK托管，以便于回调onAdClose
                // 务必最后调用
                nativeFeedAdInfo.registerCloseView(ivClose);
            }
        }

        protected abstract void setImageOrMediaData(Context context, RFNativeFeedAdInfo nativeFeedAdInfo);
    }

    /**
     * 信息流模板广告ViewHolder
     */
    private static class NativeExpressAdViewHolder extends RecyclerView.ViewHolder {

        NativeExpressAdViewHolder(@NonNull ViewGroup viewGroup) {
            super(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_native_ad_express_ad, viewGroup, false));
        }

        public void setData(RFNativeExpressAdInfo nativeExpressAdInfo) {
            // 判断广告Info对象是否被释放（调用过RFNativeAd的release()或RFNativeAdInfo的release()会释放广告Info对象）
            // 释放后的广告Info对象不能再次使用
            if (!RFAdUtil.adInfoIsRelease(nativeExpressAdInfo)) {
                NativeAdAdapter.setVideoListener(nativeExpressAdInfo);
                // 当前是信息流模板广告，getNativeExpressAdView获取的是整个模板广告视图
                View nativeExpressAdView = nativeExpressAdInfo.getNativeExpressAdView((ViewGroup) itemView);
                // 将广告视图添加到容器中的便捷方法
                RFViewUtil.addAdViewToAdContainer((ViewGroup) itemView, nativeExpressAdView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                // 渲染广告视图, 必须调用, 因为是模板广告, 所以传入ViewGroup和响应点击的控件可能并没有用
                // 务必在最后调用
                nativeExpressAdInfo.render((ViewGroup) itemView);
            }
        }
    }

    /**
     * 设置视频监听，无需求可不设置，视频监听回调因平台差异会有所不一，如：某些平台可能没有完成回调等
     */
    private static void setVideoListener(RFNativeAdInfo nativeAdInfo) {
        if (nativeAdInfo.isVideo()) {
            // 设置视频监听，监听回调因三方平台SDK差异有所差异，无需要可不设置
            nativeAdInfo.setVideoListener(new RFNativeVideoListener() {
                @Override
                public void onVideoLoad(RFNativeAdInfo rfNativeAdInfo) {
                    Log.d(ADRFDemoConstant.TAG, "onVideoLoad.... ");
                }

                @Override
                public void onVideoStart(RFNativeAdInfo rfNativeAdInfo) {
                    Log.d(ADRFDemoConstant.TAG, "onVideoStart.... ");
                }

                @Override
                public void onVideoPause(RFNativeAdInfo rfNativeAdInfo) {
                    Log.d(ADRFDemoConstant.TAG, "onVideoPause.... ");
                }

                @Override
                public void onVideoComplete(RFNativeAdInfo rfNativeAdInfo) {
                    Log.d(ADRFDemoConstant.TAG, "onVideoComplete.... ");
                }

                @Override
                public void onVideoError(RFNativeAdInfo rfNativeAdInfo, RFError error) {
                    Log.d(ADRFDemoConstant.TAG, "onVideoError.... " + error.toString());
                }
            });
        }
    }
}
