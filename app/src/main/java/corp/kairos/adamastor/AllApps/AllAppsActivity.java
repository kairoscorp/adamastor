package corp.kairos.adamastor.AllApps;

import android.content.pm.PackageManager;
import android.os.Bundle;
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
        setContentView(R.layout.activity_allapps);

        this.allAppsMenuView = findViewById(R.id.allapps_menu);
        // Managers
        this.appsManager = AppsManager.getInstance();
        this.packageManager = getPackageManager();


        this.allApps = this.appsManager.getAllApps(packageManager, true);

        // Set animations
        super.setAnimation("up");
        super.setUpActivity(HomeActivity.class);

        // Load apps and views
        loadApps();
        loadListView();
    }

    private void loadApps(){
        this.allApps = this.appsManager.getAllApps(packageManager, true);
    }

    private void loadListView(){
        this.allAppsMenuView = findViewById(R.id.allapps_grid);
        AllAppsMenuAdapter adapter = new AllAppsMenuAdapter(this, this.allApps);
        this.allAppsMenuView.setAdapter(adapter);
    }

}
