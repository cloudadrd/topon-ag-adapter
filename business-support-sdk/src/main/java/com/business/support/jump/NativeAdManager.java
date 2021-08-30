package com.business.support.jump;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.anythink.core.api.ATAdConst;
import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.AdError;
import com.anythink.nativead.api.ATNative;
import com.anythink.nativead.api.ATNativeAdView;
import com.anythink.nativead.api.ATNativeDislikeListener;
import com.anythink.nativead.api.ATNativeEventExListener;
import com.anythink.nativead.api.ATNativeNetworkListener;
import com.anythink.nativead.api.NativeAd;
import com.business.support.YMBusinessService;
import com.business.support.utils.ContextHolder;
import com.business.support.utils.Utils;
import com.business.support.webview.AdVideoMediation;
import com.business.support.widget.ContinueFrameLayout;

import java.util.HashMap;
import java.util.Map;

public class NativeAdManager {

    private static final String TAG = "NativeAdManager";
    ATNativeAdView anyThinkNativeAdView;
    NativeAd mNativeAd;

    ATNative atNative;

    NativeDemoRender anyThinkRender = null;


    ContinueFrameLayout nativeLayout = null;

    int containerHeight = Utils.dp2px(268);
    int padding = 0;//Utils.dp2px(10);

    private static class Holder {
        @SuppressLint("StaticFieldLeak")
        private static final NativeAdManager MANAGER = new NativeAdManager();
    }

    public static NativeAdManager getInstance() {
        return NativeAdManager.Holder.MANAGER;
    }


    private NativeAdManager() {
        final Context context = ContextHolder.getGlobalAppContext();
        atNative = new ATNative(context, "b6018fdc99f11e", new ATNativeNetworkListener() {
            @Override
            public void onNativeAdLoaded() {
                Log.i(TAG, "onNativeAdLoaded");
                Toast.makeText(context, "load success..."
                        , Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNativeAdLoadFail(AdError adError) {
                Log.i(TAG, "onNativeAdLoadFail, " + adError.getFullErrorInfo());
                Toast.makeText(context, "load fail...：" + adError.getFullErrorInfo(), Toast.LENGTH_LONG).show();

            }
        });
        final int adViewWidth = context.getResources().getDisplayMetrics().widthPixels - 2 * padding;
        final int adViewHeight = containerHeight - 2 * padding;

        Map<String, Object> localMap = new HashMap<>();

        // since v5.6.4
        localMap.put(ATAdConst.KEY.AD_WIDTH, adViewWidth);
        localMap.put(ATAdConst.KEY.AD_HEIGHT, adViewHeight);

        atNative.setLocalExtra(localMap);
        nativeLayout = YMBusinessService.getNativeViewByStyle();

        anyThinkNativeAdView = new ATNativeAdView(context);

        anyThinkRender = new NativeDemoRender(context);
    }

    public void load(final Context context) {

        atNative.makeAdRequest();
    }

    public void show(ViewGroup viewGroup, Context context) {
        if (anyThinkNativeAdView.getParent() == null) {
//            anyThinkNativeAdView.setPadding(padding, padding, padding, padding);
            anyThinkNativeAdView.setVisibility(View.GONE);

            if (nativeLayout != null) {
                nativeLayout.addView(anyThinkNativeAdView, new FrameLayout.LayoutParams(context.getResources().getDisplayMetrics().widthPixels, containerHeight));
                viewGroup.addView(nativeLayout, new FrameLayout.LayoutParams(context.getResources().getDisplayMetrics().widthPixels, containerHeight));
            } else {
                viewGroup.addView(anyThinkNativeAdView, new FrameLayout.LayoutParams(context.getResources().getDisplayMetrics().widthPixels, containerHeight));
            }
        }
        NativeAd nativeAd = atNative.getNativeAd();
        if (nativeAd != null) {
            if (mNativeAd != null) {
                mNativeAd.destory();
            }
            mNativeAd = nativeAd;
            mNativeAd.setNativeEventListener(new ATNativeEventExListener() {
                @Override
                public void onDeeplinkCallback(ATNativeAdView view, ATAdInfo adInfo, boolean isSuccess) {
                    Log.i(TAG, "onDeeplinkCallback:" + adInfo.toString() + "--status:" + isSuccess);
                }

                @Override
                public void onAdImpressed(ATNativeAdView view, ATAdInfo entity) {
                    Log.i(TAG, "native ad onAdImpressed:\n" + entity.toString());
                    if (nativeLayout != null) {
                                /*
                                   调用此方法，会立即显示诱导按钮。
                                   此处的4000是4秒的意思，根据弹窗倒计时来的，弹窗的倒计时是多少秒此处就设置多少
                                 */
                        nativeLayout.display(4000);
                    }

                    //曝光日志code start

                    //上报日志时需要带上的数据
                    String customNativeStyle = YMBusinessService.getCustomNativeStyle();
                    //上报日志时的逻辑
                    if (customNativeStyle != null) {
                        //jsonObject.put("state", customNativeStyle);
                    }

                    //曝光日志code end
                }

                @Override
                public void onAdClicked(ATNativeAdView view, ATAdInfo entity) {
                    Log.i(TAG, "native ad onAdClicked:\n" + entity.toString());
                    //曝光日志code start

                    //上报日志时需要带上的数据
                    String customNativeStyle = YMBusinessService.getCustomNativeStyle();
                    //上报日志时的逻辑
                    if (customNativeStyle != null) {
                        //jsonObject.put("state", customNativeStyle);
                    }

                    //曝光日志code end
                }

                @Override
                public void onAdVideoStart(ATNativeAdView view) {
                    Log.i(TAG, "native ad onAdVideoStart");
                }

                @Override
                public void onAdVideoEnd(ATNativeAdView view) {
                    Log.i(TAG, "native ad onAdVideoEnd");
                }

                @Override
                public void onAdVideoProgress(ATNativeAdView view, int progress) {
                    Log.i(TAG, "native ad onAdVideoProgress:" + progress);
                }
            });
            mNativeAd.setDislikeCallbackListener(new ATNativeDislikeListener() {
                @Override
                public void onAdCloseButtonClick(ATNativeAdView view, ATAdInfo entity) {
                    Log.i(TAG, "native ad onAdCloseButtonClick");
                    if (view.getParent() != null) {
                        ((ViewGroup) view.getParent()).removeView(view);
                    }
                }
            });
            try {
                mNativeAd.renderAdView(anyThinkNativeAdView, anyThinkRender);
            } catch (Exception e) {

            }

            anyThinkNativeAdView.setVisibility(View.VISIBLE);
            mNativeAd.prepare(anyThinkNativeAdView, anyThinkRender.getClickView(), null);
        } else {
            Toast.makeText(context, "this placement no cache!", Toast.LENGTH_LONG).show();

        }
    }

}
