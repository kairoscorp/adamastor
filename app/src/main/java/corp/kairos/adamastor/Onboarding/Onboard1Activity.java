package corp.kairos.adamastor.Onboarding;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import corp.kairos.adamastor.R;
import corp.kairos.adamastor.Settings.Settings;


public class Onboard1Activity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboard1);
        new Settings(this).resetSettings();
    }

    public void start(View v) {
        Intent i = new Intent(this,Onboard2Activity.class);
        startActivity(i);
        finish();
    }
}
