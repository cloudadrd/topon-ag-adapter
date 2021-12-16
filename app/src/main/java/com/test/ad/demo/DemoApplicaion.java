package com.test.ad.demo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.multidex.MultiDexApplication;

import com.anythink.core.api.ATSDK;
import com.anythink.custom.adapter.OAIDHandler;
import com.business.support.utils.ContextHolder;
import com.business.support.utils.Utils;
import com.business.support.webview.WxApi;
import com.facebook.stetho.Stetho;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.zz365.mobi.wxapi.Constants;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Z on 2018/1/10.
 */

public class DemoApplicaion extends MultiDexApplication {
    public static final String appid = "a6086931f7149b";//"";a5ff2b9464c121
    public static final String appKey = "be9b2e39d03dd60ed17870594123d7f4";
    public static final String mPlacementId_native_all = "b6018fdc99f11e";
    public static final String mPlacementId_native_mintegral = "b5aa1fa85b86d5";
    public static final String mPLacementId_native_automatic_rending_mintegral = "b5ee8aeb8f3458";
    public static final String mPlacementId_native_GDT = "b5ab8590d44f82";
    public static final String mPlacementId_native_toutiao = "b5c2c97629da0d";
    public static final String mPlacementId_native_toutiao_drawer = "b5c355d79ef9be";
    public static final String mPlacementId_native_baidu = "b60360e3ced743";
    public static final String mPlacementId_native_kuaishou = "b5e4105d4f21b6";
    public static final String mPlacementId_native_kuaishou_drawer = "b5e5dc4110310f";
    public static final String mPlacementId_native_oneway = "b5f22761b35766";
    public static final String mPlacementId_native_myoffer = "b5f33a12982b7f";
    public static final String mPlacementId_content_KS = "b600a40062eae1";
    public static final String mPlacementId_native_JD = "b601f9516ed70f";
    public static final String mPlacementId_native_IFLY = "b604887777333b";


    //RewardedVideo
    public static final String mPlacementId_rewardvideo_all = "b61af178eb7ca0";//b60f4f02b94602 b5ff41a9f64dec
    public static final String mPlacementId_rewardvideo_mintegral = "b5b449f2f58cd7";
    public static final String mPlacementId_rewardvideo_GDT = "b5c2c880cb9d52";
    public static final String mPlacementId_rewardvideo_toutiao = "b5b728e7a08cd4";
    public static final String mPlacementId_rewardvideo_uniplay = "b5badef36435e7";
    public static final String mPlacementId_rewardvideo_oneway = "b5badf5b390201";
    public static final String mPlacementId_rewardvideo_ksyun = "b5bbd61d0aa571";
    public static final String mPlacementId_rewardvideo_baidu = "b5c2c800fb3a52";
    public static final String mPlacementId_rewardvideo_ks = "b5d67459a3e535";
    public static final String mPlacementId_rewardvideo_sigmob = "b5d7228c6c5d6a";
    public static final String mPlacementId_rewardvideo_myoffer = "b5db6c3764aea3";

    //Banner
    public static final String mPlacementId_banner_all = "b60869394d051a";
    public static final String mPlacementId_banner_mintegral = "b5dd388839bf5e";
    public static final String mPlacementId_banner_GDT = "b5baca43951901";
    public static final String mPlacementId_banner_toutiao = "b5baca45138428";
    public static final String mPlacementId_banner_uniplay = "b5baca4aebcb93";
    public static final String mPLacementId_banner_baidu = "b6137091746adc";//"b5c0508c4c073f";
    public static final String mPlacementId_banner_myoffer = "b5f33a1409b96b";
    public static final String mPlacementId_banner_IFLY = "b6048875baf056";

    //Interstitial
    public static final String mPlacementId_interstitial_all = "b603f37c4ebe4e";
    public static final String mPlacementId_interstitial_mintegral = "b5bbdc725768fa";
    public static final String mPlacementId_interstitial_video_mintegral = "b5bbdc855a1506";
    public static final String mPlacementId_interstitial_GDT = "b5baca561bc100";
    public static final String mPlacementId_interstitial_toutiao = "b5baca585a8fef";
    public static final String mPlacementId_interstitial_video_toutiao = "b5baca599c7c61";
    public static final String mPlacementId_interstitial_uniplay = "b5baca5d16c597";
    public static final String mPlacementId_interstitial_oneway = "b5baca5e3d2b29";
    public static final String mPlacementId_interstitial_baidu = "b5c0508e2c84d4";
    public static final String mPlacementId_interstitial_kuaishou = "b5d6745b8133f2";
    public static final String mPlacementId_interstitial_sigmob = "b5d7614ab30695";
    public static final String mPlacementId_interstitial_myoffer = "b5db6c39aed9c5";
    public static final String mPlacementId_interstitial_AdsGreat = "b5fb222c36cb86";
    public static final String mPlacementId_interstitial_JD = "b601f94e7b83b4";

