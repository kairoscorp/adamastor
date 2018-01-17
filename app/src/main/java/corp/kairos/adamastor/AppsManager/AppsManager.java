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

import corp.kairos.adamastor.AppDetail;
import corp.kairos.adamastor.Statistics.AppDetailStats;

public class AppsManager {
    private Map<String, AppDetail> allAppsDetails;
    private Map<String, AppDetail> appsDetailsWithoutLauncher;
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


    public Set<AppDetail> getAllApps(PackageManager packageManager, Boolean withLaunchers) {
        if(this.isSetup()) {
            setupApps(packageManager);
        }

        if(withLaunchers) {
            return new TreeSet<>(this.allAppsDetails.values());
        } else {
            return new TreeSet<>(this.appsDetailsWithoutLauncher.values());
        }
    }

     private void setupApps(PackageManager packageManager){
        // Get all apps
        Intent intentApps = new Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> availableActivities = packageManager.queryIntentActivities(intentApps, 0);
        for(ResolveInfo ri:availableActivities){
            String label = ri.loadLabel(packageManager).toString();
            String name = ri.activityInfo.packageName;
            Drawable icon = ri.activityInfo.loadIcon(packageManager);
            AppDetail app = new AppDetail(label, name, icon);
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

    public Set<AppDetailStats> getAppsStatistics(PackageManager packageManager, UsageStatsManager usm) {
        if(this.isSetup()) {
            setupApps(packageManager);
        }

        Map<String, AppDetailStats> appsStats = new TreeMap<>();
        long time = System.currentTimeMillis();
        List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,  time - 10000000, time);
        Log.i(TAG, "AllApps = " + this.allAppsDetails.size());
        Log.i(TAG, "NotLauncher = " + this.appsDetailsWithoutLauncher.size());
        if (appList != null && appList.size() > 0) {
            for (UsageStats usageStats : appList) {
                try {
                    ApplicationInfo appInfo = packageManager.getApplicationInfo(usageStats.getPackageName(), PackageManager.GET_META_DATA);
                    String appName = usageStats.getPackageName();
                    String appLabel = packageManager.getApplicationLabel(appInfo).toString();
                    Drawable appIcon = packageManager.getApplicationIcon(appInfo);
                    Long appTotalTime = usageStats.getTotalTimeInForeground();
                    AppDetailStats appDetailStats = new AppDetailStats(appLabel, appName, appIcon, appTotalTime);
                    if(appTotalTime > 0 && appsDetailsWithoutLauncher.containsKey(appName)) {
                        if(appsStats.containsKey(appName)) {
                            appDetailStats.setTotalTime(appTotalTime + appsStats.get(appName).getTotalTime());
                        }
                        appsStats.put(appName, appDetailStats);
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    // This should never happen
                    Log.e(TAG, "Unknown package name");
                }
            }
        }
        return new TreeSet<>(appsStats.values());
    }

    private boolean isSetup(){
        return this.allAppsDetails.size() == 0 || this.force;
    }
}
