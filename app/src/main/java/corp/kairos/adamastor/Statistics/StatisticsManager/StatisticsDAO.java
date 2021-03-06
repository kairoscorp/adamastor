package corp.kairos.adamastor.Statistics.StatisticsManager;

import android.app.usage.UsageStatsManager;

import java.util.Map;
import java.util.TreeSet;

import corp.kairos.adamastor.AppDetails;

public interface StatisticsDAO {
    Map<String, Long> getContextStatistics();

    int getAppTime(AppDetails app);
    String lastTimeApp(AppDetails app);
    
    Map<String, AppDetails> getAppsStatistics(Map<String, AppDetails> allAppsDetails, UsageStatsManager usm);

    TreeSet<AppDetails> getContextAppsStatistics(Map<String, AppDetails> allAppsDetails, String context);
}
