package com.ranfeng.mediationsdkdemo.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.FrameLayout;

import com.ranfeng.mediationsdk.ADRFMediationSDK;
import com.ranfeng.mediationsdk.ad.RFSplashAd;
import com.ranfeng.mediationsdk.ad.data.AdInfo;
import com.ranfeng.mediationsdk.ad.entity.AdSize;
import com.ranfeng.mediationsdk.ad.entity.ExtraParams;
import com.ranfeng.mediationsdk.ad.error.RFError;
import com.ranfeng.mediationsdk.ad.listener.RFSplashAdListener;
import com.ranfeng.mediationsdk.config.InitConfig;
import com.ranfeng.mediationsdk.listener.RFInitListener;
import com.ranfeng.mediationsdkdemo.R;
import com.ranfeng.mediationsdkdemo.constant.ADRFDemoConstant;
import com.ranfeng.mediationsdkdemo.util.SPUtil;
import com.ranfeng.mediationsdkdemo.util.UIUtils;
import com.ranfeng.mediationsdkdemo.widget.PrivacyPolicyDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * @description 开屏广告示例，开屏广告容器请保证有屏幕高度的75%，建议开屏界面设置为全屏模式并禁止返回按钮
 */
public class SplashAdActivity extends AppCompatActivity {

    private static final String AGREE_PRIVACY_POLICY = "AGREE_PRIVACY_POLICY";
    /**
     * 根据实际情况申请 如果接入oppo平台必须要获取READ_PHONE_STATE权限，否则无法获取oppo平台广告
     */
    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };
    private static final int REQUEST_CODE = 7722;

    private List<String> permissionList = new ArrayList<>();
    private RFSplashAd rfSplashAd;
    private FrameLayout flContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_ad);
        flContainer = findViewById(R.id.flContainer);

        // 据悉，工信部将在2020年8月底前上线运行全国APP技术检测平台管理系统，2020年12月10日前完成覆盖40万款主流App的合规检测工作。
        // 为了保证您的App顺利通过检测，结合当前监管关注重点，请务必将ADRFMediationSDK的初始化放在用户同意隐私政策之后。
        checkPrivacyPolicy();
    }

    /**
     * 检查隐私政策
     */
    private void checkPrivacyPolicy() {
        // 获取是否已经同意过隐私政策
        boolean agreePrivacyPolicy = SPUtil.getBoolean(this, AGREE_PRIVACY_POLICY);
        if (agreePrivacyPolicy) {
            // 如果同意了则直接初始化广告SDK并加载开屏广告
            initADSdkAndLoadSplashAd();
        } else {
            // 否则展示隐私政策弹框
            showPrivacyPolicyDialog();
        }
    }

    /**
     * 展示隐私政策弹框
     */
    private void showPrivacyPolicyDialog() {
        PrivacyPolicyDialog privacyPolicyDialog = new PrivacyPolicyDialog(this);
        privacyPolicyDialog.setOnResultCallback(new PrivacyPolicyDialog.OnResultCallback() {
            @Override
            public void onConfirm() {
                // 用户同意之后SP进行记录
                SPUtil.putBoolean(getApplicationContext(), AGREE_PRIVACY_POLICY, true);
                // 初始化广告SDK并加载开屏广告
                initADSdkAndLoadSplashAd();
            }

            @Override
            public void onCancel() {
                // 用户不同意不进行ADRFMediationSDK的初始化（将导致广告获取失败）
                jumpMain();
            }
        });
        privacyPolicyDialog.setCancelable(false);
        privacyPolicyDialog.setCanceledOnTouchOutside(false);
        privacyPolicyDialog.show();
    }

    /**
     * 初始化广告SDK并且跳转开屏界面
     */
    private void initADSdkAndLoadSplashAd() {

        // 初始化ADRFMediationSDK广告SDK
        ADRFMediationSDK.getInstance().init(
                this,
                new InitConfig.Builder()
                        // 设置APPID
                        .appId(ADRFDemoConstant.APP_ID)
                        // 是否开启Debug，开启会有详细的日志信息打印
                        .debug(true)
                        //【慎改】是否同意隐私政策，将禁用一切设备信息读起严重影响收益
                        .agreePrivacyStrategy(true)
                        // 是否可获取定位数据
                        .isCanUseLocation(true)
                        // 是否可获取设备信息
                        .isCanUsePhoneState(true)
                        // 是否可读取设备安装列表
                        .isCanReadInstallList(true)
                        // 是否可读取设备外部读写权限
                        .isCanUseReadWriteExternal(true)
                        // 是否过滤第三方平台的问题广告（例如: 已知某个广告平台在某些机型的Banner广告可能存在问题，如果开启过滤，则在该机型将不再去获取该平台的Banner广告）
                        .filterThirdQuestion(false)
                        .build(),
                new RFInitListener() {
                    @Override
                    public void onSuccess() {
                        Log.d(ADRFDemoConstant.TAG, "ADRFMediationSDK init onSuccess");
                    }

                    @Override
                    public void onFailed(String s) {
                        Log.d(ADRFDemoConstant.TAG, "ADRFMediationSDK init onFailed error : " + s);
                    }
                }
        );

        initSplashAd();
//        jumpMain();
    }

    private void initSplashAd() {
        // 6.0及以上获取没有申请的权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : PERMISSIONS) {
                int checkSelfPermission = ContextCompat.checkSelfPermission(this, permission);
                if (PackageManager.PERMISSION_GRANTED == checkSelfPermission) {
                    continue;
                }
                permissionList.add(permission);
            }
        }

        // 创建开屏广告实例，第一个参数可以是Activity或Fragment，第二个参数是广告容器（请保证容器不会拦截点击、触摸等事件，高度不小于真实屏幕高度的75%，并且处于可见状态）
        rfSplashAd = new RFSplashAd(this, flContainer);
        // 设置是否是沉浸式，如果为true，跳过按钮距离顶部的高度会加上状态栏高度
        rfSplashAd.setImmersive(true);

        int widthPixels = UIUtils.getScreenWidthInPx(this);
        int heightPixels = UIUtils.getScreenHeightInPx(this);

        // 创建额外参数实例
        ExtraParams extraParams = new ExtraParams.Builder()
                // 设置整个广告视图预期宽高(目前头条平台需要，没有接入头条可不设置)，单位为px，如果不设置头条开屏广告视图将会以9 : 16的比例进行填充，小屏幕手机可能会出现素材被压缩的情况
                .adSize(new AdSize(widthPixels, heightPixels - UIUtils.dp2px(this, 100)))
                .build();

        // 如果开屏容器不是全屏可以设置额外参数
        rfSplashAd.setLocalExtraParams(extraParams);

        // 设置仅支持的广告平台，设置了这个值，获取广告时只会去获取该平台的广告，null或空字符串为不限制，默认为null，方便调试使用，上线时建议不设置。注：仅debug模式为true时生效。
        rfSplashAd.setOnlySupportPlatform(ADRFDemoConstant.SPLASH_AD_ONLY_SUPPORT_PLATFORM);
        // 设置开屏广告监听
        rfSplashAd.setListener(new RFSplashAdListener() {

            @Override
            public void onADTick(long millisUntilFinished) {
                Log.d(ADRFDemoConstant.TAG, "广告剩余倒计时时长回调：" + millisUntilFinished);
            }

            @Override
            public void onReward(AdInfo info) {
                // 目前仅优量汇渠道支持该回调
                Log.d(ADRFDemoConstant.TAG, "广告奖励回调，不一定准确，埋点数据仅供参考... ");
            }

            @Override
            public void onAdSkip(AdInfo info) {
                Log.d(ADRFDemoConstant.TAG, "广告跳过回调，不一定准确，埋点数据仅供参考... ");
            }

            @Override
            public void onAdReceive(AdInfo info) {
                Log.d(ADRFDemoConstant.TAG, "广告获取成功回调... ");
            }

            @Override
            public void onAdExpose(AdInfo info) {
                Log.d(ADRFDemoConstant.TAG, "广告展示回调，有展示回调不一定是有效曝光，如网络等情况导致上报失败");
            }

            @Override
            public void onAdClick(AdInfo info) {
                Log.d(ADRFDemoConstant.TAG, "广告点击回调，有点击回调不一定是有效点击，如网络等情况导致上报失败");
            }

            @Override
            public void onAdClose(AdInfo info) {
                Log.d(ADRFDemoConstant.TAG, "广告关闭回调，需要在此进行页面跳转");
                jumpMain();
            }

            @Override
            public void onAdFailed(RFError error) {
                if (error != null) {
                    String failedJson = error.toString();
                    Log.d(ADRFDemoConstant.TAG, "onAdFailed----->" + failedJson);
                }
                jumpMain();
            }
        });

        if (!permissionList.isEmpty()) {
            // 存在未申请的权限则先申请
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[0]), REQUEST_CODE);
        } else {
            loadSplash();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (REQUEST_CODE == requestCode && rfSplashAd != null) {
            loadSplash();
        }
    }

    private void loadSplash() {
        // 加载开屏广告，参数为广告位ID，同一个RFSplashAd只有一次loadAd有效
        rfSplashAd.loadAd(ADRFDemoConstant.SPLASH_AD_POS_ID);
    }

    @Override
    public void onBackPressed() {
        // 取消返回事件，增加开屏曝光率
    }

    /**
     * 跳转到主界面
     */
    private void jumpMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

}
