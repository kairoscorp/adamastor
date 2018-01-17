package corp.kairos.adamastor;

import android.location.Location;

import java.util.GregorianCalendar;
import java.util.List;

/*
* This class represents a context
* */
public class UserContext {
    private String contextName; //The name of the context
    private List<AppDetail> contextApps; //The list of apps associated with this context
    private Location location;
    private GregorianCalendar init;
    private GregorianCalendar end;

    public UserContext(String contextName, List<AppDetail> contextApps) {
        this.contextName = contextName;
        this.contextApps = contextApps;
    }
    public String getContextName() {
        return contextName;
    }

    public List<AppDetail> getContextApps() {
        return contextApps;
    }

    public void addApp(AppDetail app) {
        contextApps.add(app);
    }

    public void removeApp(AppDetail app){
        contextApps.remove(app);
    }

    public boolean appExists(AppDetail app){
        return contextApps.contains(app);

    }
    public void setTimes(GregorianCalendar init, GregorianCalendar end){
        this.init=init;
        this.end=end;
    }

    public GregorianCalendar getEnd() {
        return end;
    }

    public GregorianCalendar getInit() {
        return init;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location){
        this.location = location;
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

