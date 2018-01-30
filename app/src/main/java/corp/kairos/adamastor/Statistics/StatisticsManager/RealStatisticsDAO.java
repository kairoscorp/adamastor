package corp.kairos.adamastor.Statistics.StatisticsManager;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import corp.kairos.adamastor.AppDetails;
import corp.kairos.adamastor.Collector.CollectorService;

public class RealStatisticsDAO implements StatisticsDAO {
    private CollectorService collectorService = CollectorService.getInstance();
    public RealStatisticsDAO() {
        //
    }

    @Override
    public Map<String, Long> getContextStatistics() {
        return collectorService.getContextStatistics();
    }

    public Collection<AppDetails> getAppsStatistics(Map<String, AppDetails> allAppsDetails, Map<String, AppDetails> appsDetailsWithoutLauncher, UsageStatsManager usm, boolean withLaunchers) {
        Map<String, AppDetails> appStatsMap = new TreeMap<>();
        long time = System.currentTimeMillis();
        List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,  time - 10000000, time);
        if (appList != null && appList.size() > 0) {
            for (UsageStats usageStats : appList) {
                String appName = usageStats.getPackageName();
                Long appTotalTime = usageStats.getTotalTimeInForeground();
                Long appLastTime = usageStats.getLastTimeUsed();

                // Get the app from all apps
                if(allAppsDetails.containsKey(appName)) {
                    AppDetails appDetails = allAppsDetails.get(appName);
                    appDetails.setTotalUsedTime(appTotalTime);
                    appDetails.setLastUsedTime(appLastTime);
                    if(appTotalTime > 0 && (! withLaunchers && appsDetailsWithoutLauncher.containsKey(appName))) {
                        if(appStatsMap.containsKey(appName)) {
                            appDetails.setTotalUsedTime(appTotalTime + appStatsMap.get(appName).getTotalUsedTime());
                            appDetails.setLastUsedTime(appLastTime);
                        }
                        appStatsMap.put(appName, appDetails);

                        // Update the all apps map to use as cache
                        allAppsDetails.put(appName, appDetails);
                    }
                }
            }
        }

        return appStatsMap.values();
    }
}
