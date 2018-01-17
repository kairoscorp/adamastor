package corp.kairos.adamastor.AllApps;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import corp.kairos.adamastor.AnimActivity;
import corp.kairos.adamastor.AppDetail;
import corp.kairos.adamastor.Home.HomeActivity;
import corp.kairos.adamastor.R;

import static corp.kairos.adamastor.Util.getObjectByIndex;

public class AllAppsActivity extends AnimActivity {
    private PackageManager packageManager;
    private Set<AppDetail> allApps;
    private GridView allAppsMenuView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allapps_menu);

        this.setLeftActivity(HomeActivity.class);

        loadApps();
        loadListView();
        addClickListener();
    }

    public static Set<AppDetail> getAllApps(PackageManager packageManager, boolean withLaunchers) {
        Map<String, AppDetail> allApps = new TreeMap<>();
        Intent i = new Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> availableActivities = packageManager.queryIntentActivities(i, 0);
        for(ResolveInfo ri:availableActivities){
            String label = ri.loadLabel(packageManager).toString();
            String name = ri.activityInfo.packageName.toString();
            Drawable icon = ri.activityInfo.loadIcon(packageManager);
            AppDetail app = new AppDetail(label, name, icon);
            allApps.put(name, app);
        }

        if(!withLaunchers) {
            Set<String> launchersNames = getLauncherNames(packageManager);
            for(String launcher: launchersNames) {
                allApps.remove(launcher);
            }
        }

        return new TreeSet<>(allApps.values());
    }

    private static Set<String> getLauncherNames(PackageManager packageManager) {
        // Get list of installed launchers
        Intent i = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> launchersInfo= packageManager.queryIntentActivities(i, 0);
        Set<String> launchersList = new TreeSet<>();
        for(ResolveInfo info: launchersInfo) {
            launchersList.add(info.activityInfo.packageName);
        }
        return launchersList;
    }

    private void loadApps(){
        this.packageManager = getPackageManager();
        this.allApps = this.getAllApps(this.packageManager, true);
    }

    private void loadListView(){
        this.allAppsMenuView = findViewById(R.id.allapps_menu);
        AllAppsMenuAdapter adapter = new AllAppsMenuAdapter(this, this.allApps);
        this.allAppsMenuView.setAdapter(adapter);
    }

    private void addClickListener(){
        this.allAppsMenuView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos,
                                    long id) {
                AppDetail app = (AppDetail) getObjectByIndex(pos, allApps);
                Intent i = packageManager.getLaunchIntentForPackage(app.getName());
                AllAppsActivity.this.startActivity(i);
            }
        });
    }
}
