package corp.kairos.adamastor.Home;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import corp.kairos.adamastor.AllApps.AllAppsActivity;
import corp.kairos.adamastor.AnimActivity;
import corp.kairos.adamastor.AppDetail;
import corp.kairos.adamastor.AppsManager.AppsManager;
import corp.kairos.adamastor.Collector.CollectorService;
import corp.kairos.adamastor.Onboarding.Onboard1Activity;
import corp.kairos.adamastor.R;
import corp.kairos.adamastor.Settings.ContextRelated.ContextRelatedSettingsActivity;
import corp.kairos.adamastor.Settings.Settings;
import corp.kairos.adamastor.Statistics.StatisticsActivity;
import corp.kairos.adamastor.UserContext;

public class HomeActivity extends AnimActivity {

    private static final String TAG = HomeActivity.class.getName();

    private UserContext currentContext;
    private int currentContextIndex;
    private UserContext[] contexts = new UserContext[4];
    private View.OnClickListener clickHandler;
    private PackageManager pm;
    private AppsManager appsManager;

    private boolean permissionsGranted = false;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.appsManager = AppsManager.getInstance();
        Settings sets = new Settings(this);
        if(!sets.isOnboardingDone()){
            Intent i = new Intent(this, Onboard1Activity.class);
            startActivity(i);
            finish();
        } else {
            setContentView(R.layout.activity_home);

            setContexts();
            this.currentContext = this.contexts[0];
            this.currentContextIndex = 0;
            this.setRightActivity(AllAppsActivity.class);
            this.setLeftActivity(StatisticsActivity.class);

            addClickListener();
        }
    }

    private void addClickListener(){
        clickHandler = v -> {
            HomeApp ha = (HomeApp) v;
            Intent i = pm.getLaunchIntentForPackage(ha.getPackageName());
            HomeActivity.this.startActivity(i);
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        adjustScreenToContext();

        checkPermissions();
        if(permissionsGranted){
            bindCollectorService();
        }

    }

    /*
    * This method should be called when the home screen needs to change in order to show the
    * correct apps for the current context*/
    private void adjustScreenToContext() {
        TextView contextOnScreen = ((TextView) findViewById(R.id.id_context_label));

        //Just refresh the view if the context actually changed
        if(!(contextOnScreen.getText().equals(currentContext.getContextName()))) {
            LinearLayout contextAppsList = findViewById(R.id.id_context_apps_list);
            contextAppsList.removeAllViews();
            contextOnScreen.setText(currentContext.getContextName());

            for (AppDetail app : currentContext.getContextApps()) {
                LayoutInflater inflater = LayoutInflater.from(this);
                HomeApp inflatedView = (HomeApp) inflater.inflate(R.layout.home_app, null);

                inflatedView.setPackageName(app.getName());
                TextView textViewTitle = (TextView) inflatedView.findViewById(R.id.app_text);
                ImageView imageViewIte = (ImageView) inflatedView.findViewById(R.id.app_image);

                textViewTitle.setText(app.getLabel());
                imageViewIte.setImageDrawable(app.getIcon());
                inflatedView.setOnClickListener(clickHandler);

                contextAppsList.addView(inflatedView);
            }
        }
    }

    private void setContexts(){
        /*
        * Creating 4 static contexts to simulate the intended behaviour */
        List<String> homeApps = Arrays.asList("com.google.android.talk", "com.facebook.katana");
        List<String> workApps = Arrays.asList("com.google.android.gm", "com.Slack", "com.google.android.calendar");
        List<String> workoutApps = Arrays.asList("com.google.android.apps.fitness");
        List<String> travellingApps = Arrays.asList("com.google.android.apps.maps");

        List<List<String>> apps = Arrays.asList(homeApps, workApps, workoutApps, travellingApps);

        pm = getPackageManager();

        /*
        * These loops just associate different apps with different contexts*/
        int n = 0;
        for(List<String> appsList : apps){
            List<AppDetail> appDetails = new ArrayList<>();
            for(String p: appsList){
                try {
                    ApplicationInfo ai = pm.getApplicationInfo(p,0);

                    String label = (String) pm.getApplicationLabel(ai);
                    String name = ai.packageName;
                    Drawable icon = pm.getApplicationIcon(ai);

                    AppDetail app = new AppDetail(label, name, icon);
                    appDetails.add(app);
                } catch (PackageManager.NameNotFoundException e) {
                    Log.e(TAG, "App not found");
                    e.printStackTrace();
                }
            }
            this.contexts[n] = new UserContext("Context " + n, appDetails);
            n++;
        }
    }


    public void showAllAppsMenu(View v){
        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(50);
        Intent i = new Intent(this, AllAppsActivity.class);
        startActivity(i);
    }

    public void showSettings(View v){
        Intent i = new Intent(this, ContextRelatedSettingsActivity.class);
        startActivity(i);
    }

    public void showStatistics(View v){
        Intent i = new Intent(this, StatisticsActivity.class);
        startActivity(i);
    }


    /*
    * The mechanism for manual context change is pressing the context label.
    * This method is called when the context label is pressed.
    */
    public void changeContext(View view){
        this.currentContextIndex = (this.currentContextIndex + 1) % this.contexts.length;
        this.currentContext = this.contexts[this.currentContextIndex];
        adjustScreenToContext();
    }

    //KAIROS
    private void checkPermissions(){
        if ((ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) ||
                (ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED)||
                (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) ||
                (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.GET_ACCOUNTS)
                        != PackageManager.PERMISSION_GRANTED)){


            requestPremissions();

        }else{
            Log.i("CollectorServiceLog", "permissions OK");
            permissionsGranted = true;
        }
    }

    //KAIROS
    private void requestPremissions(){
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCOUNT_MANAGER,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.PACKAGE_USAGE_STATS,
                        Manifest.permission.GET_ACCOUNTS},
                MY_PERMISSIONS_REQUEST_LOCATION);
    }

    //KAIROS
    private void bindCollectorService(){
        Log.i("CollectorServiceLog", "Binding Service");
        Intent intent = new Intent(this, CollectorService.class);
        startService(intent);

    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionsGranted = true;
            } else {
                permissionsGranted = false;
            }
        }
    }

}