package corp.kairos.adamastor.AllApps;


import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.Set;

import corp.kairos.adamastor.Animation.AnimationActivity;
import corp.kairos.adamastor.AppDetails;
import corp.kairos.adamastor.AppsManager.AppsManager;
import corp.kairos.adamastor.Home.HomeActivity;
import corp.kairos.adamastor.R;

public class AllAppsActivity extends AnimationActivity {
    private PackageManager packageManager;
    private AppsManager appsManager;

    private Set<AppDetails> allApps;

    private GridView allAppsMenuView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allapps_menu);

        this.allAppsMenuView = findViewById(R.id.allapps_menu);
        // Managers
        this.appsManager = AppsManager.getInstance();
        this.packageManager = getPackageManager();


        this.allApps = this.appsManager.getAllApps(packageManager, true);

        // Set animations
        super.setAnimation("up");
        super.setUpActivity(HomeActivity.class);

        // Views
        AllAppsMenuAdapter adapter = new AllAppsMenuAdapter(this, this.allApps);
        this.allAppsMenuView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            if (getFragmentManager().findFragmentByTag("OPTIONS") != null) {
                getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag("OPTIONS")).commit();
            }
            if (getFragmentManager().findFragmentByTag("CONTEXT") != null) {
                getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag("CONTEXT")).commit();
            }
            if (findViewById(R.id.select_context) != null) {
                ((ViewGroup) findViewById(R.id.select_context).getParent()).removeView(findViewById(R.id.select_context));
                getWindow().setStatusBarColor(0);
                getWindow().setNavigationBarColor(0);
            }

        } else {
            super.onBackPressed();
        }
    }
}
