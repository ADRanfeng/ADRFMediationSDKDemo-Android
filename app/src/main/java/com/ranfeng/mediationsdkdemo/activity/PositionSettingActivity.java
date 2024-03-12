package com.ranfeng.mediationsdkdemo.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.ranfeng.mediationsdk.ad.data.AdType;
import com.ranfeng.mediationsdk.util.RFToastUtil;
import com.ranfeng.mediationsdkdemo.R;
import com.ranfeng.mediationsdkdemo.constant.ADRFDemoConstant;

/**
 * @description 设置界面
 */
public class PositionSettingActivity extends BaseAdActivity {
    private static final String AD_TYPE = "AD_TYPE";
    private static final String POS_ID_LIST = "POS_ID_LIST";
    private EditText etPosId;
    private TextView tvCount;
    private EditText etCount;
    private TextView tvAutoRefreshInterval;
    private EditText etAutoRefreshInterval;
    private SwitchCompat cbNativeMute;
    private String adType;
    private EditText etOnlySupportPlatform;
    private List<String> posIdList;
    private SwitchCompat cbOnlySupportPlatform;
    private TextView tvScene;
    private EditText etScene;
    private HashMap<String, String> platformMap = new HashMap<>();

    public static void start(Context context, String adType, ArrayList<String> posIdList) {
        Intent intent = new Intent(context, PositionSettingActivity.class);
        intent.putExtra(AD_TYPE, adType);
        intent.putStringArrayListExtra(POS_ID_LIST, posIdList);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position_setting);
        initPlatformData();
        initView();
        initListener();
        initData();
    }

    private void initPlatformData() {
        platformMap.put("所有(null或空字符串)", "");
        platformMap.put("天目(tianmu)", "tianmu");
        platformMap.put("然峰(ranfeng)", "ranfeng");
        platformMap.put("优量汇(gdt)", "gdt");
        platformMap.put("头条/穿山甲(toutiao)", "toutiao");
        platformMap.put("百度/百青藤(baidu)", "baidu");
        platformMap.put("快手(ksad)", "ksad");
    }

    private String getPlatformKey(Map<String, String> map, String value) {
        if (value == null) {
            value = "";
        }
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return "";
    }

    private void initView() {
        etPosId = findViewById(R.id.etPosId);
        tvCount = findViewById(R.id.tvCount);
        etCount = findViewById(R.id.etCount);

        etOnlySupportPlatform = findViewById(R.id.etOnlySupportPlatform);
        cbOnlySupportPlatform = findViewById(R.id.cbOnlySupportPlatform);

        tvAutoRefreshInterval = findViewById(R.id.tvAutoRefreshInterval);
        etAutoRefreshInterval = findViewById(R.id.etAutoRefreshInterval);

        cbNativeMute = findViewById(R.id.cbNativeMute);

        tvScene = findViewById(R.id.tvScene);
        etScene = findViewById(R.id.etScene);

    }

    private void initListener() {
        findViewById(R.id.btnDefine).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData();
            }
        });
        etOnlySupportPlatform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPlatformSelectDialog();
            }
        });
        etPosId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPosIdSelectDialog();
            }
        });
        etCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAdCountSelectDialog();
            }
        });
    }

    private void showAdCountSelectDialog() {
        new AlertDialog.Builder(this)
                .setItems(R.array.ad_count, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        etCount.setText(getResources().getStringArray(R.array.ad_count)[which]);
                    }
                })
                .create()
                .show();
    }

    private void showPosIdSelectDialog() {
        String[] posIds = new String[posIdList.size()];
        posIdList.toArray(posIds);

        new AlertDialog.Builder(this)
                .setItems(posIds, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        etPosId.setText(posIdList.get(which));
                    }
                })
                .create()
                .show();
    }

    private void showPlatformSelectDialog() {
        new AlertDialog.Builder(this)
                .setItems(R.array.platforms_zh, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        etOnlySupportPlatform.setText(getResources().getStringArray(R.array.platforms_zh)[which]);
                    }
                })
                .create()
                .show();
    }

    @SuppressLint("SetTextI18n")
    private void initData() {
        adType = getIntent().getStringExtra(AD_TYPE);
        adType = adType == null ? "" : adType;
        posIdList = getIntent().getStringArrayListExtra(POS_ID_LIST);
        posIdList = posIdList == null ? new ArrayList<String>() : posIdList;

        switch (adType) {
            case AdType.TYPE_SPLASH:
                etPosId.setText(ADRFDemoConstant.SPLASH_AD_POS_ID);
                String platformKey = getPlatformKey(platformMap, ADRFDemoConstant.SPLASH_AD_ONLY_SUPPORT_PLATFORM);
                etOnlySupportPlatform.setText(platformKey);
                break;
            case AdType.TYPE_BANNER:
                etPosId.setText(ADRFDemoConstant.BANNER_AD_POS_ID);
                platformKey = getPlatformKey(platformMap, ADRFDemoConstant.BANNER_AD_ONLY_SUPPORT_PLATFORM);
                etOnlySupportPlatform.setText(platformKey);
                etAutoRefreshInterval.setText(String.valueOf(ADRFDemoConstant.BANNER_AD_AUTO_REFRESH_INTERVAL));
                tvAutoRefreshInterval.setVisibility(View.VISIBLE);
                etAutoRefreshInterval.setVisibility(View.VISIBLE);
                tvScene.setVisibility(View.VISIBLE);
                etScene.setVisibility(View.VISIBLE);
                etScene.setText(String.valueOf(ADRFDemoConstant.BANNER_AD_SCENE_ID));
                break;
            case AdType.TYPE_FLOW:
                etPosId.setText(ADRFDemoConstant.NATIVE_AD_POS_ID);
                platformKey = getPlatformKey(platformMap, ADRFDemoConstant.BANNER_AD_ONLY_SUPPORT_PLATFORM);
                etOnlySupportPlatform.setText(platformKey);
                etCount.setText(String.valueOf(ADRFDemoConstant.NATIVE_AD_COUNT));
                etCount.setVisibility(View.VISIBLE);
                tvCount.setVisibility(View.VISIBLE);
                cbNativeMute.setVisibility(View.VISIBLE);
                cbNativeMute.setChecked(ADRFDemoConstant.NATIVE_AD_PLAY_WITH_MUTE);
                tvScene.setVisibility(View.VISIBLE);
                etScene.setVisibility(View.VISIBLE);
                etScene.setText(String.valueOf(ADRFDemoConstant.NATIVE_AD_SCENE_ID));
                break;
            case AdType.TYPE_REWARD_VOD:
                etPosId.setText(ADRFDemoConstant.REWARD_VOD_AD_POS_ID);
                platformKey = getPlatformKey(platformMap, ADRFDemoConstant.REWARD_VOD_AD_ONLY_SUPPORT_PLATFORM);
                etOnlySupportPlatform.setText(platformKey);
                tvScene.setVisibility(View.VISIBLE);
                etScene.setVisibility(View.VISIBLE);
                cbNativeMute.setVisibility(View.VISIBLE);
                cbNativeMute.setChecked(ADRFDemoConstant.REWARD_AD_PLAY_WITH_MUTE);
                etScene.setText(String.valueOf(ADRFDemoConstant.REWARD_VOD_AD_SCENE_ID));
                break;
            case AdType.TYPE_INTERSTITIAL:
                etPosId.setText(ADRFDemoConstant.INTERSTITIAL_AD_POS_ID);
                platformKey = getPlatformKey(platformMap, ADRFDemoConstant.INTERSTITIAL_AD_ONLY_SUPPORT_PLATFORM);
                etOnlySupportPlatform.setText(platformKey);
                tvScene.setVisibility(View.VISIBLE);
                etScene.setVisibility(View.VISIBLE);
                cbNativeMute.setVisibility(View.VISIBLE);
                cbNativeMute.setChecked(ADRFDemoConstant.INTERSTITIAL_AD_PLAY_WITH_MUTE);
                etScene.setText(String.valueOf(ADRFDemoConstant.INTERSTITIAL_AD_SCENE_ID));
                break;
            default:
                RFToastUtil.show(this, "非法广告类型");
                finish();
                break;
        }
    }

    private void updateData() {
        String posId = etPosId.getText().toString().trim();
        String platformKey = etOnlySupportPlatform.getText().toString().trim();
        String onlySupportPlatform = platformMap.get(platformKey);
        switch (adType) {
            case AdType.TYPE_SPLASH:
                ADRFDemoConstant.SPLASH_AD_POS_ID = posId;
                ADRFDemoConstant.SPLASH_AD_ONLY_SUPPORT_PLATFORM = onlySupportPlatform;
                break;
            case AdType.TYPE_BANNER:
                ADRFDemoConstant.BANNER_AD_POS_ID = posId;
                ADRFDemoConstant.BANNER_AD_AUTO_REFRESH_INTERVAL = getAutoRefreshInterval();
                ADRFDemoConstant.BANNER_AD_ONLY_SUPPORT_PLATFORM = onlySupportPlatform;
                ADRFDemoConstant.BANNER_AD_SCENE_ID = getSceneId();
                break;
            case AdType.TYPE_FLOW:
                ADRFDemoConstant.NATIVE_AD_POS_ID = posId;
                ADRFDemoConstant.NATIVE_AD_COUNT = getAdCount();
                ADRFDemoConstant.NATIVE_AD_ONLY_SUPPORT_PLATFORM = onlySupportPlatform;
                ADRFDemoConstant.NATIVE_AD_PLAY_WITH_MUTE = cbNativeMute.isChecked();
                ADRFDemoConstant.NATIVE_AD_SCENE_ID = getSceneId();
                break;
            case AdType.TYPE_REWARD_VOD:
                ADRFDemoConstant.REWARD_VOD_AD_POS_ID = posId;
                ADRFDemoConstant.REWARD_VOD_AD_ONLY_SUPPORT_PLATFORM = onlySupportPlatform;
                ADRFDemoConstant.REWARD_AD_PLAY_WITH_MUTE = cbNativeMute.isChecked();
                ADRFDemoConstant.REWARD_VOD_AD_SCENE_ID = getSceneId();
                break;
            case AdType.TYPE_INTERSTITIAL:
                ADRFDemoConstant.INTERSTITIAL_AD_POS_ID = posId;
                ADRFDemoConstant.INTERSTITIAL_AD_ONLY_SUPPORT_PLATFORM = onlySupportPlatform;
                ADRFDemoConstant.INTERSTITIAL_AD_PLAY_WITH_MUTE = cbNativeMute.isChecked();
                ADRFDemoConstant.INTERSTITIAL_AD_SCENE_ID = getSceneId();
                break;
            default:
                break;
        }
        if (cbOnlySupportPlatform.isChecked()) {
            ADRFDemoConstant.SPLASH_AD_ONLY_SUPPORT_PLATFORM = onlySupportPlatform;
            ADRFDemoConstant.BANNER_AD_ONLY_SUPPORT_PLATFORM = onlySupportPlatform;
            ADRFDemoConstant.NATIVE_AD_ONLY_SUPPORT_PLATFORM = onlySupportPlatform;
            ADRFDemoConstant.REWARD_VOD_AD_ONLY_SUPPORT_PLATFORM = onlySupportPlatform;
            ADRFDemoConstant.INTERSTITIAL_AD_ONLY_SUPPORT_PLATFORM = onlySupportPlatform;
        }

        RFToastUtil.show(this, "修改成功");
        finish();
    }

    private int getAutoRefreshInterval() {
        String autoRefreshIntervalStr = etAutoRefreshInterval.getText().toString().trim();
        try {
            int autoRefreshInterval = Integer.parseInt(autoRefreshIntervalStr);
            return autoRefreshInterval <= 0 ? 0 : autoRefreshInterval < 30 ? 30 : autoRefreshInterval > 120 ? 120 : autoRefreshInterval;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int getAdCount() {
        String countStr = etCount.getText().toString().trim();
        try {
            int count = Integer.parseInt(countStr);
            return count <= 0 ? 1 : count > 3 ? 3 : count;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    private String getSceneId() {
        String sceneStr = etScene.getText().toString().trim();
        return sceneStr;
    }
}
