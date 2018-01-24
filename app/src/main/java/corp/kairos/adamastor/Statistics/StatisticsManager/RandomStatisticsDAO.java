package corp.kairos.adamastor.Statistics.StatisticsManager;

import android.app.usage.UsageStatsManager;

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
    public Set<AppDetails> getAppsStatistics(Map<String, AppDetails> allAppsDetails, Map<String, AppDetails> appsDetailsWithoutLauncher, UsageStatsManager usm) {
        Map<String, AppDetails> appStatsMap = new TreeMap<>();
        long min = TimeUnit.MINUTES.toMillis(0);
        long max = TimeUnit.MINUTES.toMillis(50);
        long randomTime = 0;

        for(AppDetails appDetails : allAppsDetails.values()) {
            randomTime = ThreadLocalRandom.current().nextLong(min, max);
            AppDetails app = appDetails.clone();
            app.setUsageStatistics(randomTime);
            appStatsMap.put(app.getPackageName(), app);
        }

        Set<AppDetails> resultSet = new TreeSet<>(new StatisticsAppDetailsComparator());
        resultSet.addAll(appStatsMap.values());
        return resultSet;
    }
}