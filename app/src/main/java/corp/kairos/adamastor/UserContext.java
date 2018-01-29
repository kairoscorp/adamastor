package corp.kairos.adamastor;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;

/*
* This class represents a context
* */
public class UserContext implements Parcelable {
    private String contextName; //The name of the context
    private List<AppDetails> contextApps; //The list of apps associated with this context
    private Location location;
    private GregorianCalendar init;
    private GregorianCalendar end;


    public UserContext(String contextName, List<AppDetails> contextApps) {
        this.contextName = contextName;
        this.contextApps = contextApps;
    }

    protected UserContext(Parcel in) {
        contextName = in.readString();
        contextApps = in.createTypedArrayList(AppDetails.CREATOR);
        location = in.readParcelable(Location.class.getClassLoader());
        init = (GregorianCalendar) in.readSerializable();
        end = (GregorianCalendar) in.readSerializable();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.contextName);
        dest.writeList(this.contextApps);
        dest.writeParcelable(location, flags);
        dest.writeSerializable(init);
        dest.writeSerializable(end);
    }

    public static final Creator<UserContext> CREATOR = new Creator<UserContext>() {
        @Override
        public UserContext createFromParcel(Parcel in) {
            return new UserContext(in);
        }

        @Override
        public UserContext[] newArray(int size) {
            return new UserContext[size];
        }
    };

    // TODO: make contexts carry their own icon
    public static int getContextIcon(String contextName) {
        switch (contextName) {
            case "Work":
                return R.drawable.ic_work_black_24dp;
            case "Home":
            case "Leisure":
                return R.drawable.ic_leisure_black_24dp;
            case "Travel":
            case "Commute":
                return R.drawable.ic_commute_black_24dp;
        }
        return R.drawable.ic_settings_black_24dp;
    }
}

