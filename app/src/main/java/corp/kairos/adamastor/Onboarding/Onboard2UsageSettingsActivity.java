package corp.kairos.adamastor.Onboarding;

import android.app.AppOpsManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import corp.kairos.adamastor.R;


public class Onboard2UsageSettingsActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboard2_usage_sets);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppOpsManager appOps = (AppOpsManager) this.getSystemService(this.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), this.getPackageName());
        if(mode == AppOpsManager.MODE_ALLOWED) {
            findViewById(R.id.next).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_ok).setVisibility(View.GONE);
            findViewById(R.id.img_check).setVisibility(View.VISIBLE);
        }
    }

    public void showUsageSettings(View v){
        startActivityForResult(new Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS), 1);
    }

    public void goNext(View v) {
        startActivity(new Intent(this,Onboard3LocationActivity.class));
        finish();
    }
}
