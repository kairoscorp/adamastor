package corp.kairos.adamastor.AllApps;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.GridView;

import java.util.Set;

import corp.kairos.adamastor.AnimActivity;
import corp.kairos.adamastor.AppDetail;
import corp.kairos.adamastor.AppDetailComparator;
import corp.kairos.adamastor.ContextList.ContextListActivity;
import corp.kairos.adamastor.AppsManager.AppsManager;
import corp.kairos.adamastor.Home.HomeActivity;
import corp.kairos.adamastor.R;

import static corp.kairos.adamastor.Util.getObjectByIndex;

public class AllAppsActivity extends AnimActivity {
    private PackageManager packageManager;
    private Set<AppDetail> allApps;
    private GridView allAppsMenuView;
    private AppsManager appsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allapps_menu);

        this.appsManager = AppsManager.getInstance();
        this.setLeftActivity(HomeActivity.class);

        loadApps();
        loadListView();
        addClickListener();
    }

    private void loadApps(){
        packageManager = getPackageManager();
        this.allApps = this.appsManager.getAllApps(packageManager, true);
    }

    private void loadListView(){
        this.allAppsMenuView = findViewById(R.id.allapps_menu);
        AllAppsMenuAdapter adapter = new AllAppsMenuAdapter(this, this.allApps);
        this.allAppsMenuView.setAdapter(adapter);
    }

    private void addClickListener(){
        this.allAppsMenuView.setOnItemClickListener((av, v, pos, id) -> {
            AppDetail app = (AppDetail) getObjectByIndex(pos, allApps);
            Intent i = packageManager.getLaunchIntentForPackage(app.getName());
            AllAppsActivity.this.startActivity(i);
        });
    }
}
