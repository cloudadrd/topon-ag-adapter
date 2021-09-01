package com.business.support.captcha;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.business.support.R;
import com.business.support.config.Const;
import com.business.support.utils.ContextHolder;

public class CaptchaActivity extends Activity {

    private final static String LISTENER_KEY = "listener_key";

    private Captcha captcha;

    private static CaptchaListener mCaptchaListener;

    public static void launch(CaptchaListener captchaListener) {
        Intent intent = new Intent(ContextHolder.getGlobalAppContext(), CaptchaActivity.class);
        mCaptchaListener = captchaListener;
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        ContextHolder.getGlobalAppContext().startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bs_captcha_activity);
        captcha = findViewById(R.id.captCha);
        captcha.setBitmap(R.drawable.bssdk_cat);
        captcha.setSeekBarStyle(R.drawable.bssdk_po_seekbar1, R.drawable.bssdk_thumb1);
        captcha.setCaptchaListener(new Captcha.CaptchaListener() {

            @Override
            public String onAccess(long time) {
                Const.HANDLER.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 1000);

                return mCaptchaListener.onAccess(time);
            }

            @Override
            public String onFailed(int failedCount) {
                Const.HANDLER.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        captcha.reset(false);
                    }
                }, 1200);
                return mCaptchaListener.onFailed(failedCount);
            }

            @Override
            public String onMaxFailed() {
                return "";

            }

        });
    }


}
