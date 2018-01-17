package corp.kairos.adamastor;

import android.location.Location;

import java.util.GregorianCalendar;
import java.util.List;

/*
* This class represents a context
* */
public class UserContext {
    private String contextName; //The name of the context
    private List<AppDetails> contextApps; //The list of apps associated with this context
    private Location location;
    private GregorianCalendar init;
    private GregorianCalendar end;


    public UserContext(String contextName, List<AppDetails> contextApps) {
        this.contextName = contextName;
        this.contextApps = contextApps;
    }
    public String getContextName() {
        return contextName;
    }

    public List<AppDetails> getContextApps() {
        return contextApps;
    }
    public void addApp(AppDetails app){
        contextApps.add(app);
    }

    public void removeApp(AppDetails app){
        contextApps.remove(app);
    }

    public boolean appExists(AppDetails app){
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

    public String toString(){
        StringBuilder sb = new StringBuilder(" --- " + contextName + " ---\n");
        for(AppDetails app: contextApps) {
            sb.append("- ").append(app.getPackageName()).append("\n");
        }
        return sb.toString();
    }
}

