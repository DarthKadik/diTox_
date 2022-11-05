package com.demo.ditox;

import static java.util.stream.Collectors.toCollection;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

//This class manages the user's phone usage data
public class UsageDataManager {
    private static Drawable icon;
    private static long hourMil = 1000*60*60*25 + 24*60*1000; // In Milliseconds
    private static long endTime = System.currentTimeMillis();
    private static long startTime = endTime - hourMil;

    public static Map<String, Long> getUsageDataForThisWeek(Context context)
    {

        List<UsageStats> appDataList = getInfo(getUsageDataOG(6, 1, context));
        List<String> appNameList = getDescription(appDataList, context);
        Map<String, Long> usageDataMap= new HashMap<>();

        for (int i = appDataList.size(); i-- > 0;) {
            if (!usageDataMap.containsKey(appNameList.get(i)))
            {
                usageDataMap.put(appNameList.get(i), appDataList.get(i).getTotalTimeInForeground() / 60000);
            }
            else
            {
                usageDataMap.replace(appNameList.get(i), usageDataMap.get(appNameList.get(i)) + appDataList.get(i).getTotalTimeInForeground() / 60000);
            }
        }
        return usageDataMap;
    }

    public static Map<String, Long> getUsageDataMap(int startDayInPast, int endDayInPast, Context context){
    List< UsageStats > appDataList = getInfo(getUsageDataOG(startDayInPast, endDayInPast, context));
    List<String> appNameList = getDescription(appDataList, context);
    Map<String, Long> usageDataMap= new HashMap<>();

        for (int i = appDataList.size(); i-- > 0;) {
        if (!usageDataMap.containsKey(appNameList.get(i)))
        {
            usageDataMap.put(appNameList.get(i), appDataList.get(i).getTotalTimeInForeground() / 60000);
        }
        else
        {
            usageDataMap.replace(appNameList.get(i), usageDataMap.get(appNameList.get(i)) + appDataList.get(i).getTotalTimeInForeground() / 60000);
        }
    }
        return usageDataMap;
    }

    public static Map<String, Long> getNLargestAndOthers(Map<String, Long> map, int n)
    {
        Log.i("a térkép: ", map.toString());
        Map<String, Long> returnMap = new HashMap<>();
        List<String> keyList = new ArrayList<>(map.keySet());
        ArrayList<Long> valueList = new ArrayList<>(map.values());
        String currentKey = "";
        Long currentValue = 0L;
        for (int i = 0; i < n; i++) {
            int maxIndex = 0;

            for (int j = 0; j < keyList.size(); j++) {
                currentValue = valueList.get(maxIndex);
                if (valueList.get(j) >= currentValue) {
                    maxIndex = j;
                    currentKey = keyList.get(maxIndex);
                    currentValue = valueList.get(maxIndex);
                }
            }

            keyList.remove(maxIndex);
            valueList.remove(maxIndex);
            returnMap.put(currentKey, currentValue);
        }

        Log.i("a térkép: ", returnMap.toString());
        Long otherSum = 0L;
        for(Long l : valueList)
            otherSum += l;
        returnMap.put("Others",otherSum);
        return returnMap;
    }

    //gets app usage data returns a list of usage stats
    // startDayInPast is a natural numbers which gives the first day in interest in the past (eg. if you
    // want to have last week's data put 8 as startDayInPast and 1 as endDayInPast (yesterday))
    public static List<UsageStats> getUsageDataOG(int startDayInPast, int endDayInPast, Context context)
    {
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);

        Calendar startCal = getMidnight(Calendar.getInstance(), true);
        startCal.add(Calendar.DATE, - startDayInPast);
        long start = startCal.getTimeInMillis();

        Calendar endCal = getMidnight(Calendar.getInstance(), false);
        endCal.add(Calendar.DATE, - endDayInPast);
        long end = Calendar.getInstance().getTimeInMillis();

        List<UsageStats> stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start, end);

        List<UsageStats> returnList = new ArrayList<>();
        for (int i = stats.size(); i-- > 0;) {
            if (!returnList.contains(stats.get(i))) {
                returnList.add(stats.get(i));
            } else {
                i = 0;
            }
        }

        return returnList;
    }

    public static List<UsageStats> getInfo(List<UsageStats> list) {
                list.removeIf(n-> (n.getTotalTimeInForeground() < 60000 || n.getPackageName().contains("clock")) || n.getPackageName().contains("launcher"));
        return list;
    }

    public static List<String> getDescription (List<UsageStats> list, Context context)
    {
        List<String> returnList = new ArrayList<>();
        for(UsageStats us: list)
        {
            final PackageManager packageManager = context.getApplicationContext().getPackageManager();
            ApplicationInfo appInfo;
            try
            {
                appInfo = packageManager.getApplicationInfo(us.getPackageName(), 0);

            }
            catch (final PackageManager.NameNotFoundException e)
            {
                appInfo = null;
            }
            final String appName = (String) (appInfo != null ? packageManager.getApplicationLabel(appInfo) : "unknown");
            if (appInfo != null)
            {
                icon = packageManager.getApplicationLogo(appInfo);
            }
            returnList.add(appName);
        }
        return returnList;
    }

    //changes calendar's time to midnight of the day. Either in the morning (0:0:1) or night (23:59:59)
    private static Calendar getMidnight(Calendar calendar, Boolean morning)
    {
        if (morning) {
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 1);
            calendar.set(Calendar.MILLISECOND, 0);
        }
        else {
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 0);
        }

        return calendar;
    }
}
