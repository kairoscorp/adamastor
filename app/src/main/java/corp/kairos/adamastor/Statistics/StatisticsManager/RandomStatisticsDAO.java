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
    public int getAppTime(AppDetails app) {
        return 0;
    }

    @Override
    public String lastTimeApp(AppDetails app) {
        return "fake";
    }

    @Override
    public Map<String, AppDetails> getAppsStatistics(Map<String, AppDetails> allAppsDetails, UsageStatsManager usm) {
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
        return appStatsMap;
    }

    @Override
    public Set<AppDetails> getContextAppsStatistics(Map<String, AppDetails> allAppsDetails, String context) {
        Set<AppDetails> stats = new TreeSet<>(new StatisticsAppDetailsComparator());
        long min = TimeUnit.MINUTES.toMillis(15);
        long max = TimeUnit.MINUTES.toMillis(360);
        long randomTime = 0;

        for(Map.Entry<String, AppDetails> entry : allAppsDetails.entrySet()) {
            AppDetails app = entry.getValue();
            randomTime =  ThreadLocalRandom.current().nextLong(min, max);
            app.setUsageStatistics(randomTime);
            stats.add(app);
        }

        return stats;
    }
}
