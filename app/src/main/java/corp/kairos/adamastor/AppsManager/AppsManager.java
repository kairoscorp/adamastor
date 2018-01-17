package corp.kairos.adamastor.AppsManager;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import corp.kairos.adamastor.AppDetails;
import corp.kairos.adamastor.Collector.CollectorService;
import corp.kairos.adamastor.Statistics.StatisticsAppDetailsComparator;

public class AppsManager {
    private Map<String, AppDetails> allAppsDetails;
    private Map<String, AppDetails> appsDetailsWithoutLauncher;
    private static AppsManager instance;
    private BroadcastReceiver listener;
    protected Boolean force;

    private static final String TAG = AppsManager.class.getName();

    private AppsManager(){
        this.allAppsDetails = new TreeMap<>();
        this.appsDetailsWithoutLauncher = new TreeMap<>();
        this.force = true;
    }

    public static AppsManager getInstance() {
        if(instance == null) {
            instance = new AppsManager();
            new AppsBroadcastService();
        }
        return instance;
    }


    public Set<AppDetails> getAllApps(PackageManager packageManager, Boolean withLaunchers) {
        if(this.needToLoad()) {
            setupApps(packageManager);
        }

        if(withLaunchers) {
            return new TreeSet<>(this.allAppsDetails.values());
        } else {
            return new TreeSet<>(this.appsDetailsWithoutLauncher.values());
        }
    }

     public void setupApps(PackageManager packageManager){
        // Get all apps
        Intent intentApps = new Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> availableActivities = packageManager.queryIntentActivities(intentApps, 0);
        for(ResolveInfo ri:availableActivities){
            String label = ri.loadLabel(packageManager).toString();
            String name = ri.activityInfo.packageName;
            Drawable icon = ri.activityInfo.loadIcon(packageManager);
            AppDetails app = new AppDetails(label, name, icon);
            allAppsDetails.put(name, app);
            appsDetailsWithoutLauncher.put(name, app);
        }

        // Get list of installed launchers
        Intent intentLauncher = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> launchersInfo = packageManager.queryIntentActivities(intentLauncher, 0);
        for(ResolveInfo info: launchersInfo) {
            appsDetailsWithoutLauncher.remove(info.activityInfo.packageName);
        }

        this.force = false;
    }

    public Set<AppDetails> getAppsStatistics(PackageManager packageManager, UsageStatsManager usm) {
        if(this.needToLoad()) {
            setupApps(packageManager);
        }

        Map<String, AppDetails> appStatsMap = new TreeMap<>();
        long time = System.currentTimeMillis();
        List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,  time - 10000000, time);
        if (appList != null && appList.size() > 0) {
            for (UsageStats usageStats : appList) {
                String appName = usageStats.getPackageName();
                Long appTotalTime = usageStats.getTotalTimeInForeground();

                // Get the app from all apps
                if(this.allAppsDetails.containsKey(appName)) {
                    AppDetails appDetails = this.allAppsDetails.get(appName);
                    appDetails.setUsageStatistics(appTotalTime);
                    if(appTotalTime > 0 && appsDetailsWithoutLauncher.containsKey(appName)) {
                        if(appStatsMap.containsKey(appName)) {
                            appDetails.setUsageStatistics(appTotalTime + appStatsMap.get(appName).getUsageStatistics());
                        }
                        appStatsMap.put(appName, appDetails);

                        // Update the all apps map to use as cache
                        allAppsDetails.put(appName, appDetails);
                    }
                }
            }
        }

        Set<AppDetails> resultSet = new TreeSet<>(new StatisticsAppDetailsComparator());
        resultSet.addAll(appStatsMap.values());
        return resultSet;
    }

    public Map<String, Long> getContextStatistics() {
         return CollectorService.getContextStatistics();
    }

    public AppDetails getAppDetails(PackageManager packageManager, String packageName) {
        if(this.needToLoad()) {
            setupApps(packageManager);
        }

        return allAppsDetails.get(packageName);
    }

    private boolean needToLoad(){
        return this.allAppsDetails.size() == 0 || this.force;
    }
}
