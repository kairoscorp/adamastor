package corp.kairos.adamastor.Onboarding;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import corp.kairos.adamastor.Animation.AnimationCompatActivity;
import corp.kairos.adamastor.R;
import corp.kairos.adamastor.Settings.Settings;

public class Onboard1WelcomeActivity extends AnimationCompatActivity {
    private Settings settingsUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboard1_welcome);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        this.settingsUser = Settings.getInstance(this);
        this.settingsUser.resetSettings();
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

}
