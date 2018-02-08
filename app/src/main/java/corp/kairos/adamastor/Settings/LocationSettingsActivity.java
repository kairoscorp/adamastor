package corp.kairos.adamastor.Settings;

import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.model.LatLng;

import corp.kairos.adamastor.Onboarding.Onboard3LocationActivity;
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
        super.loadInitialMaps();
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
}
