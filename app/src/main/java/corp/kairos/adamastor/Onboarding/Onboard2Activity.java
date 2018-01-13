package corp.kairos.adamastor.Onboarding;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import corp.kairos.adamastor.R;
import corp.kairos.adamastor.Settings.Settings;

import static android.location.Criteria.ACCURACY_HIGH;

/**
 * Created by jlsilva94 on 10/01/18.
 */

public class Onboard2Activity extends AppCompatActivity{
    private Settings sets;

    private LocationManager locationManager;
    private Location lastLocation;
    private boolean checkLocation = false;
    private GoogleMap workMap;
    private GoogleMap homeMap;
    private MapView workplaceView;
    private MapView homeplaceView;
    private static final String MAP_VIEW_BUNDLE_WORK_KEY = "MapViewBundleWorkKey";
    private static final String MAP_VIEW_BUNDLE_HOME_KEY = "MapViewBundleHomeKey";

    private Location homeLoc;
    private Location workLoc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboard2);
        this.sets = new Settings(this);
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        checkLocationPermissions();
        loadMapsSettings(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapWorkViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_WORK_KEY);
        Bundle mapHomeViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_WORK_KEY);
        if (mapWorkViewBundle == null) {
            mapWorkViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_WORK_KEY, mapWorkViewBundle);
        }
        if (mapHomeViewBundle == null) {
            mapHomeViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_HOME_KEY, mapHomeViewBundle);
        }

        workplaceView.onSaveInstanceState(mapWorkViewBundle);
        homeplaceView.onSaveInstanceState(mapHomeViewBundle);

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
        workplaceView = findViewById(R.id.mapWork);
        workplaceView.onCreate(savedInstanceState);
        workplaceView.getMapAsync(new OnMapReadyCallback() {
            public void onMapReady(GoogleMap googleMap) {
                workMap = googleMap;
                workMap.setMinZoomPreference(10);
                LatLng ny = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                workMap.moveCamera(CameraUpdateFactory.newLatLng(ny));

                workMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        workMap.clear();
                        MarkerOptions opts = new MarkerOptions();
                        opts.position(latLng);
                        workMap.addMarker(opts);
                        workLoc = new Location(lastLocation.getProvider());
                        workLoc.setLatitude(latLng.latitude);
                        workLoc.setLongitude(latLng.longitude);
                        Button btn = findViewById(R.id.btn_pickWork);
                        btn.setVisibility(View.VISIBLE);

                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                sets.getUserContext("Work").setLocation(workLoc);
                                showNext();
                            }
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
                homeMap.setMinZoomPreference(10);
                LatLng pt = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
                homeMap.moveCamera(CameraUpdateFactory.newLatLng(pt));
                MarkerOptions opts = new MarkerOptions();

                homeMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        homeMap.clear();
                        MarkerOptions opts = new MarkerOptions();
                        opts.position(latLng);
                        homeMap.addMarker(opts);
                        homeLoc = new Location(lastLocation.getProvider());
                        homeLoc.setLatitude(latLng.latitude);
                        homeLoc.setLongitude(latLng.longitude);
                        Button btn = findViewById(R.id.btn_pickHome);
                        btn.setVisibility(View.VISIBLE);

                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                sets.getUserContext("Leisure").setLocation(homeLoc);
                                showNext();
                            }
                        });

                    }
                });
            }
        });

    }

    public void checkLocationPermissions(){
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkLocation = false;
        }else{
            checkLocation = true;
        }

        lastLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria,true));

        if(lastLocation == null){
            checkLocation = false;
        }
    }

    private void showNext() {
        if ((homeLoc != null) && (workLoc != null)) {
            findViewById(R.id.next2).setVisibility(View.VISIBLE);
        }
    }

    public void goNext(View v) {

        //sets.getUserContext("Home").setLocation(homeLoc);
        sets.saveContextSettings("Work");
        sets.saveContextSettings("Leisure");
        Intent i = new Intent(this,Onboard3Activity.class);
        Set<String> contexts = sets.getContexts().keySet();
        i.putExtra("CONTEXTS", contexts.toArray());
        startActivity(i);
        finish();
    }
}
