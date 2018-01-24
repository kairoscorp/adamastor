package corp.kairos.adamastor;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    public static UserContext[] createDummyContextList(String ActivityTAG, PackageManager pm) {

        // Creating 3 static contexts to simulate the intended behaviour */
        UserContext[] staticContexts = new UserContext[3];

        List<String> homeApps = Arrays.asList("com.google.android.talk", "com.facebook.katana");
        List<String> workApps = Arrays.asList("com.google.android.gm", "com.Slack", "com.google.android.calendar");
        List<String> travellingApps = Arrays.asList("com.google.android.apps.maps", "com.google.android.apps.fitness");

        List<List<String>> apps = Arrays.asList(homeApps, workApps, travellingApps);

        // These loops just associate different apps with different contexts
        int n = 0;
        for(List<String> appsList : apps){
            List<AppDetails> appDetails = new ArrayList<>();
            for(String p: appsList){
                try {
                    ApplicationInfo ai = pm.getApplicationInfo(p,0);

                    String label = (String) pm.getApplicationLabel(ai);
                    String name = ai.packageName;
                    Drawable icon = pm.getApplicationIcon(ai);

                    AppDetails app = new AppDetails(label, name, icon);
                    appDetails.add(app);
                } catch (PackageManager.NameNotFoundException e) {
                    Log.e(ActivityTAG, "App not found");
                    e.printStackTrace();
                }
            }
            staticContexts[n] = new UserContext("Context " + n, appDetails);
            n++;
        }
        return staticContexts;
    }
}
