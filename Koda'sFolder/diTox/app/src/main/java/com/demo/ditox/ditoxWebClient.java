package com.demo.ditox;

import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONArray;

import java.util.ArrayList;

public class ditoxWebClient extends WebViewClient {
    JSONArray jerryTheJellyfish = new JSONArray();

    //called when index.html finished loading
    @Override
    public void onPageFinished(WebView webView, String url) {
        refresh(webView);
    }

    //refreshes app usage data on screen with 5 apps by default
    public static void refresh(WebView webView) {
        int numberOfApps = 10;
        Log.i("Ez ewgupsaegopsdg: ", UsageDataManager.getUsageDataForThisWeek(webView.getContext()).toString());
        ArrayList<Long> valueList = new ArrayList<>(UsageDataManager.getNLargestAndOthers(UsageDataManager.getUsageDataForThisWeek(webView.getContext()), numberOfApps).values());
        ArrayList<String> keyList = new ArrayList<>(UsageDataManager.getNLargestAndOthers(UsageDataManager.getUsageDataForThisWeek(webView.getContext()), numberOfApps).keySet());
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

    @JavascriptInterface
    public void getNames() {
        
    }
}
