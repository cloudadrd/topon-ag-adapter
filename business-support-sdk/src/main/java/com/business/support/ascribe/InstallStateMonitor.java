package com.business.support.ascribe;

import android.content.Context;

public class InstallStateMonitor {


    public static void register(Context context, InstallListener installListener) {

        InstallStateReceiver.registerReceiver(context, installListener);

    }
}
