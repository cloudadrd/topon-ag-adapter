package com.business.support.ascribe;

import android.content.pm.PackageInfo;

import com.business.support.adinfo.BSAdType;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RewardTaskInfo {


//    public static class PackageInfo {
//
//        public String packageName;
//
//        public BSAdType bsAdType;
//
//        public PackageInfo(String packageName, BSAdType bsAdType) {
//            this.packageName = packageName;
//            this.bsAdType = bsAdType;
//        }
//    }

    public static Map<String, BSAdType> revealAdPackages = new HashMap<>();

    public static String currentInstallPkg = null;
}
