package com.business.support.config;

import android.os.Handler;
import android.os.Looper;

public class Const {

    public final static Handler HANDLER = new Handler(Looper.getMainLooper());


    public final static String SHUMENG_URL = "https://ddi2.shuzilm.cn/q";

    public final static String SHUMEI_URL = "http://api-tw-bj.fengkongcloud.com/v4/event";

    //    private static String mUrlStr = "http://172.31.4.170:8080/v1/strategy/check";
//        private static String mUrlStr = "http://172.31.5.40:8080/v1/strategy/check";

    public final static String TOP_HOST = "http://deapi.adsgreat.cn";

    private static String getHost() {
        return TOP_HOST + "/v1/";
    }

    public final static String STRATEGY_CHECK_URL = getHost() + "strategy/check";

    public final static String IP_URL = getHost() + "ip";
}
