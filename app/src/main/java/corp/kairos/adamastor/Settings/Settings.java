package corp.kairos.adamastor.Settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


import corp.kairos.adamastor.AppDetail;
import corp.kairos.adamastor.AppDetailComparator;
import corp.kairos.adamastor.UserContext;



public class Settings{
    private Context ctx;
    private PackageManager packageManager;

    private Map<String,UserContext> contexts;
    private Set<AppDetail> allApps;

    private static final String TIME_FROM = "_timeFrom";
    private static final String TIME_TO = "_timeTo";
    private static final String LOC_LAT = "_lat";
    private static final String LOC_LNG = "_lng";
    private static final String CONTEXT_NAME = "_name";

    public Settings (Context c) {
        this.ctx = c;
        this.packageManager = ctx.getPackageManager();
        this.allApps = loadApps();

        this.contexts = new HashMap<>();
        this.contexts.put("Work",loadContextSettings("Work"));
        this.contexts.put("Leisure",loadContextSettings("Leisure"));
        this.contexts.put("Travel",loadContextSettings("Travel"));

    }

    public boolean isOnboardingDone() {
        SharedPreferences sharedPref = ctx.getSharedPreferences("GENERAL",Context.MODE_PRIVATE);
        return sharedPref.getBoolean("OnboardingDone",false);
    }
    public void setOnboardingDone() {
        SharedPreferences sharedPref = ctx.getSharedPreferences("GENERAL",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("OnboardingDone",true);
        editor.apply();
    }

    public UserContext loadContextSettings(String context) {
        SharedPreferences sharedPref = ctx.getSharedPreferences("Prefs_"+context, Context.MODE_PRIVATE);

        List<AppDetail> ctxApps = new ArrayList<>();
        GregorianCalendar start = new GregorianCalendar();
        GregorianCalendar end = new GregorianCalendar();
        Location pos = new Location("Provider");

        start.setTimeInMillis(sharedPref.getLong(TIME_FROM, 0));
        end.setTimeInMillis(sharedPref.getLong(TIME_TO,0));

        pos.setLatitude(sharedPref.getFloat(LOC_LAT,0));
        pos.setLongitude(sharedPref.getFloat(LOC_LNG,0));

        for (AppDetail app : this.allApps) {
            if(sharedPref.getBoolean(app.getName(), false)) {
                ctxApps.add(app);
            }
        }

        UserContext uc = new UserContext(context,ctxApps);
        uc.setTimes(start,end);
        uc.setLocation(pos);
        return uc;
    }

    public void saveContextSettings(String context) {
        SharedPreferences sharedPref = ctx.getSharedPreferences("Prefs_"+context, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        UserContext uc = contexts.get(context);

        editor.putString(CONTEXT_NAME,context);
        //Save time settings
        editor.putLong(TIME_FROM, uc.getInit().getTimeInMillis());
        editor.putLong(TIME_TO, uc.getEnd().getTimeInMillis());
        //Save location
        editor.putFloat(LOC_LAT,(float)uc.getLocation().getLatitude());
        editor.putFloat(LOC_LNG,(float)uc.getLocation().getLongitude());
        //Save Apps
        for (AppDetail app : uc.getContextApps()) {
            editor.putBoolean(app.getName(), true);
        }
        editor.apply();
    }


    public Set<AppDetail> loadApps() {
        Set<AppDetail> apps = new TreeSet<>(new AppDetailComparator());
      
        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> availableActivities = this.packageManager.queryIntentActivities(i, 0);
        for (ResolveInfo ri : availableActivities) {
            String label = ri.loadLabel(this.packageManager).toString();
            String name = ri.activityInfo.packageName.toString();
            Drawable icon = ri.activityInfo.loadIcon(this.packageManager);
            AppDetail app = new AppDetail(label, name, icon);
            apps.add(app);
        }
        return apps;
    }

    public Map<String, UserContext> getContexts() {
        return this.contexts;
    }

    public UserContext getUserContext(String c) {
        return this.contexts.get(c);
    }


    public Set<AppDetail> getAllApps() {return this.allApps;}

    public void resetSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ctx.deleteSharedPreferences("Prefs_Work");
            ctx.deleteSharedPreferences("Prefs_Leisure");
            ctx.deleteSharedPreferences("Prefs_Travel");
        }
    }
}


