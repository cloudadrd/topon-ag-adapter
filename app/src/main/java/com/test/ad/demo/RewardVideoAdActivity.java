package com.test.ad.demo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.anythink.core.api.ATAdConst;
import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.AdError;
import com.anythink.rewardvideo.api.ATRewardVideoAd;
import com.anythink.rewardvideo.api.ATRewardVideoListener;

import java.util.HashMap;
import java.util.Map;

public class RewardVideoAdActivity extends Activity {

    private static String TAG = "RewardVideoAdActivity";
    String placementIds[] = new String[]{
            DemoApplicaion.mPlacementId_rewardvideo_all
            , DemoApplicaion.mPlacementId_rewardvideo_mintegral
            , DemoApplicaion.mPlacementId_rewardvideo_GDT
            , DemoApplicaion.mPlacementId_rewardvideo_toutiao
            , DemoApplicaion.mPlacementId_rewardvideo_baidu
            , DemoApplicaion.mPlacementId_rewardvideo_ks
            , DemoApplicaion.mPlacementId_rewardvideo_sigmob
            , DemoApplicaion.mPlacementId_rewardvideo_myoffer
    };

    String unitGroupName[] = new String[]{
            "All network",
            "Mintegral",
            "GDT",
            "Toutiao",
            "Baidu",
            "Kuaishou",
            "Sigmob",
            "Myoffer"
    };

    RadioGroup mRadioGroup;


    int mCurrentSelectIndex;


    ATRewardVideoAd mRewardVideoAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        mRadioGroup = (RadioGroup) findViewById(R.id.placement_select_group);

        for (int i = 0; i < placementIds.length; i++) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setPadding(20, 20, 20, 20);
            radioButton.setText(unitGroupName[i]);
            radioButton.setId(i);
            mRadioGroup.addView(radioButton);
        }

        mRadioGroup.check(0);

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                mCurrentSelectIndex = i;
                init();
            }
        });

//        mCurrentSelectIndex = 9;
        init();

        findViewById(R.id.is_ad_ready_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                boolean isReady = mRewardVideoAd.isAdReady();
//                Toast.makeText(RewardVideoAdActivity.this, "video ad ready status:" + isReady, Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.loadAd_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mRewardVideoAd.load();
            }
        });

        findViewById(R.id.show_ad_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mRewardVideoAd.show(RewardVideoAdActivity.this);
//                mRewardVideoAd.show(RewardVideoAdActivity.this, "f5e5492eca9668");
                boolean result = rewardLoadManager.showReward(RewardVideoAdActivity.this);
                if (!result) {
                    Log.i("RewardLoadManager", "show fail, no ad");
                }
            }
        });

    }


    RewardLoadManager rewardLoadManager = null;

    private void init() {
        rewardLoadManager = RewardLoadManager.getInstance(this);
        rewardLoadManager.loopLoadStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        rewardLoadManager.clean();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}

