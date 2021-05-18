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

import com.anythink.custom.adapter.OAIDHandler;
import com.business.support.ascribe.InstallListener;
import com.business.support.ascribe.InstallStateMonitor;
import com.business.support.reallycheck.EmulatorCheck;
import com.business.support.reallycheck.HookCheck;
import com.business.support.reallycheck.ResultData;
import com.business.support.reallycheck.RootCheck;
import com.business.support.reallycheck.WireSharkCheck;

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
                if (cacheWebView.getParent() != null && cacheWebView.getParent() instanceof ViewGroup) {
                    ViewGroup viewGroup = (ViewGroup) cacheWebView.getParent();
                    viewGroup.removeView(cacheWebView);
                }
                InnerWebViewActivity.launch(MainActivity.this, cacheWebView);
            }
        });


        findViewById(R.id.webviewBtn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, InnerWebViewActivity2.class));
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


        ResultData emulatorResult = EmulatorCheck.validCheck(this);

        ResultData rootResult = RootCheck.validCheck(this);

        ResultData hookResult = HookCheck.validCheck(this);

        ResultData wireSharkResult = WireSharkCheck.validCheck(this);

        Log.i("check", "isEmulator=" + emulatorResult.isError() + ",errorMessage=" + emulatorResult.getErrorMessage());

        Log.i("check", "isRoot=" + rootResult.isError() + ",errorMessage=" + emulatorResult.getErrorMessage());

        Log.i("check", "isHook=" + hookResult.isError() + ",errorMessage=" + emulatorResult.getErrorMessage());

        Log.i("check", "isWireShark=" + wireSharkResult.isError() + ",errorMessage=" + emulatorResult.getErrorMessage());

        InstallStateMonitor.register(this, new InstallListener() {
            @Override
            public void installedHit(String pkg) {
                Log.i("check-tjt", "pkg=" + pkg);
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
