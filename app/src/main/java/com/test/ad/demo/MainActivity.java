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
import android.widget.Toast;

import com.anythink.custom.adapter.OAIDHandler;
import com.business.support.StrategyInfoListener;
import com.business.support.TaskMonitorListener;
import com.business.support.WhiteService;
import com.business.support.YMBusinessService;
import com.business.support.adinfo.BSAdType;
import com.business.support.adinfo.TKCreator;
import com.business.support.ascribe.InstallListener;
import com.business.support.captcha.CaptchaListener;
import com.business.support.compose.SIDListener;
import com.business.support.config.Const;
import com.business.support.deferred.DefaultAndroidDeferredManager;
import com.business.support.utils.ImageResultListener;
import com.business.support.utils.SLog;
import com.business.support.webview.CacheWebView;
import com.business.support.webview.ImagePreserve;
import com.business.support.webview.InnerWebViewActivity;
import com.business.support.webview.InnerWebViewActivity2;
import com.business.support.webview.WebViewToNativeListener;
import com.business.support.webview.WxApi;


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
//                stopService(new Intent(MainActivity.this, WhiteService.class));
//                WxApi.pay("{\n" +
//                        "    \"appid\": \"wxe420bdf1b73df10f\" ,\n" +
//                        "    \"noncestr\": \"20211013114434608111433865363456\" , \n" +
//                        "    \"package\": \"Sign=WXPay\" ,\n" +
//                        "    \"partnerid\": \"1598042261\" ,\n" +
//                        "    \"prepayid\": \"wx131144346840937d6439b9a9a368910000\" ,\n" +
//                        "    \"sign\": \"OWT4y4JFgrugF+KA5UAF9qTUvNX/9y7qMkX9Y8XkE5nyyMjUQVrpkK/QC7t9F0Tt88OTo+Z3JyCVnNN0tYbiCoRWIvy+y6XLQ7G+sy0Vzud4/ijDmVA1lOUGOv5hMxdeddDDe0JBaH8LbpD/dy7Ky+dvsc5Ndvb5pYtDrhbBpkG4IlLLk3VjfOL2/uEsYOivfWnunu4V4AWRzcwOLqSkCuDK0pSTfMQsPj8qDahNtjon888X2urL1Gv0s8wkvqw94lasTyUJR9JS7JvDLgZdlZIUJhJQCf+qzD1GABbkkunvNyr0YaFzDU+V0y1mUq/duh/lEDue+yj0okJHEGXFQA==\" ,\n" +
//                        "    \"timestamp\": \"1634096674\" \n" +
//                        "}");
                YMBusinessService.startCurrentAdApp("123", 10000);
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
        ThinkingAnalyticsSDK.calibrateTimeWithNtp("time.windows.com");
        YMBusinessService.init(this, biInstance,
                "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAMsZuh7bnTRuNGmu8urpyfvB5NERn6Z1dylHYD2Lgs2nKTUYJDoKsU+ALI21MY0NPif3YgdKgzMRZWg3zTL8fA8CAwEAAQ==",
                "39ee4d74c93c967def52dbec1e592d20",
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


        //支付宝登录
//        Const.HANDLER.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                AliPayApi.openAuthScheme(MainActivity.this, "2021002176610585");
//            }
//        }, 8000);

//        ImagePreserve.downloadToSysPicture("https://pic2.zhimg.com/80/v2-fca32e14dea7f716d425d337a4f201f5_720w.jpg", new ImageResultListener() {
//            @Override
//            public void onSuccess() {
//                Log.e("tjt852", "downloadToSysPicture onSuccess");
//            }
//
//            @Override
//            public void onFailure(String message) {
//                Log.e("tjt852", "downloadToSysPicture onFailure message=" + message);
//            }
//        });

//        ImagePreserve.base64ToSysPicture("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACkAAAAnCAYAAACSamGGAAAAAXNSR0IArs4c6QAABRlJREFUWAm1WGtsFFUUPq3Usi3QLhSUWqCAQCtFI8hDrDEBBapYjCSgCYlGIkREovJD1B/y+EWCqFGDQUNIjIn4ShCNiQSpIRLEpgYCETBKK+9njaClVlq/b2buvTOT2Z3Zme1Jvj3n3Oc35763oGf7eMmjlKKtz4D1QBMQLo2HQsv0CS2RW4EtKD4HmAhUAteBxFKYuAXTQC3MBY47BPpJx06s8knydR+b53x+bDdfJFNg0OBjcQf8+3xpsdx8keRCsWXw3cqiftvtxLXzQXIkOjdDW/2YSFE/xWcCjMXKiavzQfIN3XnVwyJDZ4rUrdJJMDyOOyOqnZTkzehonu5sJKJIGfaISHGFbYvcCmOKcuLopCSf0Z1WzhJJc604UvWQsqgTzc0kJBnFVzST6oXatAz6hUUqjZGcq5xcdRKSnIv2icUIVkz19l06XKRmuTvtfbeTi53LsZhGw7XAYKAacCYgrNoV+AkQRrP1E5F/TjGTkV8NHAbOAceAs0CoFARcMIaiVg3ArYWaxEYDI4ASwCtjl6BUBpIsefJrkZaXvHVsrwvqBPAbcBQ4ApD4r8ApaTzEfEtIchGsScBtwBigCtCTCXZmqZgmMv2DzPkqp/lFkdPfKi+KZoQV6RaS7IlSyypThu9IYdRKKkUG4qLDFR1VLu4TudQicgWB6zgj0n4wak0hyWUo/a6nRvp2kf4Y4X6joIGSYTaxG/p6iiVyuv8TuYaA/Y0Rv3rcQSv8P9QcVs1vVHPyXqR8DxRYOcUDRSa/aUdLFe1t3dMtsvcpRLvZ3dNSzM3NhU7KHmhzcnReRoWn8XWt7gq9Z5Ng03w/wZUkyE4VSdo7gJnAn3Sku9P+Ms6l3pTOSyL7lmKuclFreRYENypPDbfyqe8CfnInSP2HGPo7PUl5cUiw6VERaiOPg+DHxvVGUqVzUswBuOHasn+FyIW9ysuP7jgt0rzST/AFP0F2FhRJRYKbd6tyLD1tk8gQrrGEwsWxH0dm11V3Q/eD4C53grLdc1KlKd0GYwZgxuLAOpWXTB9c6ye4PBNBdpSNJPN3AzXACTrCIWr71DJj/7QfwCL5XVVnAOpB0LtPq1xHh5FksYvAq055+9TQTgyDp46RLSD4g3GDrSgkWZPnuS08FpNIH/7JoWWAtrIYUUnW6zaSbkUlvGRpwaUzXKKSvEU3lbpJm7GM/nzyaBmvrSxGVJI4zB0pKlNWPJ1CJM2UGS5f1uH2kl2i3MwHoQkTvmK6AdL1F3bVbfbCGjQR9/d7RMp5RQ2QAeOwU5xVGXh7OLuHSvHpKCTrUOdGq156gvtxZTd1vcMmd3iDafr8HpFf3sJdfj6u0UtwzTOzxSpUhsv+OV66LOF5+5VjB6qoJO3K5eTryL/tNjm+Ya6dV6le3fY59tUv8Ph4Aq+ihSKlzsjmOC+jzMlJumfezCknd4jsnIUL/jt+gnwjPAggjEp68IrZKrKrAS+Y90T4ceWIpBHMjewShaRpsRCjvnseHlYv4+9RDLOR4zDxt4XMBr4BngemA96Nmh/1XSMuKz8iS8soLB6zMHWyMbJdMFiKO+8FIEUnQH5G2ipgJ4CQBcpUpL4GNHhz+QjQVTJeLlgnLJJjUSaI4GWk46Zq/e3MIda9wfYLw8YpwCgfNZmeKln3yzCSM0yjlnUMv8uAMcBmKyX6Dz+mFlgEeKeByAPZmgkjmUblK8AZYA3ATjYBjGQcYfg+AnjMLgaOAGy/L5BR/gdh3B5llTJohgAAAABJRU5ErkJggg==", new ImageResultListener() {
//            @Override
//            public void onSuccess() {
//                Log.e("tjt852", "base64ToSysPicture onSuccess");
//            }
//
//            @Override
//            public void onFailure(String message) {
//                Log.e("tjt852", "base64ToSysPicture onFailure message=" + message);
//            }
//        });


        TKCreator.send(this, "12321");

//        YMBusinessService.traceInstall2("com.tencent.mm.openapi", "123");

        gDM.when(() -> {
            Log.i("tjt852", "thread-when=" + Thread.currentThread().getName());
//            throw new RuntimeException("nihaoya when error");
            return "sss";
        }).progress(f -> {

        }).then(v -> {
            Log.i("tjt852", "thread-then=" + Thread.currentThread().getName() + ",v=" + v);
//            throw new RuntimeException("nihaoya then error");

        }).fail(f -> {
            Log.i("tjt852", "thread-fail=" + Thread.currentThread().getName() + ",f=" + f.getMessage());
        }).done(d -> {
            Log.i("tjt852", "thread-done=" + Thread.currentThread().getName() + ",f=" + d.toString());
        });


//        Const.HANDLER.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(getBaseContext(), String.valueOf(ThinkingAnalyticsSDK.getTimeFormat()), Toast.LENGTH_LONG).show();
//                Const.HANDLER.postDelayed(this, 2000);
//            }
//        }, 2000);
    }

    private static final DefaultAndroidDeferredManager gDM = new DefaultAndroidDeferredManager();


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
