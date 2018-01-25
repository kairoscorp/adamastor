package corp.kairos.adamastor.Onboarding;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Set;

import corp.kairos.adamastor.R;
import corp.kairos.adamastor.Settings.Settings;
import corp.kairos.adamastor.UserContext;


public class Onboard3LocationActivity extends AppCompatActivity{
    private Settings settingsUser;

    private boolean checkLocation = false;
    private GoogleMap workMap;
    private GoogleMap homeMap;
    private MapView workplaceView;
    private MapView homeplaceView;
    public static final int PLACE_PICKER_REQUEST = 7;

    private Location homeLoc;
    private Location workLoc;

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
        loadMapsSettings(savedInstanceState);
    }


    //Override Methods in order to control life cycle of MapViews
    @Override
    protected void onDestroy() {
        super.onDestroy();
        workplaceView.onDestroy();
        homeplaceView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        workplaceView.onResume();
        homeplaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        workplaceView.onPause();
        homeplaceView.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        workplaceView.onStart();
        homeplaceView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        workplaceView.onStop();
        homeplaceView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        workplaceView.onLowMemory();
        homeplaceView.onLowMemory();
    }

    public void loadMapsSettings(Bundle savedInstanceState) {
        findViewById(R.id.btn_pickHome).setVisibility(View.INVISIBLE);
        findViewById(R.id.btn_pickWork).setVisibility(View.INVISIBLE);
        workplaceView = findViewById(R.id.mapWork);
        workplaceView.onCreate(savedInstanceState);
        workplaceView.getMapAsync(new OnMapReadyCallback() {
            public void onMapReady(GoogleMap googleMap) {
                workMap = googleMap;
                workMap.setMinZoomPreference(10);
                if(checkLocation) {
                     pos = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                }
                workMap.moveCamera(CameraUpdateFactory.newLatLng(pos));

                workMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        workMap.clear();
                        MarkerOptions opts = new MarkerOptions();
                        opts.position(latLng);
                        workMap.addMarker(opts);
                        workLoc = new Location("provider");
                        workLoc.setLatitude(latLng.latitude);
                        workLoc.setLongitude(latLng.longitude);
                        Button btn = findViewById(R.id.btn_pickWork);
                        btn.setVisibility(View.VISIBLE);

                        btn.setOnClickListener(view -> {
                            pickWork = true;
                            UserContext context = settingsUser.getUserContext("Work");
                            context.setLocation(workLoc);
                            settingsUser.setUserContext(context);
                            showNext();
                        });

                    }
                });
            }
        });

        homeplaceView = findViewById(R.id.mapHome);
        homeplaceView.onCreate(savedInstanceState);
        homeplaceView.getMapAsync(new OnMapReadyCallback() {
            public void onMapReady(GoogleMap googleMap) {
                homeMap = googleMap;
                homeMap.setMinZoomPreference(12);
                if(checkLocation) {
                    pos = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                }
                homeMap.moveCamera(CameraUpdateFactory.newLatLng(pos));

                homeMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        homeMap.clear();
                        MarkerOptions opts = new MarkerOptions();
                        opts.position(latLng);
                        homeMap.addMarker(opts);
                        homeLoc = new Location("provider");
                        homeLoc.setLatitude(latLng.latitude);
                        homeLoc.setLongitude(latLng.longitude);
                        Button btn = findViewById(R.id.btn_pickHome);
                        btn.setVisibility(View.VISIBLE);

                        btn.setOnClickListener(view -> {
                            pickHome = true;
                            UserContext context = settingsUser.getUserContext("Home");
                            context.setLocation(homeLoc);
                            settingsUser.setUserContext(context);
                            showNext();
                        });

                    }
                });
            }
        });

    }

    private void pickHomeLocation() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
    }

    private void showHomeMap(String address) {
        findViewById(R.id.warning_location_home).setVisibility(View.INVISIBLE);
        ((TextView)findViewById(R.id.home_location_label)).setTextSize(14);
        ((TextView)findViewById(R.id.home_location_address)).setText(address);
    }

    private void showNext() {
        if (pickHome && pickWork) {
            findViewById(R.id.next2).setVisibility(View.VISIBLE);
        }
    }

    public void goNext(View v) {
        Intent i = new Intent(this,Onboard4ContextAppsActivity.class);
        Set<String> contexts = this.settingsUser.getContexts().keySet();
        startActivity(i);
        finish();
    }
}
