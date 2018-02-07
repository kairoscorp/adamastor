package corp.kairos.adamastor.Settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import corp.kairos.adamastor.Animation.AnimationCompatActivity;
import corp.kairos.adamastor.R;
import corp.kairos.adamastor.Settings.ContextRelated.EditContextAppsActivity;


public class KairosSettingsActivity extends AnimationCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kairos_settings);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setAnimation("left");
    }

    @Override
    public void onBackPressed() {
        setAnimation("right");
        finish();
    }

    public void schedules(View v) {
        startActivity(new Intent(getApplicationContext(),ScheduleSettingsActivity.class));
    }

    public void locations(View v) {
        startActivity(new Intent(getApplicationContext(),LocationSettingsActivity.class));
    }

    public void contextApps(View v) {
        startActivity( new Intent(getApplicationContext(), EditContextAppsActivity.class));
    }
}
