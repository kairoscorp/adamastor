package corp.kairos.adamastor.Onboarding;

import android.app.AppOpsManager;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Set;

import corp.kairos.adamastor.R;
import corp.kairos.adamastor.Settings.Settings;
import corp.kairos.adamastor.UserContext;


public class Onboard3LocationActivity extends AppCompatActivity{
    private Settings settingsUser;

    private GoogleMap workMap;
    private GoogleMap homeMap;
    private MapView workplaceView;
    private MapView homeplaceView;
    public static final int PLACE_PICKER_REQUEST_HOME = 1;
    public static final int PLACE_PICKER_REQUEST_WORK = 2;
    private Bundle savedInstanceState;

    private Location homeLoc = new Location("provider");
    private Location workLoc = new Location("provider");

    //UMinho coordinates, if no location provider is available, this will center the map in this location, because UMinho is amazing!
    private LatLng pos = new LatLng(41.56131,-8.393804);
    private boolean pickWork = false;
    private boolean pickHome = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboard3_location);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        this.settingsUser = Settings.getInstance(this);
        this.savedInstanceState = savedInstanceState;
        workplaceView = findViewById(R.id.mapWork);
        workplaceView.onCreate(this.savedInstanceState);
        homeplaceView = findViewById(R.id.mapHome);
        homeplaceView.onCreate(this.savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AppOpsManager appOps = (AppOpsManager) this.getSystemService(this.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), this.getPackageName());
        if(mode == AppOpsManager.MODE_ALLOWED) {
            startActivity(new Intent(getApplicationContext(),Onboard1WelcomeActivity.class));
        }else {
            startActivity(new Intent(getApplicationContext(),Onboard2SpecialPermissionActivity.class));
        }

    }

    public void pickHomeLocation(View v) {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST_HOME);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    public void pickWorkLocation(View v) {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST_WORK);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST_HOME)
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this,data);
                String address = String.format("%s", place.getAddress());
                showHomeMap(address, place.getLatLng());
                homeLoc.setLatitude(place.getLatLng().latitude);
                homeLoc.setLongitude(place.getLatLng().longitude);
            }
            if (requestCode == PLACE_PICKER_REQUEST_WORK)
                if (resultCode == RESULT_OK) {
                    Place place = PlacePicker.getPlace(this,data);
                    String address = String.format("%s", place.getAddress());
                    showWorkMap(address,place.getLatLng());
                    workLoc.setLatitude(place.getLatLng().latitude);
                    workLoc.setLongitude(place.getLatLng().longitude);
            }
    }

    private void showWorkMap(String address, LatLng pos) {
        findViewById(R.id.warning_location_work).setVisibility(View.INVISIBLE);
        ((TextView)findViewById(R.id.work_location_label)).setTextSize(15);
        ((TextView)findViewById(R.id.work_location_address)).setText(address);
        workplaceView.setVisibility(View.VISIBLE);
        workplaceView.setClickable(false);
        workplaceView.getMapAsync(googleMap -> {
            workMap = googleMap;
            workMap.getUiSettings().setMapToolbarEnabled(false);
            workMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
            MarkerOptions opts = new MarkerOptions();
            opts.position(pos);
            workMap.addMarker(opts);
        });
        pickWork = true;
        showNext();
    }

    private void showHomeMap(String address, LatLng pos) {
        findViewById(R.id.warning_location_home).setVisibility(View.INVISIBLE);
        ((TextView)findViewById(R.id.home_location_label)).setTextSize(15);
        ((TextView)findViewById(R.id.home_location_address)).setText(address);
        homeplaceView.setVisibility(View.VISIBLE);
        homeplaceView.setClickable(false);
        homeplaceView.getMapAsync(googleMap -> {
            homeMap = googleMap;
            homeMap.getUiSettings().setMapToolbarEnabled(false);
            homeMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
            MarkerOptions opts = new MarkerOptions();
            opts.position(pos);
            homeMap.addMarker(opts);
        });
        pickHome = true;
        showNext();
    }

    private void showNext() {
        if (pickHome && pickWork) {
            findViewById(R.id.next2).setVisibility(View.VISIBLE);
        }
    }

    public void goNext(View v) {
        Intent i = new Intent(this,Onboard4ContextAppsActivity.class);

        UserContext homeContext = this.settingsUser.getUserContext("Leisure");
        UserContext workContext = this.settingsUser.getUserContext("Work");

        homeContext.setLocation(homeLoc);
        workContext.setLocation(workLoc);

        this.settingsUser.setUserContext(homeContext);
        this.settingsUser.setUserContext(workContext);

        startActivity(i);
    }
}
