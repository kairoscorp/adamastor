package corp.kairos.adamastor.Statistics.StatisticsManager;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import corp.kairos.adamastor.AppDetails;
import corp.kairos.adamastor.Collector.CollectorService;
import corp.kairos.adamastor.Statistics.StatisticsAppDetailsComparator;

public class RealStatisticsDAO implements StatisticsDAO {
    private CollectorService collectorService = CollectorService.getInstance();
    public RealStatisticsDAO() {
        //
    }

    @Override
    public Map<String, Long> getContextStatistics() {
        return collectorService.getContextStatistics();
    }

    @Override
    public int getAppTime(AppDetails app) {
        return collectorService.getAppTime(app);
    }

    @Override
    public String lastTimeApp(AppDetails app) {
        return collectorService.lastTimeApp(app);
    }


    @Override
    public Map<String, AppDetails> getAppsStatistics(Map<String, AppDetails> allAppsDetails, UsageStatsManager usm) {
        Map<String, AppDetails> appStatsMap = new TreeMap<>();
        long time = System.currentTimeMillis();
        List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,  time - 10000000, time);
        if (appList != null && appList.size() > 0) {
            for (UsageStats usageStats : appList) {
                String appName = usageStats.getPackageName();
                Long appTotalTime = usageStats.getTotalTimeInForeground();

                // Get the app from all apps
                if(allAppsDetails.containsKey(appName)) {
                    AppDetails appDetails = allAppsDetails.get(appName);
                    appDetails.setUsageStatistics(appTotalTime);
                    if(appTotalTime > 0) {
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

        return appStatsMap;
    }

    public TreeSet<AppDetails> getContextAppsStatistics(Map<String, AppDetails> allAppsDetails, String context) {
        TreeSet<AppDetails> result = new TreeSet<>(new StatisticsAppDetailsComparator());
        Map<String, Long> collectorResult = collectorService.getContextAppsStatistics(context);
        for(Map.Entry<String, Long> entry : collectorResult.entrySet()) {
            AppDetails app = allAppsDetails.get(entry.getKey());
            if(app != null) {
                app.setUsageStatistics(entry.getValue());
                result.add(app.clone());
            }
        }

        return result;
    }
}
