package corp.kairos.adamastor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import corp.kairos.adamastor.AllApps.AllAppsActivity;
import corp.kairos.adamastor.R;

public class HomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public void showAllAppsMenu(View v){
        Intent i = new Intent(this, AllAppsActivity.class);
        startActivity(i);
    }
}