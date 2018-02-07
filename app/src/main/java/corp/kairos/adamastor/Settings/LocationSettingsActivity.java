package corp.kairos.adamastor.Settings;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
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

import corp.kairos.adamastor.Onboarding.Onboard3LocationActivity;
import corp.kairos.adamastor.Onboarding.Onboard4ContextAppsActivity;
import corp.kairos.adamastor.R;
import corp.kairos.adamastor.UserContext;


public class LocationSettingsActivity extends Onboard3LocationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setAnimation("left");
        Button save = findViewById(R.id.next2);
        save.setText("save changes");
        save.setVisibility(View.VISIBLE);
        loadInitialMaps();
    }

    @Override
    public void onBackPressed() {
        setAnimation("right");
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void goNext(View v) {
        save();
        setAnimation("right");
        finish();
    }

    private void loadInitialMaps() {
        Settings settingsUser = Settings.getInstance(getApplicationContext());
        UserContext homeContext = settingsUser.getUserContext("Leisure");
        UserContext workContext = settingsUser.getUserContext("Work");
        Location homeLoc = homeContext.getLocation();
        Location workLoc = workContext.getLocation();
        String homeAddress = homeContext.getAddress();
        String workAddress = workContext.getAddress();

        showHomeMap(homeAddress, new LatLng(homeLoc.getLatitude(), homeLoc.getLongitude()) );
        showWorkMap(workAddress, new LatLng(workLoc.getLatitude(), workLoc.getLongitude()) );
    }
}
