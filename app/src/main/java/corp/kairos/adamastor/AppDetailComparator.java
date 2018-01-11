package corp.kairos.adamastor;

import java.io.Serializable;
import java.util.Comparator;

public class AppDetailComparator implements Comparator<AppDetail>,Serializable {
    @Override
    public int compare(AppDetail o1, AppDetail o2) {
        return o1.getLabel().compareTo(o2.getLabel());
    }
}