    //Splash
    public static final String mPlacementId_splash_all = "b6018fda915c06";
    public static final String mPlacementId_splash_gdt = "b5fb2229fdd388";
    public static final String mPlacementId_splash_toutiao = "b5fb2229fdd388";
    public static final String mPlacementId_splash_baidu = "b5fb2229fdd388";
    public static final String mPlacementId_splash_sigmob = "b5fb2229fdd388";
    public static final String mPlacementId_splash_mintegral = "b5fb2229fdd388";
    public static final String mPlacementId_splash_kuaishou = "b5fb2229fdd388";
    public static final String mPlacementId_splash_AdsGreat = "b5fb2229fdd388";
    public static final String mPlacementId_splash_myoffer = "b5fb2229fdd388";
    public static final String mPlacementId_splash_JD = "b601f94b030f4b";
    public static final String mPlacementId_splash_IFLY = "b604887461e0f4";

    @Override
    public void onCreate() {
        super.onCreate();
//        new CrashHandler().init(this);
//        JacocoHelper.Builder builder = new JacocoHelper.Builder();
//        builder.setApplication(this).setDebuggable(true);
//        JacocoHelper.initialize(builder.build());

        //Android 9 or above must be set
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            String processName = getProcessName();
            if (!getPackageName().equals(processName)) {
                WebView.setDataDirectorySuffix(processName);
            }
        }

        Stetho.initializeWithDefaults(getApplicationContext());
        ATSDK.setNetworkLogDebug(true);
        ATSDK.integrationChecking(getApplicationContext());

        Map<String, Object> custommap = new HashMap<String, Object>();
        custommap.put("key1", "initCustomMap1");
        custommap.put("key2", "initCustomMap2");
        ATSDK.initCustomMap(custommap);

        Map<String, Object> subcustommap = new HashMap<String, Object>();
        subcustommap.put("key1", "initPlacementCustomMap1");
        subcustommap.put("key2", "initPlacementCustomMap2");

        ATSDK.initPlacementCustomMap("b61af178eb7ca0", subcustommap);//native  facebook

        ATSDK.setChannel("testChannle");
        ATSDK.setSubChannel("testSubChannle");

//        ATSDK.init(this, appid, appKey);


//        ATSDK.init(this, "a5ff2b6c1c6fc5", "be9b2e39d03dd60ed17870594123d7f4");
        ATSDK.init(this, appid, appKey);


        OAIDHandler.init(this);

        IWXAPI api = WXAPIFactory.createWXAPI(ContextHolder.getGlobalAppContext(), Constants.APP_ID, true);
        api.registerApp(Constants.APP_ID);
        WxApi.registerWxSend(new WxApi.SendListener() {
            @Override
            public void send(String json) {
                final SendAuth.Req req = new SendAuth.Req();
                req.scope = "snsapi_userinfo,snsapi_friend,snsapi_message,snsapi_contact";
                req.state = "none";
                api.sendReq(req);
            }
        });
        WxApi.registerPay(new WxApi.PayListener() {
            @Override
            public void payStart(String json) throws JSONException {
                JSONObject jsonObj = new JSONObject(json);
                PayReq req = new PayReq();
                //req.appId = "wxf8b4f85f3a794e77";  // 测试用appId
                req.appId = jsonObj.getString("appid");
                req.partnerId = jsonObj.getString("partnerid");
                req.prepayId = jsonObj.getString("prepayid");
                req.nonceStr = jsonObj.getString("noncestr");
                req.timeStamp = jsonObj.getString("timestamp");
                req.packageValue = jsonObj.getString("package");
                req.sign = jsonObj.getString("sign");
                req.extData = "app data"; // optional
//                Toast.makeText(getApplicationContext(), "正常调起支付", Toast.LENGTH_SHORT).show();
                Log.d("jim", "check args " + req.checkArgs());
                // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
                Log.d("jim", "send return :" + api.sendReq(req));

            }
        });
        Log.e("tjt852", "external-files-path=" + this.getExternalFilesDir(null).getAbsolutePath());

        Log.e("tjt852", "external-cache-path=" + this.getExternalCacheDir().getAbsolutePath());

        Log.e("tjt852", "cache-path=" + this.getFilesDir().getAbsolutePath());


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.e("tjt852", "external-media-path=" + this.getExternalMediaDirs()[0].getAbsolutePath());
        }

    }


    private static View findViewByText(ViewGroup adContainer, String... text) {
        for (int i = 0; i < adContainer.getChildCount(); i++) {
            View childView = adContainer.getChildAt(i);
            Log.i("tjt852_view", "childView=" + childView.getClass().getSuperclass());
            if (childView instanceof ViewGroup) {
                if (((ViewGroup) childView).getChildCount() > 0) {
                    View childView2 = findViewByText((ViewGroup) childView, text);
                    if (childView2 != null) return childView2;
                }
            }
            if (childView instanceof TextView) {
                TextView textView = (TextView) childView;
                Log.i("tjt852_text", "text=" + textView.getText().toString());
                if (Arrays.binarySearch(text, textView.getText().toString()) > -1) {
                    return childView;
                }
            }
        }

        return null;
    }

    @SuppressLint("ResourceType")
    private static ImageView getCloseImg(Context context) {
        ImageView close = new ImageView(context);
        close.setBackgroundResource(R.drawable.bssdk_circle_close);
        RelativeLayout.LayoutParams closeLayoutParams = new RelativeLayout.LayoutParams(Utils.dp2px(28), Utils.dp2px(28));
        closeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        closeLayoutParams.setMargins(0, Utils.dp2px(40), Utils.dp2px(22), 0);
        close.setLayoutParams(closeLayoutParams);
        return close;
    }

}
