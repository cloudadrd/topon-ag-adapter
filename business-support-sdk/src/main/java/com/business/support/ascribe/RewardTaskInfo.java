package com.business.support.ascribe;

import android.content.pm.PackageInfo;

import com.business.support.adinfo.BSAdType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    public static Map<String, Map<String, BSAdType>> revealAdPackages = new HashMap<>();

    public static Map<String, BSAdType> adPackages = new HashMap<>();

    public static RewardTaskInfo currentStartPkg = null;

    public String currentInstallPkg;

    public BSAdType bsAdType;

    public String sceneId;

    //0安装，1打开
    public int infoState;

    public long startTaskAppTime;

    public static boolean isExistsForPkg(String packageName) {
        Collection<Map<String, BSAdType>> list = revealAdPackages.values();
        for (Map<String, BSAdType> map : list) {
            if (map.get(packageName) != null) {
                return true;
            }
        }
        return false;
    }

    public static List<RewardTaskInfo> getRewardTasksForPkg(String packageName) {
        List<RewardTaskInfo> list = new ArrayList<>();
        Set<String> keys = revealAdPackages.keySet();
        for (String key : keys) {
            if (revealAdPackages.get(key).get(packageName) != null) {
                list.add(new RewardTaskInfo(packageName, revealAdPackages.get(key).get(packageName), key, 0, 0));
            }
        }
        return list;
    }

    public static void putRevelPackage(String sceneId, String packageName, BSAdType bsAdType) {
        if (revealAdPackages.get(sceneId) != null) {
            revealAdPackages.get(sceneId).put(packageName, bsAdType);
        } else {
            Map<String, BSAdType> map = new HashMap<>();
            map.put(packageName, bsAdType);
            revealAdPackages.put(sceneId, map);
        }
    }

    public RewardTaskInfo(String currentInstallPkg, BSAdType bsAdType, int infoState, long startTaskAppTime) {
        this.currentInstallPkg = currentInstallPkg;
        this.bsAdType = bsAdType;
        this.infoState = infoState;
        this.startTaskAppTime = startTaskAppTime;
    }

    public RewardTaskInfo(String currentInstallPkg, BSAdType bsAdType, String sceneId, int infoState, long startTaskAppTime) {
        this.currentInstallPkg = currentInstallPkg;
        this.bsAdType = bsAdType;
        this.sceneId = sceneId;
        this.infoState = infoState;
        this.startTaskAppTime = startTaskAppTime;
    }
}
