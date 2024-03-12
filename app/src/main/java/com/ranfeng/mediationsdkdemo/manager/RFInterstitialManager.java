package com.ranfeng.mediationsdkdemo.manager;

import android.app.Activity;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.kwad.sdk.api.KsInterstitialAd;
import com.ranfeng.mediationsdk.ad.data.RFInterstitialAdInfo;
import com.ranfeng.mediationsdk.util.RFDisplayUtil;
import com.ranfeng.mediationsdkdemo.R;
import com.ranfeng.mediationsdkdemo.util.ADViewFindUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


/**
 * @Description: 插屏广告添加跳过按钮和自动关闭功能管理类，
 *               目前支持优量汇（优量汇）、头条、快手渠道，渠道陆续新增中。
 */
public class RFInterstitialManager {

    private Activity adInterstitialActivity;
    private TextView jumpView;
    public static int jumpTime = 10;
    private List<View> views = new ArrayList<>();
    private RFInterstitialAdInfo rfInterstitialAdInfo;
    private boolean isDialog;

    public static String PLATFORM_GDT = "gdt";
    public static String PLATFORM_TOUTIAO = "toutiao";
    public static String PLATFORM_KUAISHOU = "ksad";

    /**
     * 快手插屏弹窗对象，没有接入快手可注释掉
     * 快手的对象会因为迭代经常变更，可以通过showInterstitialAd方法找到快手的dialog对象，然后进行反射关闭
     */
    private com.kwad.components.ad.interstitial.d kuaishouDialog;

    private static RFInterstitialManager instance;

    public static RFInterstitialManager getInstance() {
        if (instance == null) {
            synchronized (RFInterstitialManager.class) {
                if (instance == null) {
                    instance = new RFInterstitialManager();
                }
            }
        }
        return instance;
    }

    /**
     * 设置插屏广告activity
     * @param adInterstitialActivity
     */
    public void setAdInterstitialActivity(Activity adInterstitialActivity) {
        this.adInterstitialActivity = adInterstitialActivity;
    }

    /**
     * 添加跳过按钮和倒计时逻辑
     *
     * @param interstitialAdInfo
     * @param adInterstitialActivity
     */
    public void addJumpView(RFInterstitialAdInfo interstitialAdInfo, Activity adInterstitialActivity) {
        this.rfInterstitialAdInfo = interstitialAdInfo;
        views.clear();

        if (adInterstitialActivity == null || adInterstitialActivity.isFinishing() || TextUtils.isEmpty(interstitialAdInfo.getPlatform())) {
            return;
        }

        if (interstitialAdInfo.getPlatform().equals(PLATFORM_GDT)) {
            this.adInterstitialActivity = adInterstitialActivity;
            isDialog = true;
            gdtAddJumpButton();
        } else if (interstitialAdInfo.getPlatform().equals(PLATFORM_TOUTIAO)) {
            isDialog = false;
            toutiaoAddJumpButton();
        } else if (interstitialAdInfo.getPlatform().equals(PLATFORM_KUAISHOU)) {
            this.adInterstitialActivity = adInterstitialActivity;
            isDialog = true;
            kuaishouAddJumpButton();
        }
    }

