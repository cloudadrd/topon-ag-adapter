package com.test.ad.demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.anythink.custom.adapter.OAIDHandler;
import com.business.support.StrategyInfoListener;
import com.business.support.TaskMonitorListener;
import com.business.support.WhiteService;
import com.business.support.YMBusinessService;
import com.business.support.adinfo.BSAdType;
import com.business.support.ascribe.InstallListener;
import com.business.support.captcha.CaptchaListener;
import com.business.support.compose.SIDListener;
import com.business.support.config.Const;
import com.business.support.utils.SLog;
import com.business.support.webview.CacheWebView;
import com.business.support.webview.InnerWebViewActivity;
import com.business.support.webview.InnerWebViewActivity2;
import com.business.support.webview.WebViewToNativeListener;
//import com.vilyever.socketclient.SocketClient;
//import com.vilyever.socketclient.util.IPUtil;

import cn.thinkingdata.android.TDConfig;
import cn.thinkingdata.android.ThinkingAnalyticsSDK;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
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
//                YMBusinessService.getDeviceInfo(MainActivity.this, "1839996660099");
//                YMBusinessService.getAdChannel(MainActivity.this, "1004", new GetAdChannelListener() {
//                    @Override
//                    public void adChannel(String channel) {
//                        Log.d("channel**",channel);
//                    }
//                });
                startActivity(new Intent(MainActivity.this, NativeAdActivity.class));

            }
        });


        YMBusinessService.setFirstInstallTime(System.currentTimeMillis());
        YMBusinessService.setRewardedVideoTimes(1);
        YMBusinessService.requestRewaredConfig(this, "1004", new StrategyInfoListener() {
            @Override
            public void isActive(boolean isActive, boolean install) {
                Log.i("YMBusinessService", "isActive=" + isActive + ",install=" + install);
            }
        });

        findViewById(R.id.rewardedVideoBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RewardVideoAdActivity.class));
            }
        });

        findViewById(R.id.rewardedVideoBtn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RewardVideoAdActivity2.class));
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
                    public void event3(Activity activity, String params) {
                        activity.finish();
                    }

                });
            }
        });


        findViewById(R.id.webviewBtn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //不带缓存的webview content://com.scqdd.mobi.bssdk/bs_external_res_h5/forumweb/forumweb/index.html?user=1
                //file:///android_asset/forumweb/index.html?appId=111&token=c8f4e78d-f372-43f9-82f6-275de3421cf5
                YMBusinessService.startWebViewPage(MainActivity.this, "file:///android_asset/test.html", new WebViewToNativeListener() {

                    @Override
                    public void event1(InnerWebViewActivity activity) {

                    }

                    @Override
                    public void event2(InnerWebViewActivity2 activity) {

                    }

                    @Override
                    public void event3(Activity activity, String params) {
                        activity.finish();
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

        findViewById(R.id.startAdApp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean result = YMBusinessService.startCurrentAdApp("1");
                Log.i(TAG, "是否启动成功 result=" + result);

            }
        });

        findViewById(R.id.startAdApp2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean result = YMBusinessService.startCurrentAdApp("2");
                Log.i(TAG, "是否启动成功 result=" + result);

            }
        });

        YMBusinessService.setAndRefreshTaskMonitor(new TaskMonitorListener() {
            @Override
            public void over(String sceneId) {
                Log.i(TAG, "setAndRefreshTaskMonitor 任务完成  ok sceneId=" + sceneId);
            }
        });

        findViewById(R.id.customBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(MainActivity.this, WhiteService.class));
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
        YMBusinessService.enableAdTrace(new InstallListener() {
            @Override
            public void installedHit(String pkg, String appName, BSAdType bsAdType, String sceneId) {
                SLog.i(TAG, "installedHit pkg=" + pkg + ",sceneId=" + sceneId + ",appName=" + appName);
            }
        });
//        ResUpdateManager.getH5ResPathAndUpdate("95", "95", 101, new ResH5Listener() {
//
//            /**
//             * 资源获取回调函数
//             * @param isSuccess 是否成功返回
//             * @param path 返回的H5主页面地址
//             */
//            @Override
//            public void result(boolean isSuccess, String path) {
//                Log.i("check-tjt", "getH5ResPathAndUpdate result isSuccess=" + isSuccess + ",path=\n" + path);
//                if (!isSuccess) return;
//                YMBusinessService.startWebViewPage(MainActivity.this, path + "?appId=111&token=c8f4e78d-f372-43f9-82f6-275de3421cf5",
//                        "b5fb2228113cf7", new WebViewToNativeListener() {
//                            @Override
//                            public void event1(InnerWebViewActivity activity) {
//
//                            }
//
//                            @Override
//                            public void event2(InnerWebViewActivity2 activity) {
//
//                            }
//
//                            @Override
//                            public void tracking(String name, JSONObject properties) {
////                        AppActivity.app.biInstance.track(name, properties);
//                            }
//                        });
//            }
//        });

        YMBusinessService.setH5RewardPlacementId("广告位ID");
        YMBusinessService.setH5InterstitialPlacementId("b603f37c4ebe4e");

        YMBusinessService.startCaptcha(new CaptchaListener() {
            @Override
            public String onAccess(long time) {
                Log.i(TAG, "onAccess time=" + time);
                return "验证通过,耗时" + time + "毫秒";
            }

            @Override
            public String onFailed(int failCount) {
                Log.i(TAG, "onFailed failCount=" + failCount);
                if (failCount > 4) {
                    Const.HANDLER.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            System.exit(1);
                        }
                    }, 1500);
                    return "验证失败,帐号已封锁";
                }
                return "验证失败,已失败" + failCount + "次";
            }

        });



    }


    @Override
    protected void onResume() {
        super.onResume();
        YMBusinessService.stopTaskMonitor();
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
