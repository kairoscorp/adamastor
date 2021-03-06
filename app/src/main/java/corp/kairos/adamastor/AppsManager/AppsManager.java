package corp.kairos.adamastor.AppsManager;

import android.app.usage.UsageStatsManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import corp.kairos.adamastor.AppDetails;
import corp.kairos.adamastor.Statistics.StatisticsManager.RandomStatisticsDAO;
import corp.kairos.adamastor.Statistics.StatisticsManager.RealStatisticsDAO;
import corp.kairos.adamastor.Statistics.StatisticsManager.StatisticsDAO;
import corp.kairos.adamastor.Statistics.StatisticsManager.RandomStatisticsDAO;
import corp.kairos.adamastor.Util;
import corp.kairos.adamastor.UserContext;

public class AppsManager {
    private Map<String, AppDetails> allAppsDetails;
    private Map<String, AppDetails> appsDetailsWithoutLauncher;
    private StatisticsDAO statisticsManager;

    private static AppsManager instance;
    protected Boolean force;

    private static final String TAG = AppsManager.class.getName();

    private AppsManager(){
        this.statisticsManager = new RealStatisticsDAO();
        this.allAppsDetails = new TreeMap<>();
        this.appsDetailsWithoutLauncher = new TreeMap<>();
        this.force = true;
    }

    public boolean switchStatisticsManager (){
        if(this.statisticsManager instanceof RandomStatisticsDAO) {
            this.statisticsManager = new RealStatisticsDAO();
            return true;
        } else {
            this.statisticsManager = new RandomStatisticsDAO();
            return false;
        }
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
            boolean system = !Util.isUserApp(ri.activityInfo.applicationInfo);
            AppDetails app = new AppDetails(label, name, icon,system);
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
    
    public Map<String, AppDetails> getAppsStatistics(PackageManager packageManager, UsageStatsManager usm, Boolean filtered) {
        if(this.needToLoad()) {
            setupApps(packageManager);
        }
        Map<String, AppDetails> statistics = statisticsManager.getAppsStatistics(this.appsDetailsWithoutLauncher, usm);
        Map<String, AppDetails> result = new TreeMap<>();
        if(filtered) {
            for(Map.Entry<String, AppDetails> app : statistics.entrySet()) {
                if(this.appsDetailsWithoutLauncher.containsKey(app.getKey())) {
                    result.put(app.getKey(), app.getValue());
                }
            }
            return result;
        }
        return statistics;
    }

    public Map<String, TreeSet<AppDetails>> getAppsStatisticsByContext(List<String> contextsNames, PackageManager packageManager) {
        if(this.needToLoad()) {
            setupApps(packageManager);
        }
        Map<String, TreeSet<AppDetails>> result = new TreeMap<>();
        for(String contextName : contextsNames) {
            TreeSet<AppDetails> statistics = statisticsManager.getContextAppsStatistics(this.appsDetailsWithoutLauncher, contextName);
            result.put(contextName, statistics);
        }
        return result;
    }

    public AppDetails getAppStatisticsDetails(String packageName, UserContext userContext, PackageManager packageManager, UsageStatsManager usm) {
        Map<String, AppDetails> statistics = this.getAppsStatistics(packageManager, usm, false);
        if(statistics.containsKey(packageName)) {
            return statistics.get(packageName);
        } else {
            AppDetails app = this.allAppsDetails.get(packageName);
            app.setUsageStatistics(0L);
            return app;
        }
    }


    public Map<String, Long> getContextStatistics() {
         return statisticsManager.getContextStatistics();
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
