package corp.kairos.adamastor.Collector;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;


import corp.kairos.adamastor.Settings.Settings;
import corp.kairos.adamastor.AppDetails;
import corp.kairos.adamastor.AppsManager.AppsManager;
import corp.kairos.adamastor.Collector.LogDatabaseHelper;
import corp.kairos.adamastor.UserContext;


public class CollectorService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String NULL_APP = "NULL";

    private static final String TAG = "CollectorServiceLog";

    private static final int INTERVAL = 10000;

    private CollectorServiceBinder mCollectorServiceBinder = new CollectorServiceBinder();;
    private LocationManager locationManager;
    private static CollectorService instance;
    private PackageManager packageManager;
    private static Settings settingsUser;
    private LocationListener locationListener;
    private Location lastLocation;
    private boolean checkLocation = false;
    private static LogDatabaseHelper logDatabaseHelper;
    private GoogleApiClient googleApiClient;
    private ModelHandler modelHandler;
    private int userActivityNow = 1;
    private int predictedContext = 1;
    private int predictInterval = 6;
    //private int predictInterval = 360;
    private int predictIteration = 0;
    private boolean predictionActive;

    @Override
    public void onCreate(){
        Log.i(TAG, "ServiceCreated");
        // Managers and Settings
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        packageManager = this.getPackageManager();
        settingsUser = Settings.getInstance(this);
        createLocationListener();
        checkLocationPermissions();
        logDatabaseHelper = new LogDatabaseHelper(this);

        ActivityMonitorReceiver amr = new ActivityMonitorReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("kairos.CollectorService.ACTIVITY_MONITOR_RESULT");
        registerReceiver(amr,filter);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        googleApiClient.connect();

        this.modelHandler = new ModelHandler();

        InputStream modelIS = this.logDatabaseHelper.getModel();

        if(modelIS != null) {
            this.modelHandler.setModel(modelIS);
            this.predictionActive = true;
        }else{
            this.predictionActive = false;
        }

        if(modelIS == null){

            this.modelHandler.setModel(readModelFile());
            this.predictionActive = true;
        }

        this.skp();

        this.predictIteration =  predictInterval;

        new Thread(){
            public void run(){
                mainLoop();
            }
        }.start();
    }


    public Map<String, Long> getContextStatistics() {
        return logDatabaseHelper.getContextStatistics();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    private void mainLoop(){
        while(true){
            try {
                double latitude, longitude;
                int phoneActivity, playingMusic;
                int context = 1;
                int callActivity = getPhoneCallState();
                int ringMode = getRingtoneMode();
                String provider;
                String account = getUsername();
                Calendar localDate = Calendar.getInstance();
                String time = getTimeNow(localDate);
                String appForeground = getForegroundTask();

                if(isPhoneActive()){
                    phoneActivity = 1;
                }else {
                    appForeground = NULL_APP;
                    phoneActivity = 0;
                }

                if(isMusicPlaying()){
                    playingMusic = 1;
                }else{
                    playingMusic = 0;
                }

                if(checkLocation){
                    latitude =lastLocation.getLatitude();
                    longitude = lastLocation.getLongitude();
                    provider = String.valueOf(lastLocation.getProvider());
                }else{
                    latitude = 0;
                    longitude = 0;
                    provider = "PROVIDER";
                }

                Log.i(TAG,

                        time + "::"
                                + appForeground + "::"
                                + userActivityNow + "::"
                                + phoneActivity + "::"
                                + callActivity + "::"
                                + playingMusic + "::"
                                + ringMode + "::"
                                + latitude + "::"
                                + longitude + "::"
                                + provider + "::"
                                + account + "::"
                                + predictedContext);


                if(predictIteration >= predictInterval){
                    predictIteration = 0;
                    Log.i(TAG,"loop ok");
                    if(this.predictionActive) {
                        Log.i(TAG,"predictionActive");
                        context = this.getContext(localDate, appForeground, userActivityNow, phoneActivity, callActivity,
                                playingMusic, ringMode, longitude, latitude, provider);
                        CollectorService.getInstance().setContext(context);
                    }
                }


                this.predictedContext = CollectorService.getInstance().getPredictedContext();


                logDatabaseHelper.addLogEntry(time, appForeground, userActivityNow,phoneActivity,
                        callActivity, playingMusic, ringMode, latitude, longitude,
                        provider, account, predictedContext);

                this.predictIteration++;

                Thread.sleep(INTERVAL);
            }catch(InterruptedException ex){
                Log.i(TAG, "Exception in Main Loop");
            }
        }
    }

    private String getTimeNow(Calendar localDate){
        //YYYY-MM-DD HH:MM:SS.SSS
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return formatter.format(localDate.getTime());
    }


    private String getForegroundTask() {
        String currentApp = NULL_APP;
        String result;
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,  time - 10000000, time);
            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }
        } else {
            ActivityManager am = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
            currentApp = tasks.get(0).processName;
        }
        try{
            result = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(
                    currentApp, PackageManager.GET_META_DATA));
        }catch(Exception e){
            result = currentApp;
        }

        return result;
    }

    private boolean isPhoneActive(){
        boolean result;
        /*PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        try{
            result = pm.isInteractive();
        }catch(Exception ex){
            result = false;
        }
        return result;*/
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        switch(display.getState()){
            case Display.STATE_OFF : result = false; break;
            case Display.STATE_ON : result = true; break;
            case Display.STATE_VR : result = true; break;
            case Display.STATE_DOZE : result = true; break;
            case Display.STATE_DOZE_SUSPEND : result = true; break;
            case Display.STATE_UNKNOWN : result = false; break;
            default : result =false; break;
        }

        return result;
    }

    private boolean isMusicPlaying(){
        AudioManager am = (AudioManager)this.getSystemService(CollectorService.getInstance().AUDIO_SERVICE);
        return am.isMusicActive();
    }

    private int getPhoneCallState(){
        int result;
        TelephonyManager tm = (TelephonyManager)getSystemService(CollectorService.getInstance().TELEPHONY_SERVICE);
        switch(tm.getCallState()){
            case TelephonyManager.CALL_STATE_IDLE : result = 0;
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK: result = 1;
                break;
            case TelephonyManager.CALL_STATE_RINGING: result = 1;
                break;
            default : result = 0;
        }

        return result;
    }

    private void checkLocationPermissions(){
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkLocation = false;
        }else{
            checkLocation = true;
        }

        lastLocation = locationManager.getLastKnownLocation(
                locationManager.getBestProvider(criteria,true));

        if(lastLocation == null){
            checkLocation = false;
        }

        locationManager.requestLocationUpdates(
                locationManager.getBestProvider(criteria,true),
                1000,
                10,
                locationListener);

    }

    private String getUsername() {
        AccountManager manager = AccountManager.get(this);
        Account[] accounts = manager.getAccountsByType("com.google");
        List<String> possibleEmails = new LinkedList<String>();
        for (Account account : accounts) {
            possibleEmails.add(account.name);
        }

        if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
            String email = possibleEmails.get(0);
            String[] parts = email.split("@");
            if (parts.length > 0 && parts[0] != null)
                return parts[0];
            else
                return "null";
        } else
            return "null";
    }

    private int getRingtoneMode(){
        int result;
        AudioManager am = (AudioManager)this.getSystemService(CollectorService.getInstance().AUDIO_SERVICE);
        switch(am.getRingerMode()){
            case AudioManager.RINGER_MODE_NORMAL : result = 0; break;
            case AudioManager.RINGER_MODE_SILENT : result = 1; break;
            case AudioManager.RINGER_MODE_VIBRATE : result = 2; break;
            default : result = 0;
        }

        return result;
    }


    private void createLocationListener(){
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                lastLocation = location;
                if(lastLocation == null){
                    checkLocation = false;
                }else{
                    checkLocation = true;
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                checkLocationPermissions();
            }

            @Override
            public void onProviderEnabled(String provider) {
                checkLocationPermissions();
            }

            @Override
            public void onProviderDisabled(String provider) {
                checkLocationPermissions();
            }
        };
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mCollectorServiceBinder;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Intent intent = new Intent( this, ActivityRecognitionService.class );
        PendingIntent pendingIntent = PendingIntent.getService( this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );

        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates( googleApiClient, 10000, pendingIntent );


        //ActivityRecognitionClient activityRecognitionClient = ActivityRecognition.getClient(this);
        //activityRecognitionClient.requestActivityUpdates(3000, pendingIntent );
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public class CollectorServiceBinder extends Binder {
        public CollectorService getBinder(){
            Log.i(TAG, "GettingBinder");
            return CollectorService.this;
        }
    }

    public void dumpDatabaseToCSV(File path, String fileName){
        logDatabaseHelper.exportDatabaseCSV(path,fileName);
    }

    public File dumpDatabaseToCSV(){
        return logDatabaseHelper.exportDatabaseCSV();
    }

    class ActivityMonitorReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            userActivityNow = intent.getIntExtra("ActivityNow" , 1);
        }
    }

    private InputStream readModelFile(){
        FileInputStream fileInputStream = null;
        try {
            File modelFile = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()
                    + File.separator + "model.ser");
            fileInputStream = new FileInputStream(modelFile);
        }catch(Exception e){
            Log.i("CollectorServiceLog", "Error openeing file");
        }

        return fileInputStream;
    }

    public void setModel(InputStream modelIS){
        if(this.logDatabaseHelper.setModel(modelIS)){
            this.predictionActive = true;
        }else{
            this.predictionActive = false;
            Log.i(TAG, "Error reading Model");
        }
    }

    public int getAppKey(String app){
        return logDatabaseHelper.getAppKey(app);
    }

    private void skp(){
        AppsManager appsManager = AppsManager.getInstance();
        Set<AppDetails> allApps = appsManager.getAllApps(packageManager, true);
        for(AppDetails ad : allApps){
            this.logDatabaseHelper.addEntryAppLookUp(ad.getPackageName());
        }
    }

    /**
     * 0 - sleep
     * 1 - leasure
     * 2 - travel
     * 3 - work
     */
    private int getContext(Calendar time, String foreground, int activity, int screen_active,
                          int call_active, int music_active, int ring_mode, double longitute,
                          double latitude, String provider){

        int context = this.modelHandler.predictContext(time,foreground,activity,screen_active,
                call_active, music_active,ring_mode,longitute,latitude,provider);

        this.predictedContext = context;

        return context;
    }

    private void setContext(int context){
        this.predictedContext = context;
    }

    public int getPredictedContext(){
        return this.predictedContext;
    }

    public static CollectorService getInstance(){
        if(instance == null) {
            instance = new CollectorService();
        }
        return instance;
    }

    public void userContextChange(int context){
        this.predictedContext = context;
        this.predictIteration=0;
    }

    public int callContextPrediction(){

        double latitude, longitude;
        int phoneActivity, playingMusic;
        int callActivity = getPhoneCallState();
        int ringMode = 1;
        String provider;
        Calendar localDate = Calendar.getInstance();

        String appForeground = CollectorService.getInstance().getForegroundTask();


        if(isPhoneActive()){
            phoneActivity = 1;
        }else {
            appForeground = NULL_APP;
            phoneActivity = 0;
        }

        if(isMusicPlaying()){
            playingMusic = 1;
        }else{
            playingMusic = 0;
        }

        if(checkLocation){
            latitude =lastLocation.getLatitude();
            longitude = lastLocation.getLongitude();
            provider = String.valueOf(lastLocation.getProvider());
        }else{
            latitude = 0;
            longitude = 0;
            provider = "PROVIDER";
        }

        this.predictedContext = this.getContext(localDate,appForeground,userActivityNow,phoneActivity,callActivity,
                playingMusic,ringMode,longitude,latitude,provider);
        this.predictIteration = 0;
        return this.predictedContext;
    }


}
