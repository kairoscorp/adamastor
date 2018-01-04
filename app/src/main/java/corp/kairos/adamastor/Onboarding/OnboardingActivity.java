package corp.kairos.adamastor.Onboarding;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

import corp.kairos.adamastor.R;

public class OnboardingActivity extends AppCompatActivity {

    GoogleMap workMap;
    GoogleMap homeMap;
    MapView workplaceView;
    MapView homeplaceView;

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

        workplaceView = findViewById(R.id.mapWork);
        workplaceView.onCreate(savedInstanceState);

        homeplaceView = findViewById(R.id.mapHome);
        homeplaceView.onCreate(savedInstanceState);

        workplaceView.getMapAsync(new OnMapReadyCallback() {
            public void onMapReady(GoogleMap workMap) {
                Toast.makeText(getApplicationContext(), "Funcionas ou quê?", Toast.LENGTH_SHORT).show();
                workMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            }
        });
    }
}
