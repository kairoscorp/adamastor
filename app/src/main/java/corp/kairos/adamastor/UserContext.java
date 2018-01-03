package corp.kairos.adamastor;

import android.location.Location;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import corp.kairos.adamastor.AllApps.AppDetail;
import corp.kairos.adamastor.AllApps.AppDetailComparator;

/**
 * Created by kiko on 23-12-2017.
 */

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
    private void addApp(AppDetail app){contextApps.add(app);}
    private void removeApp(AppDetail app){
        contextApps.remove(app);
    }
    private boolean appExists(AppDetail app){
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
        return location;
    }

    public void setLocation(Location location){
        this.location = location;
    }

}

