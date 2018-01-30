package corp.kairos.adamastor.Statistics.StatisticsManager;

import android.app.usage.UsageStatsManager;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import corp.kairos.adamastor.AppDetails;

public interface StatisticsDAO {
    Map<String, Long> getContextStatistics();
    Collection<AppDetails> getAppsStatistics(Map<String, AppDetails> allAppsDetails, Map<String, AppDetails> appsDetailsWithoutLauncher, UsageStatsManager usm, boolean withLaunchers);
}
