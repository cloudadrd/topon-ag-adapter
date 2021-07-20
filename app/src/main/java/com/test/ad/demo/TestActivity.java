package com.test.ad.demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.anythink.custom.adapter.OAIDHandler;
import com.business.support.StrategyInfoListener;
import com.business.support.YMBusinessService;
import com.business.support.attract.PolicyData;
import com.business.support.compose.SIDListener;
import com.business.support.webview.CacheWebView;
import com.business.support.webview.InnerWebViewActivity;
import com.business.support.webview.InnerWebViewActivity2;
import com.business.support.webview.WebViewToNativeListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.thinkingdata.android.TDConfig;
import cn.thinkingdata.android.ThinkingAnalyticsSDK;

public class TestActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        findViewById(R.id.fingerFl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("tjt852_view", "click 我被点击了");
            }
        });
        jsonParse("{\n" +
                "    \"code\": 10000,\n" +
                "    \"data\": {\n" +
                "        \"status\": true,\n" +
                "        \"rv\": {\n" +
                "            \"0-50\": 20,\n" +
                "            \"151-200\": 20,\n" +
                "            \"201-250\": 20,\n" +
                "            \"251-300\": 20,\n" +
                "            \"301-350\": 20,\n" +
                "            \"351-400\": 20,\n" +
                "            \"401-500\": 20,\n" +
                "            \"501-3000\": 20,\n" +
                "            \"51-100\": 20\n" +
                "        },\n" +
                "        \"native\": {\n" +
                "            \"p\": 20\n" +
                "        },\n" +
                "        \"banner\": {\n" +
                "            \"1\": 50,\n" +
                "            \"2\": 50\n" +
                "        }\n" +
                "    },\n" +
                "    \"detail\": \"OK\",\n" +
                "    \"msg\": \"OK\"\n" +
                "}");
    }


    public void jsonParse(String json) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        JSONObject dataJson = jsonObject.optJSONObject("data");
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
        System.out.println("你好");
        System.out.println(policyData);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
