package corp.kairos.adamastor.Onboarding;

import android.app.AppOpsManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import corp.kairos.adamastor.Animation.AnimationCompatActivity;
import corp.kairos.adamastor.R;


public class Onboard2SpecialPermissionActivity extends AnimationCompatActivity {

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
        super.setAnimation("right");
        AppOpsManager appOps = (AppOpsManager) this.getSystemService(this.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), this.getPackageName());
        if(mode == AppOpsManager.MODE_ALLOWED) {
            startActivity(new Intent(this,Onboard3LocationActivity.class));
            finish();
        }
    }

    public void showUsageSettings(View v){
        startActivityForResult(new Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS), 1);
    }
    
}
