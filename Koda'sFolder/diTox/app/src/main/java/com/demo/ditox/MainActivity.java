package com.demo.ditox;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS = 1;

    public LinearRegression linearRegression = new LinearRegression();

    public HashMap<String, Double> getRecommendations(){
        Map<String, List<Double>> features = callThisSkynet();
        Map<String, double[]> data = new HashMap<>();
        for (Map.Entry<String, List<Double>> entry : features.entrySet()) {
            String key = entry.getKey();
            List<Double> value = entry.getValue();
            double[] valueArray = new double[value.size()];
            for (int i = 0; i < value.size(); i++) {
                valueArray[i] = value.get(i);
            }
            data.put(key, valueArray);
        }
        this.linearRegression.train(data);
        double[] betas = this.linearRegression.getBeta();
        HashMap<Integer, String> featureMap = this.linearRegression.getFeatureMap();
        HashMap<String, Double> coeffs = new HashMap<>();
        for(int i = 0; i < betas.length; i++){
            coeffs.put(featureMap.get(i), betas[i]);
        }
        return coeffs;
    }

    public static Map<String, List<Double>> callThisSkynet() {
        List<Double> fakeWellbeingData = Arrays.asList(75.0, 32.0, 100.0, 40.0, 56.0, 42.0, 10.0);
        Map<String, List<Double>> returnMap = new HashMap<>();
        returnMap.put("Wellbeing", fakeWellbeingData);
        List<String> topTenAppName = new ArrayList<>(UsageDataManager.getNLargestAndOthers(UsageDataManager.getUsageDataForThisWeek(AppContext.getAppContext()), 10).keySet());


        for (int i = 0; i < 10; i++) {
            returnMap.put(topTenAppName.get(i), new ArrayList<>());
        }
        //a h??t napon megy v??gig
        for (int i = 1; i < 8; i++) {
            //a 10 top appot n??zi v??gig
            for (int j = 0; j < 10; j++) {
                Map<String, Long> localMap = UsageDataManager.getUsageDataMap(i, i, AppContext.getAppContext());

                if (localMap.containsKey(topTenAppName.get(j)))
                {
                    try {
                    returnMap.get(topTenAppName.get(j)).add((double) localMap.get(topTenAppName.get(j)).intValue());
                    }
                    catch(Exception e) {
                        Log.i("catch","caught");
                    }
                }
                else
                {
                    try {
                        returnMap.get(topTenAppName.get(j)).add(0.0);
                    }
                    catch(Exception e) {
                        Log.i("catch","caught");
                    }

                }

            }
        }

        return returnMap;
    }

    //check if permission to see UsageData has been given
    private boolean permissionGiven()
    {
        AppOpsManager appOps = (AppOpsManager)
                getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getPackageName());

        return mode == AppOpsManager.MODE_ALLOWED;
    }

    //refreshes app usage data on screen
    public void refreshShowedWeeklyData(int numberOfApps)
    {
        WebView webView = findViewById(R.id.webView);
        ArrayList<Long> valueList = new ArrayList<>(UsageDataManager.getNLargestAndOthers(UsageDataManager.getUsageDataForThisWeek(this), numberOfApps).values());
        ArrayList<String> keyList = new ArrayList<>(UsageDataManager.getNLargestAndOthers(UsageDataManager.getUsageDataForThisWeek(this), numberOfApps).keySet());
        String textToPass = "javascript:appDataUpdater([";
        for (int i = 0; i < numberOfApps-1; i++)
        {
            textToPass += valueList.get(i) + ",";
        }
        textToPass += valueList.get(numberOfApps - 1) + "], [\"";
        for (int i = 0; i < numberOfApps - 1; i++)
        {
            textToPass += keyList.get(i) + "\",\"";
        }
        textToPass += keyList.get(numberOfApps - 1) + "\"])";
        webView.loadUrl(textToPass);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toast toast = Toast.makeText(AppContext.getAppContext(), "Loading...", Toast.LENGTH_SHORT);
        toast.show();

        requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,READ_EXTERNAL_STORAGE}, 1);

        if (!permissionGiven())
        {
            Toast.makeText(this, "Please give permission to Usage access in order to see app usage data.", Toast.LENGTH_LONG).show();
            startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), 
                    MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
        }

        webSetUp();

        TextView textView = findViewById(R.id.textView);
        textView.setText(callThisSkynet().toString());

    }

    private void webSetUp() {
        WebView webView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCachePath(getApplicationContext().getFilesDir().getAbsolutePath() + "/cache");
        webView.loadUrl("file:///android_asset/index.html");
        webView.addJavascriptInterface(new WebInterface(this), "Android");
        webView.setWebViewClient(new ditoxWebClient());
    }

    @Override
    protected void onStart() {
        super.onStart();

        //not in use for now. Might be useful later
    }
}