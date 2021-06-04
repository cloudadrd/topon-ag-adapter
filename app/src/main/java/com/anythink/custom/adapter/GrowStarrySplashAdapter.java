//package com.anythink.custom.adapter;//package com.anythink.custom.adapter;
//
//import android.app.Activity;
//import android.content.Context;
//import android.os.CountDownTimer;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.adsgreat.base.core.AGNative;
//import com.adsgreat.base.core.AdsgreatSDK;
//import com.anythink.splashad.unitgroup.api.CustomSplashAdapter;
//import com.anythink.splashad.unitgroup.api.CustomSplashEventListener;
//import com.growstarry.kern.callback.AdEventListener;
//import com.growstarry.kern.config.Const;
//import com.growstarry.kern.core.GTNative;
//import com.growstarry.kern.core.GrowsTarrySDK;
//import com.growstarry.kern.vo.AdsNativeVO;
//import com.growstarry.kern.vo.AdsVO;
//
//import java.util.Map;
//
//public class AdsGreatSplashAdapter extends CustomSplashAdapter {
//    private final String TAG = "AdsGreatSplashAdapter:";
//
//    String slotId = null;
//    private CountDownTimer timer;
//    private int fetchDelay = 3000;
//    private boolean isTimerOut;
//    private boolean isDestroyed;
//    private static boolean isSplashReaday;
//    private CustomSplashEventListener ImpListener;
//
//    @Override
//    public void loadCustomNetworkAd(final Context context, Map<String, Object> serverExtra, Map<String, Object> localExtra) {
//        if (serverExtra.containsKey("slot_id")) {
//            slotId = (String) serverExtra.get("slot_id");
//            if (slotId == null) {
//                if (mLoadListener != null) {
//                    mLoadListener.onAdLoadError("", " slot_id is empty!");
//                }
//                return;
//            }
//        } else {
//            if (mLoadListener != null) {
//                mLoadListener.onAdLoadError("", " slot_id is empty!");
//            }
//            return;
//        }
//        if (serverExtra.containsKey("fetchDelay")) {
//            try {
//                fetchDelay = Integer.parseInt((String) serverExtra.get("fetchDelay"));
//            } catch (Exception e) {
//            }
//        }
//        startLoad(context, slotId);
//    }
//
//    private void startLoad(Context context, String slotID) {
//        GrowsTarrySDK.initialize(context, slotID);
//        GrowsTarrySDK.preloadSplashAd(context, slotID, new SplashEventListener() {
//
//            @Override
//            public void onReceiveAdSucceed(GTNative result) {
//                if (isDestroyed) {
//                    return;
//                }
//
//                Log.d(TAG, "Splash Ad Loaded.");
//                isSplashReaday = true;
//            }
//
//            @Override
//            public void onReceiveAdFailed(GTNative result) {
//                if (isDestroyed) {
//                    return;
//                }
//                if (result != null && result.getErrorsMsg() != null)
//                    Log.e(TAG, "onReceiveAdFailed errorMsg=" + result.getErrorsMsg());
//                if (mLoadListener != null) {
//                    mLoadListener.onAdLoadError("", result.getErrorsMsg());
//                }
//            }
//
//
//            @Override
//            public void onLandPageShown(GTNative result) {
//                if (isDestroyed) {
//                    return;
//                }
//                Log.d(TAG, "onLandPageShown");
//            }
//
//            @Override
//            public void onAdClicked(GTNative result) {
//                if (isDestroyed) {
//                    return;
//                }
//                Log.d(TAG, "onAdClicked");
//                if (mImpressionListener != null) {
//                    mImpressionListener.onSplashAdClicked();
//                }
//            }
//
//            @Override
//            public void onAdClosed(GTNative result) {
//                if (isDestroyed) {
//                    return;
//                }
//                Log.d(TAG, "onAdClosed");
//                if (mImpressionListener != null) {
//                    mImpressionListener.onSplashAdDismiss();
//                }
//            }
//        });
//
//        timer = new CountDownTimer(fetchDelay, 1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//                if (isSplashReaday) {
//                    if (timer != null) {
//                        timer.cancel();
//                    }
//                }
//            }
//
//            @Override
//            public void onFinish() {
//                isTimerOut = true;
//                if (timer != null) {
//                    timer.cancel();
//                }
//                if (isDestroyed) {
//                    return;
//                }
//                mLoadListener.onAdLoadError("", "AGSDK get splash Ad time out!");
//                Log.d(TAG, "AGSDK get splash Ad time out!");
//            }
//        };
//        timer.start();
//    }
//
//    @Override
//    public String getNetworkName() {
//        return Const.getVersionNumber();
//    }
//
//    @Override
//    public boolean isAdReady() {
//        return isSplashReaday;
//    }
//
//    @Override
//    public void destory() {
//        if (timer != null) {
//            timer.cancel();
//        }
//        isDestroyed = true;
//    }
//
//    @Override
//    public String getNetworkPlacementId() {
//        return slotId;
//    }
//
//    @Override
//    public String getNetworkSDKVersion() {
//        return Const.getVersionNumber();
//    }
//
//    @Override
//    public void show(Activity activity, final ViewGroup viewGroup) {
//        if (!isTimerOut) {
//            if (mLoadListener != null) {
//                mLoadListener.onAdCacheLoaded();
//            }
//            postOnMainThread(new Runnable() {
//                public void run() {
//                    GrowsTarrySDK.showSplashAd(slotId, new SplashEventListener());
//                    if (mImpressionListener != null) {
//                        mImpressionListener.onSplashAdShow();
//                    }
//                }
//            });
//        }
//    }
//
//
//    static class SplashEventListener extends AdEventListener {
//
//        @Override
//        public void onReceiveAdSucceed(GTNative result) {
//            showMsg("onReceiveAdSucceed");
//        }
//
//        @Override
//        public void onReceiveAdVoSucceed(AdsVO result) {
//            showMsg("onReceiveAdVoSucceed");
//        }
//
//        @Override
//        public void onReceiveAdFailed(GTNative result) {
//            showMsg(result.getErrorsMsg());
//            Log.i("sdksample", "==error==" + result.getErrorsMsg());
//        }
//
//        @Override
//        public void onShowSucceed(GTNative gtNative) {
//            Log.i("sdksample", "onShowSucceed");
//        }
//
//        @Override
//        public void onLandPageShown(GTNative result) {
//            showMsg("onLandPageShown");
//        }
//
//        @Override
//        public void onAdClicked(GTNative result) {
//            showMsg("onAdClicked");
//        }
//
//        @Override
//        public void onAdClosed(GTNative result) {
//            showMsg("onAdClosed");
//        }
//
//        private void showMsg(String msg) {
//            Log.d("AdsGreatSplashAdapter:", msg);
//        }
//    }
//}
