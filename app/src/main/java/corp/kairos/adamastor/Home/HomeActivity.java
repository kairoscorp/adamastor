package corp.kairos.adamastor.Home;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import corp.kairos.adamastor.AllApps.AllAppsActivity;
import corp.kairos.adamastor.Animation.AnimationActivity;
import corp.kairos.adamastor.AppDetails;
import corp.kairos.adamastor.AppsManager.AppsManager;
import corp.kairos.adamastor.Collector.CollectorService;
import corp.kairos.adamastor.ContextList.ContextListActivity;
import corp.kairos.adamastor.Onboarding.Onboard1WelcomeActivity;
import corp.kairos.adamastor.R;
import corp.kairos.adamastor.Settings.ContextRelated.ContextRelatedSettingsActivity;
import corp.kairos.adamastor.Settings.Settings;
import corp.kairos.adamastor.Statistics.StatisticsActivity;
import corp.kairos.adamastor.UserContext;

public class HomeActivity extends AnimationActivity {

    public static final String TAG = HomeActivity.class.toString();
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private PackageManager packageManager;
    private Settings settingsUser;
    private AppsManager appsManager;

    private UserContext[] contexts;

    private boolean permissionsGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.packageManager = getPackageManager();
        this.settingsUser = Settings.getInstance(this);
        this.appsManager = AppsManager.getInstance();
        this.appsManager.setupApps(packageManager);

        if (!settingsUser.isOnboardingDone()) {
            // Set animation
            super.setAnimation("up");

            Intent i = new Intent(this, Onboard1WelcomeActivity.class);
            startActivity(i);
            finish();

        } else {
            setContentView(R.layout.activity_home);

            // Setup contexts


            // Load favorite apps


            // Setup Contexts

            // Set animations
            this.setDownActivity(AllAppsActivity.class);
            this.setLeftActivity(StatisticsActivity.class);
            this.setRightActivity(ContextListActivity.class);

            checkPermissions();
            if (permissionsGranted) {
                bindCollectorService();
            }

        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        checkPermissions();
        if (permissionsGranted)
            bindCollectorService();
    }

    public void showAllAppsMenu(View v) {
        super.setAnimation("down");
        Intent i = new Intent(this, AllAppsActivity.class);
        startActivity(i);
    }

    public void showSettings(View v) {
        super.setAnimation("up");
        Intent i = new Intent(this, ContextRelatedSettingsActivity.class);
        startActivity(i);
    }


    private void checkPermissions() {
        if ((ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) ||
                (ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) ||
                (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) ||
                (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.GET_ACCOUNTS)
                        != PackageManager.PERMISSION_GRANTED)) {

            requestPermissions();

        } else {
            Log.i(TAG, "permissions OK");
            permissionsGranted = true;
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCOUNT_MANAGER,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.PACKAGE_USAGE_STATS,
                        Manifest.permission.GET_ACCOUNTS},
                MY_PERMISSIONS_REQUEST_LOCATION);
    }

    private void bindCollectorService() {
        Log.i(TAG, "Binding Service");
        Intent intent = new Intent(this, CollectorService.class);
        startService(intent);

    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == MY_PERMISSIONS_REQUEST_LOCATION)
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                permissionsGranted = true;
            else
                permissionsGranted = false;
    }

}