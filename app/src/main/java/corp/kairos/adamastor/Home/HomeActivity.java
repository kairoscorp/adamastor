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
import corp.kairos.adamastor.Onboarding.Onboard1WelcomeActivity;
import corp.kairos.adamastor.R;
import corp.kairos.adamastor.Settings.ContextRelated.ContextRelatedSettingsActivity;
import corp.kairos.adamastor.Settings.Settings;
import corp.kairos.adamastor.Statistics.StatisticsActivity;
import corp.kairos.adamastor.UserContext;

public class HomeActivity extends AnimationActivity {

    private static final String TAG = HomeActivity.class.getName();

    private UserContext currentContext;
    private int currentContextIndex;
    private View.OnClickListener clickHandler;
    private PackageManager packageManager;
    private Settings settingsUser;
    private AppsManager appsManager;


    private boolean permissionsGranted = false;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

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
            this.currentContextIndex = 0;
            this.currentContext = settingsUser.getCurrentUserContext();

            addClickListener();
            adjustScreenToContext();

            // Set animations
            this.setDownActivity(AllAppsActivity.class);
            this.setLeftActivity(StatisticsActivity.class);

            checkPermissions();
            if (permissionsGranted) {
                bindCollectorService();
            }
        }
    }

    private void addClickListener() {
        clickHandler = v -> {
            super.setAnimation("right");
            HomeApp ha = (HomeApp) v;
            Intent i = packageManager.getLaunchIntentForPackage(ha.getPackageName());
            this.startActivity(i);
        };
    }

    /*
    * This method should be called when the home screen needs to change in order to show the
    * correct apps for the current context*/
    private void adjustScreenToContext() {
        TextView contextOnScreen = ((TextView) findViewById(R.id.id_context_label));

        //Just refresh the view if the context actually changed
        if (!(contextOnScreen.getText().equals(currentContext.getContextName()))) {
            LinearLayout contextAppsList = findViewById(R.id.id_context_apps_list);
            contextAppsList.removeAllViews();
            contextOnScreen.setText(currentContext.getContextName());

            for (AppDetails app : currentContext.getContextApps()) {
                LayoutInflater inflater = LayoutInflater.from(this);
                HomeApp inflatedView = (HomeApp) inflater.inflate(R.layout.home_app, null);

                inflatedView.setPackageName(app.getPackageName());
                TextView textViewTitle = (TextView) inflatedView.findViewById(R.id.app_text);
                ImageView imageViewIte = (ImageView) inflatedView.findViewById(R.id.app_image);

                textViewTitle.setText(app.getLabel());
                imageViewIte.setImageDrawable(app.getIcon());
                inflatedView.setOnClickListener(clickHandler);

                contextAppsList.addView(inflatedView);
            }
        }
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

    public void showStatistics(View v) {
        super.setAnimation("left");
        Intent i = new Intent(this, StatisticsActivity.class);
        startActivity(i);
    }


    /*
    * The mechanism for manual context change is pressing the context label.
    * This method is called when the context label is pressed.
    */
    public void changeContext(View view) {
        this.currentContextIndex = (this.currentContextIndex + 1) % settingsUser.getContextNames().size();
        this.currentContext = settingsUser.getUserContext(currentContextIndex);
        adjustScreenToContext();
    }

    //KAIROS
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


            requestPremissions();

        } else {
            Log.i(TAG, "permissions OK");
            permissionsGranted = true;
        }
    }

    //KAIROS
    private void requestPremissions() {
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
    private void bindCollectorService() {
        Log.i(TAG, "Binding Service");
        Intent intent = new Intent(this, CollectorService.class);
        startService(intent);

    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionsGranted = true;
            } else {
                permissionsGranted = false;
            }
        }
    }

}