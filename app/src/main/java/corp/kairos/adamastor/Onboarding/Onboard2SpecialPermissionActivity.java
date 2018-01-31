package corp.kairos.adamastor.Onboarding;

import android.app.AppOpsManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import corp.kairos.adamastor.R;


public class Onboard2SpecialPermissionActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboard2_special_permission);

        // Hide action bar back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppOpsManager appOps = (AppOpsManager) this.getSystemService(this.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), this.getPackageName());
        if(mode == AppOpsManager.MODE_ALLOWED) {
            goNext();
        }
    }

    public void showUsageSettings(View v){
        startActivityForResult(new Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS), 1);
    }

    public void goNext() {
        startActivity(new Intent(this,Onboard3LocationActivity.class));
    }
}