    /**
     * kuaishou插屏页面添加跳过按钮，该渠道是通过dialog作为插屏
     */
    private void kuaishouAddJumpButton() {

        setActivityViews();

        for (int i = 0; i < views.size(); i++) {
            View view = views.get(i);
            if (view.getId() == R.id.ksad_container) {
                jumpView = getJumpView((ViewGroup) view);
                ((ViewGroup) view).addView(jumpView);
                startCountDown();
                break;
            }
        }

        try {
            Field fieldAdapterAdInfo = rfInterstitialAdInfo.getClass().getSuperclass().getSuperclass().getDeclaredField("i");
            fieldAdapterAdInfo.setAccessible(true);
            KsInterstitialAd ksInterstitialAd = (KsInterstitialAd) fieldAdapterAdInfo.get(rfInterstitialAdInfo);
            Field fieldD = ksInterstitialAd.getClass().getDeclaredField("hJ");
            fieldD.setAccessible(true);
            kuaishouDialog = (com.kwad.components.ad.interstitial.d) fieldD.get(ksInterstitialAd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * tianmu插屏页面添加跳过按钮，该渠道是通过dialog作为插屏
     */
    private void tianmuAddJumpButton() {
        setActivityViews();

        for (int i = 0; i < views.size(); i++) {
            View view = views.get(i);
            if (view.getId() == R.id.tianmu_interstitial_container) {
                jumpView = getJumpView((ViewGroup) view);
                ((ViewGroup) view).addView(jumpView);
                startCountDown();
                break;
            }
        }
    }

    /**
     * 优量汇插屏页面添加跳过按钮，该渠道是通过dialog作为插屏
     */
    private void gdtAddJumpButton() {
        setActivityViews();

        for (View view : views) {
            if (view.getClass().getName().contains("com.qq.e.comm.plugin.")) {
                if (view instanceof ViewGroup) {
                    jumpView = getJumpView((ViewGroup) view);
                    ((ViewGroup) view).addView(jumpView);
                    startCountDown();
                    return;
                }
            }
        }
    }

    /**
     * 优量汇插屏页面添加跳过按钮，该渠道是通过activity作为插屏
     */
//    public void gdtAddJumpButton() {
//        ViewGroup content = adInterstitialActivity.findViewById(android.R.id.content);
//        if (content != null && content.getChildCount() > 0) {
//            for (int i = 0; i < content.getChildCount(); i++) {
//                View view = content.getChildAt(i);
//                if (view != null && view instanceof LinearLayout) {
//                    LinearLayout linearLayout = (LinearLayout) view;
//                    if (linearLayout.getChildCount() > 0) {
//                        View viewY = linearLayout.getChildAt(linearLayout.getChildCount() - 1);
//                        if (viewY != null && viewY instanceof FrameLayout) {
//                            jumpView = getJumpView((ViewGroup) viewY);
//                            ((ViewGroup) viewY).addView(jumpView);
//                            startCountDown();
//                            break;
//                        }
//                    }
//                }
//            }
//        }
//    }

    /**
     * 头条插屏页面添加跳过按钮，该渠道是通过activity作为插屏
     */
    public void toutiaoAddJumpButton() {
        ViewGroup content = adInterstitialActivity.findViewById(android.R.id.content);
        if (content != null && content.getChildCount() > 0) {
            for (int i = 0; i < content.getChildCount(); i++) {
                View view = content.getChildAt(i);
                if (view != null && view instanceof FrameLayout) {
                    jumpView = getJumpView((ViewGroup) view);
                    ((ViewGroup) view).addView(jumpView);
                    startCountDown();
                    break;
                }
            }
        }
    }

    CountDownTimer countDownTimer = new CountDownTimer(jumpTime * 1000, 1000) {
        public void onTick(long millisUntilFinished) {
            if (jumpView != null) {
                jumpView.setText((int)(millisUntilFinished / 1000) + "s｜跳过");
            }
        }

        public void onFinish() {
            release();
        }
    };

    /**
     * 开始倒计时
     */
    public void startCountDown() {
        countDownTimer.cancel();
        countDownTimer.start();
    }

    /**
     * 插屏广告关闭
     */
    public void release() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (!isDialog) {
            // 如果插屏为activity模式

            if (adInterstitialActivity != null) {
                adInterstitialActivity.finish();
                adInterstitialActivity = null;
            }
        } else {
            // 如果插屏为dialog模式模式

            if (rfInterstitialAdInfo != null) {
                rfInterstitialAdInfo.release();
                rfInterstitialAdInfo = null;
            }
            if (kuaishouDialog != null) {
                kuaishouDialog.dismiss();
                kuaishouDialog = null;
            }
        }
    }

    /**
     * 获取跳过按钮样式以及布局，位置默认为容器左上角
     * @param frameLayout
     * @return
     */
    public TextView getJumpView(ViewGroup frameLayout) {
        TextView textView = new TextView(frameLayout.getContext());
        textView.setText(jumpTime + "s｜跳过");
        textView.setTextColor(Color.WHITE);
        textView.setPadding(RFDisplayUtil.dp2px(10), RFDisplayUtil.dp2px(5), RFDisplayUtil.dp2px(10), RFDisplayUtil.dp2px(5));
        textView.setBackgroundResource(R.drawable.shape_black_000000_radius15);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = RFDisplayUtil.dp2px(10);
        layoutParams.leftMargin = RFDisplayUtil.dp2px(10);
        textView.setLayoutParams(layoutParams);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                release();
            }
        });

        return textView;
    }

    /**
     * 获取activity views
     * 用于dialog模式
     */
    private void setActivityViews() {
        View decor = ADViewFindUtils.tryGetTheFrontView(adInterstitialActivity);
        if (decor != null) {
            traverse(decor);
        }
    }

    /**
     * 递归获取 全部布局的 viewgroup
     * @param view
     */
    private void traverse(View view) {
        if (view.getAlpha() == 0 || view.getVisibility() != View.VISIBLE) {
            return;
        }
        views.add(view);
        if (view instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) view;
            for (int i = 0; i < parent.getChildCount(); i++) {
                traverse(parent.getChildAt(i));
            }
        }
    }
}
