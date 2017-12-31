package corp.kairos.adamastor.Onboarding;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import corp.kairos.adamastor.R;

public class WorkRelatedAppsActivity extends Activity {

    private PackageManager packageManager;
    private Set<AppCheckDetail> allApps;
    private ListView checkAppsMenuView;
    private AppCheckAdapter adapter;
    public Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apps_onboarding);
        loadApps();
        loadListView();
        checkSaveButtonClick();
    }

    private void loadApps(){
        this.packageManager = getPackageManager();
        this.allApps = new TreeSet<AppCheckDetail>(new AppCheckDetailComparator());

        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> availableActivities = this.packageManager.queryIntentActivities(i, 0);
        for(ResolveInfo ri:availableActivities){
            String label = ri.loadLabel(this.packageManager).toString();
            String name = ri.activityInfo.packageName.toString();
            Drawable icon = ri.activityInfo.loadIcon(this.packageManager);
            AppCheckDetail app = new AppCheckDetail(label, name, icon);
            this.allApps.add(app);
        }
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(
                getString(R.string.prefsfile_workApps), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        for (AppCheckDetail app: this.allApps) {
            Boolean checked = sharedPref.getBoolean(app.getName(),false);
            app.setSelected(checked);
        }
    }

    private void loadListView(){
        this.checkAppsMenuView = findViewById(R.id.check_apps_list);
        adapter = new AppCheckAdapter(this, this.allApps);
        this.checkAppsMenuView.setAdapter(adapter);
    }

    private void checkSaveButtonClick() {

        Button myButton = findViewById(R.id.btn_save_check_apps);
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(
                        getString(R.string.prefsfile_workApps), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();

                for (AppCheckDetail app : adapter.apps) {
                    editor.putBoolean(app.getName(),app.isSelected());
                    editor.apply();
                    if (app.isSelected()) {
                        Log.println(Log.INFO,"PREFS",app.label + " is now selected");
                    }
                    }

                Intent i = new Intent(context, OnboardingActivity.class);
                startActivity(i);
            }
        });
    }

}
