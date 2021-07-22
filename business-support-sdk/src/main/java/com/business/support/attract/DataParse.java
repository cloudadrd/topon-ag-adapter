package com.business.support.attract;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class DataParse {

    public static PolicyData policyData;

    public static void jsonParse(JSONObject dataJson) {
        PolicyData policyData = new PolicyData();
        if (dataJson == null) return;
        JSONObject rvJson = dataJson.optJSONObject("rv");
        if (rvJson != null) {
            Iterator<String> rvKeys = rvJson.keys();
            while (rvKeys.hasNext()) {
                String key = rvKeys.next();
                String[] values = key.split("-");
                if (values.length == 2) {
                    PolicyData.RV rv = new PolicyData.RV();
                    rv.startRange = Integer.parseInt(values[0]);
                    rv.endRange = Integer.parseInt(values[1]);
                    rv.chance = rvJson.optInt(key);
                    policyData.rvs.add(rv);
                }
            }
        }

        JSONObject bannerJson = dataJson.optJSONObject("banner");
        if (bannerJson != null) {
            Iterator<String> bannerKeys = bannerJson.keys();
            while (bannerKeys.hasNext()) {
                String key = bannerKeys.next();
                PolicyData.BannerStyleType styleType = PolicyData.BannerStyleType.get(Integer.parseInt(key));
                if (styleType == null) continue;
                PolicyData.Banner banner = new PolicyData.Banner();
                banner.styleType = styleType;
                banner.chance = bannerJson.optInt(key);
                policyData.banners.add(banner);
            }
        }

        JSONObject nativeJson = dataJson.optJSONObject("native");
        if (nativeJson != null) {
            policyData.nativeChance = nativeJson.optInt("p");
        }
        DataParse.policyData = policyData;

    }
}
