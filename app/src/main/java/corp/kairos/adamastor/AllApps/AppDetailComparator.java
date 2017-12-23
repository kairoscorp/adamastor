package corp.kairos.adamastor.AllApps;

import java.util.Comparator;

public class AppDetailComparator implements Comparator<AppDetail> {
    @Override
    public int compare(AppDetail o1, AppDetail o2) {
        return o1.getLabel().compareTo(o2.getLabel());
    }
}
