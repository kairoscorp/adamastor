package corp.kairos.adamastor.userContext.dao;


import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import corp.kairos.adamastor.AppDetails;
import corp.kairos.adamastor.userContext.UserContext;

public class SPUserContextDAO implements UserContextDAO {

    private final String TIME_FROM = "_timeFrom";
    private final String TIME_TO = "_timeTo";
    private final String LOC_LAT = "_lat";
    private final String LOC_LNG = "_lng";
    private final String CONTEXT_NAME = "_name";

    /**
     * This parameter should always be the application context
     */
    private final Context appContext;

    /**
     * Creates a new instance of this class
     * <p/>
     * This DAO uses the SharedPreferences as a backend
     *
     * @param applicationContext The Android Context for this application
     */
    public SPUserContextDAO(Context applicationContext) {
        this.appContext = applicationContext;
    }

    @Override
    public UserContext loadUserContext(String contextName, Set<AppDetails> allApps) {
        SharedPreferences sharedPref = appContext.getSharedPreferences("Prefs_"+contextName, Context.MODE_PRIVATE);

        List<AppDetails> ctxApps = new ArrayList<>();
        GregorianCalendar start = new GregorianCalendar();
        GregorianCalendar end = new GregorianCalendar();
        Location pos = new Location("Provider");

        start.setTimeInMillis(sharedPref.getLong(TIME_FROM, 0));
        end.setTimeInMillis(sharedPref.getLong(TIME_TO,0));

        pos.setLatitude(sharedPref.getFloat(LOC_LAT,0));
        pos.setLongitude(sharedPref.getFloat(LOC_LNG,0));

        for (AppDetails app : allApps) {
            if(sharedPref.getBoolean(app.getPackageName(), false)) {
                ctxApps.add(app);
            }
        }

        return new UserContext(contextName, ctxApps, pos, start, end);
    }

    @Override
    public void storeUserContext(UserContext userContext) {
        String contextName = userContext.getContextName();
        SharedPreferences sharedPref = appContext.getSharedPreferences("Prefs_"+contextName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(CONTEXT_NAME, contextName);

        //Save time settings
        editor.putLong(TIME_FROM, userContext.getInit().getTimeInMillis());
        editor.putLong(TIME_TO, userContext.getEnd().getTimeInMillis());

        //Save location
        editor.putFloat(LOC_LAT, (float)userContext.getLocation().getLatitude());
        editor.putFloat(LOC_LNG, (float)userContext.getLocation().getLongitude());

        //Save Apps
        for (AppDetails app : userContext.getContextApps()) {
            editor.putBoolean(app.getPackageName(), true);
        }
        editor.apply();
    }

    @Override
    public void deleteUserContext(String contextName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            appContext.deleteSharedPreferences("Prefs_"+contextName);
        }
    }

    @Override
    public void deleteAllUserContexts(Set<String> contextNames) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            for (String name : contextNames)
                appContext.deleteSharedPreferences("Prefs_"+name);
    }


}
