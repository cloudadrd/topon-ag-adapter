package com.business.support.utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PreferenceTools {


    public static void persistString(Context context, String namespace, String key, String value) {
        try {
            SharedPreferences sp = context.getSharedPreferences(namespace, MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(key, value);
            editor.apply();
        } catch (Exception e) {
            SLog.w(e);
        }
    }


    public static String getString(Context context, String namespace, String key, String defVal) {
        try {
            SharedPreferences sp = context.getSharedPreferences(namespace, MODE_PRIVATE);
            return sp.getString(key, defVal);
        } catch (Exception e) {
            SLog.w(e);
        }
        return defVal;
    }


    public static void persistLong(Context context, String namespace, String key, long value) {
        try {
            SharedPreferences sp = context.getSharedPreferences(namespace, MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putLong(key, value);
            editor.apply();
        } catch (Exception e) {
            SLog.w(e);
        }
    }


    public static long getLong(Context context, String namespace, String key, long defVal) {
        try {
            SharedPreferences sp = context.getSharedPreferences(namespace, MODE_PRIVATE);
            return sp.getLong(key, defVal);
        } catch (Exception e) {
            SLog.w(e);
        }

        return defVal;
    }

    //判断某个key是否存在
    public static boolean isExist(Context context, String namespace, String key) {
        try {
            SharedPreferences sp = context.getSharedPreferences(namespace, MODE_PRIVATE);
            return sp.contains(key);
        } catch (Exception e) {
            return false;
        }
    }

    //删除
    public static void removeKey(Context context, String namespace, String key) {
        try {
            SharedPreferences sp = context.getSharedPreferences(namespace, MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.remove(key);
            editor.apply();
        } catch (Exception e) {
            SLog.w(e);
        }

    }

    public static boolean removeAll(Context context, String namespace) {
        try {
            SharedPreferences sp = context.getSharedPreferences(namespace, MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.clear();
            editor.apply();
            return true;
        } catch (Exception e) {
            SLog.w(e);
        }
        return false;
    }


    //获取key的集合
    public static Set<String> getAllKeys(Context context, String namespace) {
        try {
            Set<String> result = new HashSet<>();
            SharedPreferences sharedPreferences = context.getSharedPreferences(namespace, MODE_PRIVATE);
            Map<String, ?> all = sharedPreferences.getAll();
            if (all != null) {
                result = all.keySet();
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptySet();
    }


}
