package com.business.support.jump;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;

import com.business.support.R;
import com.business.support.config.Const;


/**
 * Created by jiantao.tu on 2020/5/12.
 */
public class NativeActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("tjt852", "NativeActivity onCreate");
        //设置当前Activity的锁屏显示
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
//                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
//                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
//                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        Window window = getWindow();
        if (window != null) {
            window.addFlags(524288);
        }

        setContentView(R.layout.bs_activity_native);
        ViewGroup viewGroup = findViewById(R.id.ad_container);
        NativeAdManager.getInstance().show(viewGroup, this);

        Const.HANDLER.postDelayed(new Runnable() {

            @Override
            public void run() {
                Log.i("tjt852", "NativeActivity onCreate startActivity jd..1");
                final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("openapp.jdmobile://virtual?params=%7B%22sourceValue%22:%220_productDetail_97%22,%22des%22:%22productDetail%22,%22skuId%22:%22\"+\"10031895050322\"+\"%22,%22category%22:%22jump%22,%22sourceType%22:%22PCUBE_CHANNEL%22%7D "));
                intent.addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                Log.i("tjt852", "NativeActivity onCreate startActivity jd..2");
            }
        }, 5000);
    }

}


