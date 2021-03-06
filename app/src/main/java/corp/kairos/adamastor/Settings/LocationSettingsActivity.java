package corp.kairos.adamastor.Settings;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import corp.kairos.adamastor.Onboarding.Onboard3LocationActivity;
import corp.kairos.adamastor.R;


public class LocationSettingsActivity extends Onboard3LocationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setAnimation("right");
        Button save = findViewById(R.id.next2);
        save.setText("save changes");
        save.setVisibility(View.VISIBLE);
        super.loadInitialMaps();
    }

    @Override
    public void onBackPressed() {
        setAnimation("left");
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
        setAnimation("left");
        finish();
    }
}
