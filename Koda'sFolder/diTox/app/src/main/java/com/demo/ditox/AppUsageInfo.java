package com.demo.ditox;

import android.graphics.drawable.Drawable;

//THIS CLASS IS NOT IN USE, MIGHT BE USEFUL LATER THOUGH

class AppUsageInfo {
    Drawable appIcon; // You may add get this usage data also, if you wish.
    String appName, packageName;
    long timeInForeground;
    int launchCount;

    AppUsageInfo(String pName) {
        this.packageName=pName;
    }
}
