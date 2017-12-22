package corp.kairos.adamastor.AllApps;

import android.app.Activity;
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
import java.util.Set;
import java.util.TreeSet;

import corp.kairos.adamastor.R;

import static corp.kairos.adamastor.Util.getObjectByIndex;


public class AllAppsActivity extends Activity {
    private PackageManager packageManager;
    private Set<AppDetail> allApps;
    private GridView allAppsMenuView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allapps_menu);

        loadApps();
        loadListView();
        addClickListener();
    }

    private void loadApps(){
        this.packageManager = getPackageManager();
        this.allApps = new TreeSet<>(new AppDetailComparator());

        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> availableActivities = this.packageManager.queryIntentActivities(i, 0);
        for(ResolveInfo ri:availableActivities){
            String label = ri.loadLabel(this.packageManager).toString();
            String name = ri.activityInfo.packageName.toString();
            Drawable icon = ri.activityInfo.loadIcon(this.packageManager);
            AppDetail app = new AppDetail(label, name, icon);
            this.allApps.add(app);
        }
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
