package com.zcoup.multidownload.util;

import android.util.Log;

public class Logger {
	
	public static boolean isDebug = false;
	public static String TAG = "download";
	
	public static void log(String tag, String msg){
		if(isDebug){
			Log.i(tag+"", msg+"");
		}
	}
	
	public static void log(String msg){
		if(isDebug){
			Log.i(TAG, msg+"");
		}
	}

}
