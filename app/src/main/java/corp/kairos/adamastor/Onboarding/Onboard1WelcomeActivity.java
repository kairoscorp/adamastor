package corp.kairos.adamastor.Onboarding;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;

import corp.kairos.adamastor.Animation.AnimationCompatActivity;
import corp.kairos.adamastor.R;
import corp.kairos.adamastor.Settings.Settings;

public class Onboard1WelcomeActivity extends AnimationCompatActivity {
    private boolean permissionsGranted = false;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private Settings settingsUser;

    private static final String TAG = Onboard1WelcomeActivity.class.getName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboard1_welcome);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        this.settingsUser = Settings.getInstance(this);
        this.settingsUser.resetSettings();
        checkPermissions();
    }
    @Override
    protected void onResume() {
        super.onResume();
        super.setAnimation("right");
    }

    @Override
    public void onBackPressed() {
        //do nothing!
    }

    public void start(View v) {
        Intent i = new Intent(this,Onboard2SpecialPermissionActivity.class);
        startActivity(i);
    }

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
            Log.i(TAG, "permissions OK");
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

}
