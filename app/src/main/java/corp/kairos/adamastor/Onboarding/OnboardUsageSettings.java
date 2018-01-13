package corp.kairos.adamastor.Onboarding;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import corp.kairos.adamastor.R;
import corp.kairos.adamastor.Settings.Settings;

/**
 * Created by jlsilva94 on 13/01/18.
 */

public class OnboardUsageSettings extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboard_usage_sets);
    }


    public void showUsageSettings(View v){
        startActivityForResult(new Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS), 1);
        findViewById(R.id.next).setVisibility(View.VISIBLE);
    }

    public void goNext(View v) {
        startActivity(new Intent(this,Onboard2Activity.class));
        finish();
    }
}
