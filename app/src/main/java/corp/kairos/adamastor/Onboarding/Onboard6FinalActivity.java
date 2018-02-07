package corp.kairos.adamastor.Onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.borax12.materialdaterangepicker.time.RadialPickerLayout;
import com.borax12.materialdaterangepicker.time.TimePickerDialog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import corp.kairos.adamastor.Animation.AnimationCompactActivity;
import corp.kairos.adamastor.Home.HomeActivity;
import corp.kairos.adamastor.R;
import corp.kairos.adamastor.Settings.Settings;
import corp.kairos.adamastor.UserContext;

/**
 * Created by jlsilva94 on 07/02/18.
 */

public class Onboard6FinalActivity extends AnimationCompactActivity{

    private Settings settingsUser;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.settingsUser = Settings.getInstance(getApplicationContext());
        setContentView(R.layout.onboard6_final);
    }

    @Override
    protected void onResume() {
        super.onResume();
        super.setAnimation("right");
    }

    @Override
    public void onBackPressed() {
            super.setAnimation("left");
            super.onBackPressed();
    }


    public void finish(View v) {
        settingsUser.setOnboardingDone();
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
        finish();
    }
}
