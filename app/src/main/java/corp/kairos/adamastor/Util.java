package corp.kairos.adamastor;

import java.util.Set;

public class Util {
    public static Object getObjectByIndex(int position, Set set) {
        int i = 0;
        for (Object object : set) {
            if (i == position) {
                return object;
            }
            i++;
        }
        return null;
    }
}
