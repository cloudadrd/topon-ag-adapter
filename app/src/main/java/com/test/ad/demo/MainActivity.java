package com.test.ad.demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.anythink.custom.adapter.OAIDHandler;
import com.baidu.mobads.sdk.api.AppActivity;
import com.business.support.StrategyInfoListener;
import com.business.support.YMBusinessService;
import com.business.support.ascribe.InstallListener;
import com.business.support.ascribe.InstallStateMonitor;
import com.business.support.compose.SIDListener;
import com.business.support.webview.CacheWebView;
import com.business.support.webview.InnerWebViewActivity;
import com.business.support.webview.InnerWebViewActivity2;
import com.business.support.webview.WebViewToNativeListener;

import org.json.JSONObject;

import cn.thinkingdata.android.TDConfig;
import cn.thinkingdata.android.ThinkingAnalyticsSDK;

public class MainActivity extends Activity {

    CacheWebView cacheWebView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cacheWebView = new CacheWebView(this);
        //http://redbag.adspools.cn:8081/?appId=119&token=ad6736e3-8384-42b0-90de-11924877129a&uid=20210324105106534533243063316480&IMEI=cd389fbee1d57a31231365551111&team=002&isNew=false
        cacheWebView.loadUrl("https://m.baidu.com");
        findViewById(R.id.nativeAdBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NativeAdActivity.class));
                Context context = getBaseContext();
                YMBusinessService.setFirstInstallTime(System.currentTimeMillis());
                YMBusinessService.setRewardedVideoTimes(1);
                YMBusinessService.requestRewaredConfig(context, "40827c9f08dd4fe5f909a0e8b6355706", new StrategyInfoListener() {
                    @Override
                    public void isActive(boolean isActive) {
                        if (isActive) {
                            Log.i("YMBusinessService", "true");
                        } else {
                            Log.i("YMBusinessService", "false");
                        }
                    }
                });
            }
        });

        findViewById(R.id.rewardedVideoBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RewardVideoAdActivity.class));
            }
        });

        findViewById(R.id.interstitialBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, InterstitialAdActivity.class));
            }
        });

        findViewById(R.id.bannerBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, BannerAdActivity.class));
            }
        });

        findViewById(R.id.splashBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SplashAdActivity.class));
            }
        });

        findViewById(R.id.nativeBannerAdBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NativeBannerActivity.class));
            }
        });

        findViewById(R.id.nativeSplashAdBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NativeSplashActivity.class));
            }
        });

        findViewById(R.id.nativeListBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NativeListActivity.class));
            }
        });

        findViewById(R.id.webviewBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                YMBusinessService.startCacheWebViewPage(MainActivity.this, cacheWebView, new WebViewToNativeListener() {
                    @Override
                    public void event1(InnerWebViewActivity activity) {

                    }

                    @Override
                    public void event2(InnerWebViewActivity2 activity) {

                    }

                    @Override
                    public void tracking(String name, JSONObject properties) {
//                        AppActivity.app.biInstance.track(name, properties);
                    }
                });
            }
        });


        findViewById(R.id.webviewBtn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //不带缓存的webview
                YMBusinessService.startWebViewPage(MainActivity.this, "file:///android_asset/test.html", new WebViewToNativeListener() {
                    @Override
                    public void event1(InnerWebViewActivity activity) {

                    }

                    @Override
                    public void event2(InnerWebViewActivity2 activity) {

                    }

                    @Override
                    public void tracking(String name, JSONObject properties) {
//                        AppActivity.app.biInstance.track(name, properties);
                    }
                });
            }
        });


        findViewById(R.id.contentBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ContentAdActivity.class);
                startActivity(intent);
            }
        });

//        Const.HANDLER.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (cacheWebView.getParent() != null && cacheWebView.getParent() instanceof ViewGroup) {
//                    ViewGroup viewGroup = (ViewGroup) cacheWebView.getParent();
//                    viewGroup.removeView(cacheWebView);
//                }
//                InnerWebViewActivity.launch(MainActivity.this, cacheWebView);
//            }
//        },10000);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this).setIcon(R.mipmap.ic_launcher).setTitle("OAID")
                        .setMessage(OAIDHandler.getOAID()).setPositiveButton("复制", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                putTextIntoClip(MainActivity.this, OAIDHandler.getOAID());
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                builder.create().show();
            }
        }, 1000);


//        ResultData emulatorResult = EmulatorCheck.validCheck(this);
//
//        ResultData rootResult = RootCheck.validCheck(this);
//
//        ResultData hookResult = HookCheck.validCheck(this);
//
//        ResultData wireSharkResult = WireSharkCheck.validCheck(this);
//
//        Log.i("check", "isEmulator=" + emulatorResult.isError() + ",errorMessage=" + emulatorResult.getErrorMessage());
//
//        Log.i("check", "isRoot=" + rootResult.isError() + ",errorMessage=" + emulatorResult.getErrorMessage());
//
//        Log.i("check", "isHook=" + hookResult.isError() + ",errorMessage=" + emulatorResult.getErrorMessage());
//
//        Log.i("check", "isWireShark=" + wireSharkResult.isError() + ",errorMessage=" + emulatorResult.getErrorMessage());

        TDConfig biConfig = TDConfig.getInstance(this, "a697ed0e5fb34fba839cd1694b69d84a", " https://biapi.adsgreat.cn/logbu");
        biConfig.setMode(TDConfig.ModeEnum.DEBUG);
        ThinkingAnalyticsSDK biInstance = ThinkingAnalyticsSDK.sharedInstance(biConfig);

        YMBusinessService.init(this, biInstance,
                "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAMsZuh7bnTRuNGmu8urpyfvB5NERn6Z1dylHYD2Lgs2nKTUYJDoKsU+ALI21MY0NPif3YgdKgzMRZWg3zTL8fA8CAwEAAQ==",
                new SIDListener() {
                    @Override
                    public void onSuccess(int score, String data) {
                        Toast.makeText(getBaseContext(), "score=" + score + "\n" + "data=" + data, Toast.LENGTH_LONG).show();
                        Log.i("check-tjt", "onSuccess score=" + score + ",data=\n" + data);
                    }

                    @Override
                    public void onFailure(String msg) {
                        Log.i("check-tjt", "onFailure msg=\n" + msg);
                    }
                });


    }


    /**
     * 复制到剪贴板
     *
     * @param context
     * @param text
     */
    public static void putTextIntoClip(Context context, String text) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        //创建ClipData对象
        ClipData clipData = ClipData.newPlainText("HSFAppDemoClip", text);
        //添加ClipData对象到剪切板中
        clipboardManager.setPrimaryClip(clipData);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cacheWebView.destroy();
    }
}
