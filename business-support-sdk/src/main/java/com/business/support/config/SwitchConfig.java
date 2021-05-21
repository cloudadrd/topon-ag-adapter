package com.business.support.config;

import android.os.Build;

import com.business.support.BuildConfig;


public class SwitchConfig {


    /**
     * 测试开关
     */
    public static Boolean ISDEBUG = BuildConfig.DEBUG;

    /**
     * 日志开关
     */
    public static Boolean LOG = BuildConfig.DEBUG;   //TODO:  日志开关,一直关着就行


}
