package corp.kairos.adamastor.Statistics.StatisticsManager;

import android.app.usage.UsageStatsManager;
import android.util.Log;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import corp.kairos.adamastor.AppDetails;
import corp.kairos.adamastor.Settings.Settings;
import corp.kairos.adamastor.Statistics.StatisticsAppDetailsComparator;

public class RandomStatisticsDAO implements StatisticsDAO {
    public RandomStatisticsDAO() {
        //
    }

    @Override
    public Map<String, Long> getContextStatistics() {
        Map<String, Long> stats = new TreeMap<>();
        long min = TimeUnit.MINUTES.toMillis(15);
        long max = TimeUnit.MINUTES.toMillis(360);
        long randomTime = 0;
        String[] contexts = Settings.contextsNames;

        for(String context : contexts) {
            randomTime =  ThreadLocalRandom.current().nextLong(min, max);
            stats.put(context, randomTime);
        }

        return stats;
    }

    @Override
    public Collection<AppDetails> getAppsStatistics(Map<String, AppDetails> allAppsDetails, Map<String, AppDetails> appsDetailsWithoutLauncher, UsageStatsManager usm, boolean withLaunchers) {
        Map<String, AppDetails> appStatsMap = new TreeMap<>();
        long minLastUsed = TimeUnit.MINUTES.toMillis(0);
        long maxLastUsed = TimeUnit.MINUTES.toMillis(2);
        long minTotalUsed = TimeUnit.MINUTES.toMillis(0);
        long maxTotalUsed = TimeUnit.MINUTES.toMillis(50);
        Map<String, AppDetails> searchApps = withLaunchers ? allAppsDetails : appsDetailsWithoutLauncher;
        for(AppDetails appDetails : searchApps.values()) {
            long lastUsedTime = ThreadLocalRandom.current().nextLong(minLastUsed, maxLastUsed);
            long totalUsedTime = ThreadLocalRandom.current().nextLong(minTotalUsed, maxTotalUsed);
            AppDetails app = appDetails.clone();
            app.setLastUsedTime(lastUsedTime);
            app.setTotalUsedTime(totalUsedTime);
            appStatsMap.put(app.getPackageName(), app);
        }

        Set<AppDetails> resultSet = new TreeSet<>(new StatisticsAppDetailsComparator());
        resultSet.addAll(appStatsMap.values());
        return resultSet;
    }
}
