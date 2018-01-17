package corp.kairos.adamastor.AllApps;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.GridView;

import java.util.Set;

import corp.kairos.adamastor.Animation.AnimationActivity;
import corp.kairos.adamastor.AppDetails;
import corp.kairos.adamastor.AppsManager.AppsManager;
import corp.kairos.adamastor.Home.HomeActivity;
import corp.kairos.adamastor.R;

import static corp.kairos.adamastor.Util.getObjectByIndex;

public class AllAppsActivity extends AnimationActivity {
    private PackageManager packageManager;
    private AppsManager appsManager;

    private Set<AppDetails> allApps;

    private GridView allAppsMenuView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allapps_menu);

        // Managers
        this.appsManager = AppsManager.getInstance();
        this.packageManager = getPackageManager();

        // Set animations
        super.setAnimation("up");
        super.setUpActivity(HomeActivity.class);

        // Load apps and views
        loadApps();
        loadListView();
        addClickListener();
    }

    private void loadApps(){
        this.allApps = this.appsManager.getAllApps(packageManager, true);
    }

    private void loadListView(){
        this.allAppsMenuView = findViewById(R.id.allapps_menu);
        AllAppsMenuAdapter adapter = new AllAppsMenuAdapter(this, this.allApps);
        this.allAppsMenuView.setAdapter(adapter);
    }

    private void addClickListener(){
        this.allAppsMenuView.setOnItemClickListener((av, v, pos, id) -> {
            super.setAnimation("down");
            AppDetails app = (AppDetails) getObjectByIndex(pos, allApps);
            Intent i = packageManager.getLaunchIntentForPackage(app.getPackageName());
            startActivity(i);
            super.setAnimation("up");
        });
    }
}
