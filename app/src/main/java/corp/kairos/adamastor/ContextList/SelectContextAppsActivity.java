package corp.kairos.adamastor.ContextList;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.Iterator;
import java.util.Set;

import corp.kairos.adamastor.AppDetails;
import corp.kairos.adamastor.AppsManager.AppsManager;
import corp.kairos.adamastor.Onboarding.Onboard4ContextAppsActivity;
import corp.kairos.adamastor.Onboarding.Onboard5ScheduleActivity;
import corp.kairos.adamastor.R;
import corp.kairos.adamastor.Settings.ContextRelated.AppCheckAdapter;
import corp.kairos.adamastor.Settings.Settings;
import corp.kairos.adamastor.UserContext;
import corp.kairos.adamastor.onScroll;

/**
 * Created by jlsilva94 on 29/01/18.
 */

public class SelectContextAppsActivity extends AppCompatActivity{
        private ListView checkAppsMenuView;
        private AppCheckAdapter adapter;
        public Context context = this;
        AppsManager appsManager;

        private Settings settingsUser;
        private Set<AppDetails> allApps;
        private UserContext userContext;
        private FloatingActionButton save;
        private TabHost tabHost;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.onboard4_context_apps);
            //getSupportActionBar().setDisplayHomeAsUpEnabled(false);

            this.appsManager = AppsManager.getInstance();
            this.settingsUser = Settings.getInstance(this);
            this.allApps = appsManager.getAllApps(getPackageManager(), false);
            this.save = findViewById(R.id.btn_save_contexts);
            loadSettings();
            loadTabViewSettings();
        }

    public void loadTabViewSettings() {
        this.tabHost = findViewById(R.id.TabHost);
        tabHost.setup();
        addTab("Work",getDrawable(R.drawable.ic_work_white),this.getClass());
        addTab("Leisure",getDrawable(R.drawable.ic_leisure_white),this.getClass());
        addTab("Commute",getDrawable(R.drawable.ic_commute_white),this.getClass());
    }

        private void loadSettings() {

            loadListView();
        }

        private void loadListView() {
            this.checkAppsMenuView = findViewById(R.id.check_apps_list);
            this.checkAppsMenuView.setOnScrollListener(new onScroll(save));
            adapter = new AppCheckAdapter(this, allApps, userContext);
            this.checkAppsMenuView.setAdapter(adapter);
        }

    private void addTab(String labelId, Drawable drawable, Class<?> c) {

        Intent intent = new Intent(this, c);
        TabHost.TabSpec spec = tabHost.newTabSpec("tab" + labelId);

        View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tab_indicator, tabHost.getTabWidget(), false);
        TextView title = (TextView) tabIndicator.findViewById(R.id.title);
        title.setText(labelId);

        ImageView icon = (ImageView) tabIndicator.findViewById(R.id.icon);
        icon.setImageDrawable(drawable);
        icon.setScaleType(ImageView.ScaleType.FIT_CENTER);

        spec.setIndicator(tabIndicator);
        spec.setContent(intent);
        tabHost.addTab(spec);
    }

        public void save(View v) {
            settingsUser.setUserContext(userContext);

        }
}

