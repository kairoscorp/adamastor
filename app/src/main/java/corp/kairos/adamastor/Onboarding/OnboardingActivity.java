package corp.kairos.adamastor.Onboarding;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import corp.kairos.adamastor.HomeActivity;
import corp.kairos.adamastor.R;

public class OnboardingActivity extends AppCompatActivity {

    GoogleMap workMap;
    GoogleMap homeMap;
    MapView workplaceView;
    MapView homeplaceView;

    private Date workFrom = new Date();
    private Date workTo = new Date();

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboarding);

        // Relacionado com os mapas
        //TODO: Por os mapas a funcionar corretamente
        workplaceView = findViewById(R.id.mapWork);
        workplaceView.onCreate(savedInstanceState);

        homeplaceView = findViewById(R.id.mapHome);
        homeplaceView.onCreate(savedInstanceState);

        workplaceView.getMapAsync(new OnMapReadyCallback() {
            public void onMapReady(GoogleMap workMap) {
                workMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        });

        loadWorkTimeSettings();
    }

    public void ShowWorkRelatedApps(View v){
        saveTimeSettings();
        Intent i = new Intent(this, WorkRelatedAppsActivity.class);
        startActivity(i);
    }

    public void ShowLeisureApps(View v){
        saveTimeSettings();
        Intent i = new Intent(this, LeisureAppsActivity.class);
        startActivity(i);
    }

    public void loadWorkTimeSettings() {
        SharedPreferences sharedPref = this.getSharedPreferences("OnboardingPrefs", Context.MODE_PRIVATE);

        this.workFrom.setTime(sharedPref.getLong("workFrom",0));
        this.workTo.setTime(sharedPref.getLong("workTo",0));

        EditText timeFrom = findViewById(R.id.work_from);
        EditText timeTo =  findViewById(R.id.work_to);

        DateFormat formatter = new SimpleDateFormat("HH:mm");

        timeFrom.setText(formatter.format(workFrom).toString());
        timeTo.setText(formatter.format(workTo).toString());


    }
    public void saveTimeSettings() {
        EditText timeFrom = findViewById(R.id.work_from);
        EditText timeTo =  findViewById(R.id.work_to);

        DateFormat formatter = new SimpleDateFormat("HH:mm");
        try {
            this.workFrom = formatter.parse(timeFrom.getText().toString());
            this.workTo = formatter.parse(timeTo.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Log.println(Log.INFO,"TIME FROM",timeFrom.getText().toString());
        //Log.println(Log.INFO,"TIME TO",timeTo.getText().toString());

        //Save to shared Preferences
        SharedPreferences sharedPref = this.getSharedPreferences("OnboardingPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putLong("workFrom",this.workFrom.getTime());
        editor.putLong("workTo",this.workTo.getTime());
        editor.apply();
    }

    public void saveChanges(View v)  {
        saveTimeSettings();
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
    }

}
