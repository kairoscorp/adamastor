package corp.kairos.adamastor;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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

    public static UserContext[] createDummyContextList(String ActivityTAG, PackageManager pm) {

        // Creating 3 static contexts to simulate the intended behaviour */
        UserContext[] staticContexts = new UserContext[3];

        List<String> homeApps = Arrays.asList("com.google.android.talk", "com.facebook.katana");
        List<String> workApps = Arrays.asList("com.google.android.gm", "com.Slack", "com.google.android.calendar");
        List<String> travellingApps = Arrays.asList("com.google.android.apps.maps", "com.google.android.apps.fitness");

        List<List<String>> apps = Arrays.asList(homeApps, workApps, travellingApps);

        // These loops just associate different apps with different contexts
        int n = 0;
        for (List<String> appsList : apps) {
            List<AppDetails> appDetailsList = new ArrayList<>();
            for (String p : appsList) {
                try {
                    ApplicationInfo ai = pm.getApplicationInfo(p, 0);

                    String label = (String) pm.getApplicationLabel(ai);
                    String name = ai.packageName;
                    Drawable icon = pm.getApplicationIcon(ai);
                    boolean system = !isUserApp(ai);
                    AppDetails app = new AppDetails(label, name, icon, system);
                    appDetailsList.add(app);
                } catch (PackageManager.NameNotFoundException e) {
                    Log.e(ActivityTAG, "App not found");
                    e.printStackTrace();
                }
            }
            staticContexts[n] = new UserContext("Context " + n, appDetailsList);
            n++;
        }
        return staticContexts;
    }

    public static boolean isUserApp(ApplicationInfo ai) {
        int mask = ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP;
        return (ai.flags & mask) == 0;
    }

    public enum Measure {

        HOURS, MINUTES, SECONDS, MILLISECONDS;

        public int id;
        public String name;

        static {
            HOURS.id = 0;
            HOURS.name = "hours";

            MINUTES.id = 1;
            MINUTES.name = "minutes";

            SECONDS.id = 2;
            SECONDS.name = "seconds";

            MILLISECONDS.id = 3;
            MILLISECONDS.name = "milliseconds";
        }

        public long getValue(long value) {
            switch (this.id) {
                case 0:
                    return TimeUnit.MILLISECONDS.toHours(value);
                case 1:
                    return TimeUnit.MILLISECONDS.toMinutes(value);
                case 2:
                    return TimeUnit.MILLISECONDS.toSeconds(value);
                case 3:
                    return value;

                default:
                    return 0;
            }
        }

        public static Pair<Long, String> getValueAndMeasure(long value) {
            long timeInMinutes, timeInSeconds, timeInHours;
            timeInHours = TimeUnit.MILLISECONDS.toHours(value);
            if(timeInHours < 1) {
                timeInMinutes = TimeUnit.MILLISECONDS.toMinutes(value);
                if(timeInMinutes < 1) {
                    timeInSeconds = TimeUnit.MILLISECONDS.toSeconds(value);
                    if(timeInSeconds < 1) {
                        return new Pair<>(value, "milliseconds");
                    } else {
                        return new Pair<>(timeInSeconds, "seconds");
                    }
                } else {
                    return new Pair<>(timeInMinutes, "minutes");
                }
            } else {
                return new Pair<>(timeInHours, "hours");
            }
        }

        private Measure getMeasure(int id) {
            switch (id) {
                case 0:
                    return HOURS;
                case 1:
                    return MINUTES;
                case 2:
                    return SECONDS;
                case 3:
                    return MILLISECONDS;

                default:
                    return HOURS;
            }
        }

        public Measure max(int id) {
            return getMeasure(Math.max(this.id, id));
        }

    }
}
