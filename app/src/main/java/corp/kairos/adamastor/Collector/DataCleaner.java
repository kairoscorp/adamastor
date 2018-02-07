package corp.kairos.adamastor.Collector;

import android.location.Location;
import android.util.Log;

import java.util.Calendar;

import corp.kairos.adamastor.Settings.Settings;

public class DataCleaner {

    public static int getHour(Calendar c){
        return c.get(Calendar.HOUR_OF_DAY);
    }

    public static int getMinute(Calendar c){
        return c.get(Calendar.MINUTE);
    }

    public static int getWeekday(Calendar c){
        return c.get(Calendar.DAY_OF_WEEK);
    }

    public static int getLocation(double logNow, double latNow, String provider){
        double range = 0;
        int result = 1;

        if(provider.equals("gps")){
            range = 200;
        }else{
            range = 500;
        }

        Location locationNow = new Location("Location Now");
        locationNow.setLatitude(latNow);
        locationNow.setLongitude(logNow);

        Settings settings = Settings.getInstance(CollectorService.getInstance());

        Location locationWork = settings.getUserContext("Work").getLocation();
        Location locationHome = settings.getUserContext("Leisure").getLocation();

        double homeDistance = locationNow.distanceTo(locationHome);
        double workDistance = locationNow.distanceTo(locationWork);

        if(homeDistance < range){
            result = 1;
        }else if(workDistance < range){
            result = 2;
        }else{
            result = 3;
        }
        Log.i("CollectorServiceLog", "Location = " + String.valueOf(result));

        return result;
    }

    public static int getActivity(String app){

        return CollectorService.getInstance().getAppKey(app);

    }

}
