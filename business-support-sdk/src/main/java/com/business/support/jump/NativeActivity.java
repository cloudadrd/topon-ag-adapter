package com.business.support.jump;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;

import com.business.support.R;


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
    }


}


