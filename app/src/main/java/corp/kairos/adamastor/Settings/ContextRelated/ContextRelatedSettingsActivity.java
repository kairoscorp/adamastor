package corp.kairos.adamastor.Settings.ContextRelated;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;

import com.borax12.materialdaterangepicker.time.RadialPickerLayout;
import com.borax12.materialdaterangepicker.time.TimePickerDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import corp.kairos.adamastor.Animation.AnimationCompatActivity;
import corp.kairos.adamastor.Home.HomeActivity;
import corp.kairos.adamastor.R;
import corp.kairos.adamastor.Settings.Settings;
import corp.kairos.adamastor.UserContext;

public class ContextRelatedSettingsActivity extends AnimationCompatActivity implements TimePickerDialog.OnTimeSetListener{
    private GoogleMap workMap;
    private GoogleMap leisureMap;
    private MapView workPlaceView;
    private MapView leisurePlaceView;
    private Location leisureLoc;
    private Location workLoc;
    private UserContext workContext;
    private UserContext leisureContext;
    private static final String MAP_VIEW_BUNDLE_WORK_KEY = "MapViewBundleWorkKey";
    private static final String MAP_VIEW_BUNDLE_LEISURE_KEY = "MapViewBundleLeisureKey";
    private Settings settingsUser;
    GregorianCalendar from = new GregorianCalendar();
    GregorianCalendar to = new GregorianCalendar();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setAnimation("down");
        setContentView(R.layout.settings_onboarding);
        this.settingsUser = Settings.getInstance(this);
        workContext = this.settingsUser.getUserContext("Work");
        leisureContext = this.settingsUser.getUserContext("Leisure");
        loadTabViewSettings();
        loadMapsSettings(savedInstanceState);
        loadWorkTimeSettings();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapWorkViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_WORK_KEY);
        Bundle mapLeisureViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_LEISURE_KEY);
        if (mapWorkViewBundle == null) {
            mapWorkViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_WORK_KEY, mapWorkViewBundle);
        }
        if (mapLeisureViewBundle == null) {
            mapLeisureViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_LEISURE_KEY, mapLeisureViewBundle);
        }

        workPlaceView.onSaveInstanceState(mapWorkViewBundle);
        leisurePlaceView.onSaveInstanceState(mapLeisureViewBundle);

    }

    //Override Methods in order to control life cycle of MapViews
    @Override
    protected void onDestroy() {
        super.onDestroy();
        workPlaceView.onDestroy();
        leisurePlaceView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        workPlaceView.onResume();
        leisurePlaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        workPlaceView.onPause();
        leisurePlaceView.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        workPlaceView.onStart();
        leisurePlaceView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        workPlaceView.onStop();
        leisurePlaceView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        workPlaceView.onLowMemory();
        leisurePlaceView.onLowMemory();
    }


    public void ShowWorkRelatedApps(View v) {
        Intent i = new Intent(this, ContextRelatedAppsActivity.class);
        String ctx = "Work";
        i.putExtra("CONTEXT", ctx);
        startActivity(i);
    }

    public void ShowLeisureApps(View v) {
        Intent i = new Intent(this, ContextRelatedAppsActivity.class);
        String ctx = "Leisure";
        i.putExtra("CONTEXT", ctx);
        startActivity(i);
    }

    public void showTravelApps(View v) {
        Intent i = new Intent(this, ContextRelatedAppsActivity.class);
        String ctx = "Commute";
        i.putExtra("CONTEXT", ctx);
        startActivity(i);
    }

    public void loadWorkTimeSettings() {
        TextView timeFrom = findViewById(R.id.work_from);
        TextView timeTo = findViewById(R.id.work_to);

        DateFormat formatter = new SimpleDateFormat("HH:mm");

        timeFrom.setText(formatter.format(workContext.getInit().getTime()));
        timeTo.setText(formatter.format(workContext.getEnd().getTime()));
    }

    public void showTimePicker(View v) {
        GregorianCalendar start = workContext.getInit();
        GregorianCalendar end = workContext.getEnd();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                this,
                start.get(GregorianCalendar.HOUR_OF_DAY),
                start.get(GregorianCalendar.MINUTE),
                true,
                end.get(GregorianCalendar.HOUR_OF_DAY),
                end.get(GregorianCalendar.MINUTE)

        );
        tpd.show(getFragmentManager(), "Timepickerdialog");
    }
    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int hourOfDayEnd, int minuteEnd) {
        from.set(GregorianCalendar.HOUR_OF_DAY,hourOfDay);
        from.set(GregorianCalendar.MINUTE,minute);
        to.set(GregorianCalendar.HOUR_OF_DAY,hourOfDayEnd);
        to.set(GregorianCalendar.MINUTE,minuteEnd);

        workContext.setTimes(from, to);
        settingsUser.setUserContext(workContext);

        TextView timeFrom = findViewById(R.id.work_from);
        TextView timeTo = findViewById(R.id.work_to);
        DateFormat formatter = new SimpleDateFormat("HH:mm");

        timeFrom.setText(formatter.format(workContext.getInit().getTime()));
        timeTo.setText(formatter.format(workContext.getEnd().getTime()));
    }

    public void loadMapsSettings(Bundle savedInstanceState) {
        //TODO: Add listeners to collect location and save in sharedPreferences
        //TODO: Add marker to the map

        workPlaceView = findViewById(R.id.mapWork);
        workPlaceView.onCreate(savedInstanceState);
        workPlaceView.getMapAsync(googleMap -> {
            workMap = googleMap;
            workMap.setMinZoomPreference(10);
            Location l = workContext.getLocation();
            LatLng ps = new LatLng(l.getLatitude(),l.getLongitude());
            workMap.moveCamera(CameraUpdateFactory.newLatLng(ps));
            MarkerOptions opts = new MarkerOptions();
            workMap.addMarker(opts.position(ps));

            workMap.setOnMapClickListener(latLng -> {
                workMap.clear();
                MarkerOptions opts1 = new MarkerOptions();
                opts1.position(latLng);
                workMap.addMarker(opts1);
                workLoc = new Location("provider");
                workLoc.setLatitude(latLng.latitude);
                workLoc.setLongitude(latLng.longitude);
                Button btn = findViewById(R.id.btn_pickWork);
                btn.setVisibility(View.VISIBLE);

                btn.setOnClickListener(view -> {
                    UserContext context = workContext;
                    context.setLocation(workLoc);
                    settingsUser.setUserContext(context);
                });
            });
        });

        leisurePlaceView = findViewById(R.id.mapLeisure);
        leisurePlaceView.onCreate(savedInstanceState);
        leisurePlaceView.getMapAsync(googleMap -> {
            leisureMap = googleMap;
            leisureMap.setMinZoomPreference(10);
            Location l = leisureContext.getLocation();
            LatLng ps = new LatLng(l.getLatitude(),l.getLongitude());
            leisureMap.moveCamera(CameraUpdateFactory.newLatLng(ps));
            MarkerOptions opts = new MarkerOptions();
            leisureMap.addMarker(opts.position(ps));

            leisureMap.setOnMapClickListener(latLng -> {
                leisureMap.clear();
                MarkerOptions opts12 = new MarkerOptions();
                opts12.position(latLng);
                leisureMap.addMarker(opts12);
                leisureLoc = new Location("provider");
                leisureLoc.setLatitude(latLng.latitude);
                leisureLoc.setLongitude(latLng.longitude);
                Button btn = findViewById(R.id.btn_pickLeisure);
                btn.setVisibility(View.VISIBLE);

                btn.setOnClickListener(view -> {
                    UserContext context = leisureContext;
                    context.setLocation(leisureLoc);
                    settingsUser.setUserContext(context);
                });
            });
        });

    }

    public void loadTabViewSettings() {
        TabHost host = findViewById(R.id.TabHost);
        host.setup();
        //Tab Context work
        TabHost.TabSpec spec = host.newTabSpec("Tab One");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Work Context");
        host.addTab(spec);
        //Tab Context leisure
        spec = host.newTabSpec("Tab Two");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Leisure Context");
        host.addTab(spec);
    }

    public void saveChanges(View v) {
        this.settingsUser.saveContextSettings();
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
        finish();
    }
}
