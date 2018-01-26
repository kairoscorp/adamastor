package corp.kairos.adamastor.Settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;

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
import corp.kairos.adamastor.Onboarding.dao.OnboardStateDAO;
import corp.kairos.adamastor.userContext.UserContext;
import corp.kairos.adamastor.userContext.dao.SPUserContextDAO;
import corp.kairos.adamastor.userContext.dao.UserContextDAO;


public class Settings {
    private static Settings instance;
    private AppsManager appsManager;
    private PackageManager packageManager;

    private Map<String, UserContext> contexts;
    private int contextsNumber = 0;

    public static final String [] contextsNames = {
            "Home",
            "Work",
            "Commute"
    };

    private Settings(Context c) {
        this.packageManager = c.getPackageManager();
        this.appsManager = AppsManager.getInstance();
        this.contexts = new HashMap<>();

        UserContextDAO userContextDAO = new SPUserContextDAO(c);

        for(String contextName: contextsNames)
            this.contexts.put(
                contextName,
                userContextDAO.loadUserContext(
                    contextName,
                    this.appsManager.getAllApps(
                        this.packageManager,
                        true
                    )
                )
            );

        this.contextsNumber = this.contexts.size();
    }

    public static Settings getInstance(Context c) {
        if(instance == null) {
            instance = new Settings(c);
        }

        return instance;
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
        int min = 0;
        int max = contextsNames.length;
        int currentContextNumber = ThreadLocalRandom.current().nextInt(min, max);
        String currentContextName = contextsNames[currentContextNumber];
        return this.contexts.get(currentContextName);
    }

    public void setUserContext(UserContext c) {
        this.contexts.put(c.getContextName(), c);
    }

    public void saveContextSettings(Context appContext) {
        UserContextDAO userContextDAO = new SPUserContextDAO(appContext);
        for(UserContext userContext : contexts.values())
            userContextDAO.storeUserContext(userContext);
    }

    /**
     * Utility method to return all user contexts as an array
     *
     * @return An array with all user contexts
     */
    public UserContext[] getUserContextsAsArray() {
        Collection collection = contexts.values();
        UserContext[] userContextsArray = new UserContext[collection.size()];
        collection.toArray(userContextsArray);
        return userContextsArray;
    }

    /**
     * Utility method to compute the list of apps that don't belong in any context
     *
     * @return
     */
    public UserContext getZeroContext() {
        Collection<UserContext> contexts = this.contexts.values();
        UserContext zeroContext = new UserContext("Other Apps", new ArrayList<>());
        int i;
        Set<AppDetails> allApps = this.appsManager.getAllApps(this.packageManager, true);
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
}


