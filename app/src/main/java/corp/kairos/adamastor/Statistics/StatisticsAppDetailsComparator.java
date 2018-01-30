package corp.kairos.adamastor.Statistics;

import java.io.Serializable;
import java.util.Comparator;

import corp.kairos.adamastor.AppDetails;

public class StatisticsAppDetailsComparator implements Comparator<AppDetails>, Serializable{
    @Override
    public int compare(AppDetails o1, AppDetails o2) {
        return o2.getTotalUsedTime().compareTo(o1.getTotalUsedTime());
    }
}