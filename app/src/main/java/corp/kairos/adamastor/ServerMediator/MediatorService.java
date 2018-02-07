package corp.kairos.adamastor.ServerMediator;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.github.mikephil.charting.data.LineDataSet;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.GregorianCalendar;

import corp.kairos.adamastor.Collector.CollectorService;
import corp.kairos.adamastor.Collector.ModelHandler;
import corp.kairos.adamastor.Settings.Settings;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MediatorService extends Service {

    private MediatorServiceBinder mMediatorServiceBinder = new MediatorServiceBinder();
    private static OkHttpClient client = new OkHttpClient();
    private static MediatorService instance;
    private static final String TAG = "MediatorServiceLog";
    private static final String INITIAL_ETL_URL = "http://138.68.134.198:8080/settings";
    private static final String REGULAR_ETL_URL = "http://138.68.134.198:8080/data";
    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    MediaType CSV = MediaType.parse("text/csv");
    private ConnectivityManager connectivityManager;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMediatorServiceBinder;
    }

    public class MediatorServiceBinder extends Binder {
        public MediatorService getBinder(){
            return MediatorService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    public static MediatorService getInstance(){
        if(instance == null) {
            instance = new MediatorService();
        }
        return instance;
    }

    @Override
    public void onCreate() {
        Log.i(TAG,"Mediator Service Created");
        super.onCreate();

        connectivityManager =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, MediatorService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        GregorianCalendar initialDate = new GregorianCalendar();
        initialDate.add(GregorianCalendar.WEEK_OF_YEAR, 1);
        initialDate.set(GregorianCalendar.HOUR_OF_DAY, 0);
        initialDate.set(GregorianCalendar.MINUTE, 0);

        long oneweek = 7 * 24 *60 * 60 * 1000;

        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                initialDate.getTimeInMillis(),
                oneweek,
                pendingIntent);

    }

    class ETLAlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
            boolean isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;

            if(activeNetwork != null
                    && activeNetwork.isConnectedOrConnecting()
                    && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI){

                MediatorService.getInstance().regularETL();
            }
        }
    }

    public void initialETL(){
        RequestBody requestBody = RequestBody.create(JSON, initialJSON());

        Log.i(TAG, initialJSON());

        Request request = new Request.Builder()
                .url(INITIAL_ETL_URL)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, e.getMessage());
            }

            @Override
            public void onResponse(Call call, final Response response)  {
                if (!response.isSuccessful()) {
                    Log.i(TAG, "Server Response Not Sucessful");
                } else {

                    try {
                        ByteArrayInputStream bis = new ByteArrayInputStream(
                                response.body().bytes());

                        CollectorService.getInstance().setModel(bis);
                    } catch (IOException e) {
                        Log.i(TAG, "Error Reading Server Response");
                    }

                }
            }
        });

    }

    public void regularETL(){

        CollectorService collectorService = CollectorService.getInstance();

        if(collectorService != null) {

            String locationJSON = locationJSON();
            File logDump = collectorService.dumpDatabaseToCSV();

            RequestBody requestBody = new MultipartBody.Builder("boundary")
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("csvfile", "csvfile.csv", RequestBody.create(CSV, logDump))
                    .addFormDataPart("locations", "locations.JSON", RequestBody.create(JSON, locationJSON))
                    .build();

            Request request = new Request.Builder()
                    .url(REGULAR_ETL_URL)
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.i(TAG, e.getMessage());
                }

                @Override
                public void onResponse(Call call, final Response response) {
                    if (!response.isSuccessful()) {
                        Log.i(TAG, "Server Response Not Sucessful");
                    } else {
                        try {
                            ByteArrayInputStream bis = new ByteArrayInputStream(
                                    response.body().bytes());

                            CollectorService.getInstance().setModel(bis);
                        } catch (IOException e) {
                            Log.i(TAG, "Error Reading Server Response");
                        }
                    }
                }
            });
        }
    }

    public void alternative_regularETL(){

        CollectorService collectorService = CollectorService.getInstance();

        if(collectorService != null) {

            File logDump = collectorService.dumpCleanDatabaseToCSV();

            RequestBody requestBody = RequestBody.create(CSV, logDump);

            Request request = new Request.Builder()
                    .url(REGULAR_ETL_URL)
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.i(TAG, e.getMessage());
                }

                @Override
                public void onResponse(Call call, final Response response) {
                    if (!response.isSuccessful()) {
                        Log.i(TAG, "Server Response Not Sucessful");
                    } else {
                        try {
                            ByteArrayInputStream bis = new ByteArrayInputStream(
                                    response.body().bytes());

                            CollectorService.getInstance().setModel(bis);
                        } catch (IOException e) {
                            Log.i(TAG, "Error Reading Server Response");
                        }
                    }
                }
            });
        }
    }

    private String initialJSON(){
        String workStart = "";
        String workEnd = "";

        try {

            Settings settings = Settings.getInstance(CollectorService.getInstance());

            GregorianCalendar workInitCalendar = settings.getUserContext("Work").getInit();
            GregorianCalendar workEndCalendar = settings.getUserContext("Work").getEnd();

            workStart = String.valueOf(workInitCalendar.get(GregorianCalendar.HOUR_OF_DAY));
            workEnd = String.valueOf(workEndCalendar.get(GregorianCalendar.HOUR_OF_DAY));

        }catch(Exception e){

            workStart = "9";
            workEnd = "17";

        }

        return "{\"working_hours_start\":" + workStart + "," +
                "\"working_hours_end\":" + workEnd + "}";
    }

    private String locationJSON(){
        String workLocationLat, workLocationLong, homeLocationLat, homeLocationLong;

        try{
            Settings settings = Settings.getInstance(CollectorService.getInstance());

            Location locationWork = settings.getUserContext("Work").getLocation();
            Location locationHome = settings.getUserContext("Home").getLocation();

            workLocationLat = String.valueOf(locationWork.getLatitude());
            workLocationLong = String.valueOf(locationWork.getLongitude());
            homeLocationLat = String.valueOf(locationHome.getLatitude());
            homeLocationLong = String.valueOf(locationWork.getLongitude());

        }catch(Exception e){

            workLocationLat = "0";
            workLocationLong = "0";
            homeLocationLat = "0";
            homeLocationLong = "0";

        }

        return "{\"work_location_latitude\":" + workLocationLat + "," +
                "\"work_location_longitude\":" + workLocationLong + "," +
                "\"home_location_latitude\":" + homeLocationLat + "," +
                "\"home_location_longitude\":" + homeLocationLong + "}";
    }

}
