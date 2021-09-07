package com.test.ad.demo;

import android.util.Log;

import me.ele.lancet.base.Origin;
import me.ele.lancet.base.annotations.Insert;
import me.ele.lancet.base.annotations.TargetClass;

public class Hook {

    //    @TargetClass("com.bytedance.frameworks.encryptor")
//    @Insert("a")
//    public static byte[] hookExecute(byte[] o, int i) {
//        byte[] bytes = (byte[]) Origin.call();
//        Log.i("tjt852-fe", new String(bytes));
//        return bytes;
//    }


    @TargetClass("com.bytedance.embedapplog.util")
    @Insert("a")
    public static byte[] hookExecute2(byte[] o, int i) {
        byte[] bytes = (byte[]) Origin.call();
        Log.i("tjt852-ue", new String(bytes));
        return bytes;
    }

}
