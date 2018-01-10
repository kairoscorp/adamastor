package corp.kairos.adamastor.Onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import com.borax12.materialdaterangepicker.time.RadialPickerLayout;
import com.borax12.materialdaterangepicker.time.TimePickerDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import corp.kairos.adamastor.Home.HomeActivity;
import corp.kairos.adamastor.R;
import corp.kairos.adamastor.Settings;

public class OnboardingActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener{

    private GoogleMap workMap;
    private GoogleMap homeMap;
    private MapView workplaceView;
    private MapView homeplaceView;
    private static final String MAP_VIEW_BUNDLE_WORK_KEY = "MapViewBundleWorkKey";
    private static final String MAP_VIEW_BUNDLE_HOME_KEY = "MapViewBundleHomeKey";
    private Settings sets;
    GregorianCalendar from = new GregorianCalendar();
    GregorianCalendar to = new GregorianCalendar();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboarding);

        this.sets = new Settings(this);
        loadTabViewSettings();
        loadMapsSettings(savedInstanceState);
        loadWorkTimeSettings();
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

    public void loadWorkTimeSettings() {
        TextView timeFrom = findViewById(R.id.work_from);
        TextView timeTo = findViewById(R.id.work_to);

        DateFormat formatter = new SimpleDateFormat("HH:mm");

        timeFrom.setText(formatter.format(sets.getUserContext("Work").getInit().getTime()));
        timeTo.setText(formatter.format(sets.getUserContext("Work").getEnd().getTime()));
    }

    public void showTimePicker(View v) {
        GregorianCalendar start = sets.getUserContext("Work").getInit();
        GregorianCalendar end = sets.getUserContext("Work").getEnd();
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

        sets.getUserContext("Work").setTimes(from, to);

        TextView timeFrom = findViewById(R.id.work_from);
        TextView timeTo = findViewById(R.id.work_to);
        DateFormat formatter = new SimpleDateFormat("HH:mm");
        timeFrom.setText(formatter.format(sets.getUserContext("Work").getInit().getTime()));
        timeTo.setText(formatter.format(sets.getUserContext("Work").getEnd().getTime()));
    }

    public void loadMapsSettings(Bundle savedInstanceState) {
        //TODO: Add listeners to collect location and save in sharedPreferences
        //TODO: Add marker to the map

        workplaceView = findViewById(R.id.mapWork);
        workplaceView.onCreate(savedInstanceState);
        workplaceView.getMapAsync(new OnMapReadyCallback() {
            public void onMapReady(GoogleMap googleMap) {
                workMap = googleMap;
                workMap.setMinZoomPreference(12);
                LatLng ny = new LatLng(40.7143528, -74.0059731);
                workMap.moveCamera(CameraUpdateFactory.newLatLng(ny));
            }
        });

        homeplaceView = findViewById(R.id.mapHome);
        homeplaceView.onCreate(savedInstanceState);
        homeplaceView.getMapAsync(new OnMapReadyCallback() {
            public void onMapReady(GoogleMap googleMap) {
                homeMap = googleMap;
                homeMap.setMinZoomPreference(12);
                LatLng pt = new LatLng(41.5503200, -8.4200500);
                homeMap.moveCamera(CameraUpdateFactory.newLatLng(pt));
            }
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
        //Tab Context Leisure
        spec = host.newTabSpec("Tab Two");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Leisure Context");
        host.addTab(spec);
    }

    public void saveChanges(View v) {
        this.sets.saveContextSettings("Work");
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
        finish();
    }
}
