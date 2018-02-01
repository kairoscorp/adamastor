package corp.kairos.adamastor.Settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import corp.kairos.adamastor.AppDetails;
import corp.kairos.adamastor.AppsManager.AppsManager;
import corp.kairos.adamastor.Collector.CollectorService;
import corp.kairos.adamastor.UserContext;

public class Settings {
    private static Settings instance;
    private AppsManager appsManager;
    private PackageManager packageManager;

    private static final String TAG = Settings.class.getName();

    private Map<String,UserContext> contexts;
    public int contextsNumber;

    private Context context;
    private static final String TIME_FROM = "_timeFrom";
    private static final String TIME_TO = "_timeTo";
    private static final String LOC_LAT = "_lat";
    private static final String LOC_LNG = "_lng";
    private static final String CONTEXT_NAME = "_name";

    public static final String [] contextsNames = {
            "Home",
            "Work",
            "Commute"
    };

    private Settings(Context c) {
        this.context = c;
        this.packageManager = c.getPackageManager();
        this.appsManager = AppsManager.getInstance();

        this.contexts = new HashMap<>();

        setUpContexts();

        this.contextsNumber = this.contexts.size();
    }

    private void setUpContexts() {
        for(String contextName: contextsNames) {
            this.contexts.put(contextName, loadContextSettings(contextName));
        }
    }

    public static Settings getInstance(Context c) {
        if(instance == null) {
            instance = new Settings(c);
        }

        return instance;
    }

    public boolean isOnboardingDone() {
        SharedPreferences sharedPref = context.getSharedPreferences("GENERAL",Context.MODE_PRIVATE);
        return sharedPref.getBoolean("OnboardingDone",false);
    }
    public void setOnboardingDone() {
        SharedPreferences sharedPref = context.getSharedPreferences("GENERAL",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("OnboardingDone",true);
        editor.apply();
    }

    public UserContext loadContextSettings(String contextName) {
        SharedPreferences sharedPref = context.getSharedPreferences("Prefs_"+contextName, Context.MODE_PRIVATE);

        List<AppDetails> contextApps = new ArrayList<>();
        GregorianCalendar start = new GregorianCalendar();
        GregorianCalendar end = new GregorianCalendar();
        Location pos = new Location("Provider");

        start.setTimeInMillis(sharedPref.getLong(TIME_FROM, 0));
        end.setTimeInMillis(sharedPref.getLong(TIME_TO,0));

        pos.setLatitude(sharedPref.getFloat(LOC_LAT,0));
        pos.setLongitude(sharedPref.getFloat(LOC_LNG,0));
        Set<AppDetails> allApps = this.appsManager.getAllApps(context.getPackageManager(), true);

        for (AppDetails app : allApps) {
            if(sharedPref.getBoolean(app.getPackageName(), false)) {
                contextApps.add(app);
            }
        }

        UserContext uc = new UserContext(contextName, contextApps);
        uc.setTimes(start,end);
        uc.setLocation(pos);
        return uc;
    }

    public void saveContextSettings() {
        for(UserContext userContext : contexts.values()) {
            this.saveContextSettings(userContext);
        }
    }


    public void saveContextSettings(UserContext userContext) {
        SharedPreferences sharedPref = context.getSharedPreferences("Prefs_"+userContext.getContextName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(CONTEXT_NAME, userContext.getContextName());

        //Save time settings
        editor.putLong(TIME_FROM, userContext.getInit().getTimeInMillis());
        editor.putLong(TIME_TO, userContext.getEnd().getTimeInMillis());

        //Save location
        editor.putFloat(LOC_LAT,(float)userContext.getLocation().getLatitude());
        editor.putFloat(LOC_LNG,(float)userContext.getLocation().getLongitude());

        //Save Apps
        for (AppDetails app : userContext.getContextApps()) {
            editor.putBoolean(app.getPackageName(), true);
        }

        editor.apply();
    }


    public Map<String, UserContext> getContexts() {
        return this.contexts;
    }

    public List<String> getContextNames() {
        return new ArrayList<>(this.contexts.keySet());
    }

    public UserContext getUserContext(String c) {
        return this.contexts.get(c);
    }

    public UserContext getUserContext(int id) {
        String currentContextName = contextsNames[id];
        return this.contexts.get(currentContextName);
    }

    public UserContext getCurrentUserContext() {
        return this.contexts.get(this.getContextNamefromCollector());
    }

    public UserContext getNextUserContext(String contextName){
        List<UserContext> aux = new ArrayList<>();
        UserContext result = null;

        aux.addAll(this.contexts.values());

        for(int i = 0; i < aux.size(); i++){
            if(aux.get(i).getContextName().equals(contextName)){
                result = aux.get((i+1)%aux.size());
                break;
            }
        }

        this.informCollectorContextChange(result.getContextName());

        return result;
    }

    private String getContextNamefromCollector(){
        int context = CollectorService.getInstance().getPredictedContext();
        String result;
        switch(context){
            case 1: result = "Home";
                    break;
            case 2: result = "Travel";
                    break;
            case 3: result = "Work";
                    break;
            default: result = "Home";
                    break;
        }

        return result;
    }

    private void informCollectorContextChange(String contextName){

        int context = 1;

        switch(contextName){
            case "Home": context = 1;
                break;
            case "Travel": context = 2;
                break;
            case "Work": context = 3;
                break;
            default: context = 1;
                break;
        }

        CollectorService.getInstance().userContextChange(context);
    }

    public void setUserContext(UserContext c) {
        this.contexts.put(c.getContextName(), c);
    }


    public void resetSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            for(String contextName: this.contexts.keySet()) {
                context.deleteSharedPreferences("Prefs_"+contextName);
            }
        }
    }

    public UserContext[] getUserContextsAsArray() {
        Collection collection = contexts.values();
        UserContext[] userContextsArray = new UserContext[collection.size()];
        collection.toArray(userContextsArray);
        return userContextsArray;
    }

    public UserContext getZeroContext() {
        Collection<UserContext> contexts = this.contexts.values();
        UserContext zeroContext = new UserContext("Other Apps", new ArrayList<>());
        int i;
        Set<AppDetails> allApps = this.appsManager.getAllApps(this.packageManager, false);
        for (AppDetails app: allApps) {
            i = 0;
            for (UserContext context: contexts) {
                if (context.appExists(app)) break;
                else i++;
            }
            if (i==contextsNumber) zeroContext.addApp(app);
        }

        return zeroContext;
    }

    public UserContext getStartingContext(){
        return this.contexts.get("Home");
    }
}


