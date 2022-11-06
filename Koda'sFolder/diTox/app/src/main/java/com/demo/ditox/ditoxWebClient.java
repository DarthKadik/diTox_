package com.demo.ditox;

import android.app.usage.UsageStats;
import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ditoxWebClient extends WebViewClient {
    JSONArray jerryTheJellyfish = new JSONArray();

    //called when index.html finished loading
    @Override
    public void onPageCommitVisible(WebView webView, String url) {
        if (url.contains("index.html")) {
            refresh(webView);
        }
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        if (url.contains("apps")) {
            refresh2(view);
           // Toast toast = Toast.makeText(AppContext.getAppContext(), "Loading...", Toast.LENGTH_SHORT);
            // toast.show();
        }
    }

    public static void refresh2(WebView webView) {
        int numberOfApps = 5;
        /* JSONArray returnArray = new JSONArray();
        List<String> topTenAppName = new ArrayList<>(UsageDataManager.getNLargestAndOthers(UsageDataManager.getUsageDataForThisWeek(AppContext.getAppContext()), 10).keySet());
        for (int i = 0; i < topTenAppName.size(); i++) {
            JSONObject jsonObj = new JSONObject();
            try {
                returnArray.put(new JSONObject(topTenAppName.get(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return returnArray;*/
        Map<String, Long> map = UsageDataManager.getNLargestAndOthers(UsageDataManager.getUsageDataForThisWeek(AppContext.getAppContext()), numberOfApps + 4);
        map.remove("Others");
        map.remove("Wolt Partner");
        map.remove("Maps");
        Map<String, List<Double>> Skynet = MainActivity.callThisSkynet();
        Skynet.remove("Others");
        Skynet.remove("Wolt Partner");
        Skynet.remove("Maps");
        Skynet.remove("Revolut");
        Random rand = new Random();
        List<List<Double>> topAppUsageTimes = new ArrayList<>(Skynet.values());
        List<String> topTenAppName = new ArrayList<>(map.keySet());
        StringBuilder textToPass = new StringBuilder("javascript:setData([\"");
        for (int i = 0; i < numberOfApps - 1; i++) {
            textToPass.append(topTenAppName.get(i)).append("\",\"");
        }
        textToPass.append(topTenAppName.get(numberOfApps - 1)).append("\"],[");
        for (int i = 0; i < numberOfApps; i++) {
            textToPass.append("[");
                for (int j = 0; j < 7; j++) {
                    textToPass.append(topAppUsageTimes.get(i).get(j).intValue()).append(",");
                }
                textToPass.append(topAppUsageTimes.get(i).get(numberOfApps - 1).intValue()).append("],");
        }
        textToPass.deleteCharAt(textToPass.length() - 1).append("])");
        //textToPass.append(topAppUsageTimes.get(numberOfApps - 1).get(numberOfApps - 1)).append("]]");;
        webView.loadUrl(textToPass.toString());
    }


    //refreshes app usage data on screen with 5 apps by default
    public static void refresh(WebView webView) {
        int numberOfApps = 10;
        Log.i("Ez ewgupsaegopsdg: ", UsageDataManager.getUsageDataForThisWeek(webView.getContext()).toString());
        Map<String, Long> map2 = UsageDataManager.getNLargestAndOthers(UsageDataManager.getUsageDataForThisWeek(webView.getContext()), numberOfApps +1);
        map2.remove("Others");
        ArrayList<Long> valueList = new ArrayList<>(map2.values());
        ArrayList<String> keyList = new ArrayList<>(map2.keySet());
        StringBuilder textToPass = new StringBuilder("javascript:appDataUpdater([");
        for (int i = 0; i < numberOfApps; i++) {
            textToPass.append(valueList.get(i)).append(",");
        }
        textToPass.append(valueList.get(numberOfApps)).append("], [\"");
        for (int i = 0; i < numberOfApps; i++) {
            textToPass.append(keyList.get(i)).append("\",\"");
        }
        textToPass.append(keyList.get(numberOfApps)).append("\"])");
        webView.loadUrl(textToPass.toString());
    }

   // @JavascriptInterface
   // public void getNames() {

    //}
}
