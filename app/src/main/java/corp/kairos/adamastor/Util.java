package corp.kairos.adamastor;

import java.util.Set;

public class Util {
    public static Object getObjectByIndex(int position, Set set) {
        int i = 0;
        for(Object object: set) {
            if(i == position) {
                return object;
            }
            i++;
        }
        return null;
    }


    public static String getContextNameById(int id) {
        switch (id) {
            case 0:
                return "Home";

            case 1:
                return "Work";

            case 2:
                return "Travel";

            case 3:
                return "Fitness";

            default:
                return "Other";
        }
    }
}
