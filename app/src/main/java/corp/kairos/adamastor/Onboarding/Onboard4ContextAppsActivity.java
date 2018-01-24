package corp.kairos.adamastor.Onboarding;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Iterator;
import java.util.Set;

import corp.kairos.adamastor.AppDetails;
import corp.kairos.adamastor.AppsManager.AppsManager;
import corp.kairos.adamastor.R;
import corp.kairos.adamastor.Settings.ContextRelated.AppCheckAdapter;
import corp.kairos.adamastor.Settings.Settings;
import corp.kairos.adamastor.UserContext;


public class Onboard4ContextAppsActivity extends AppCompatActivity {
    private ListView checkAppsMenuView;
    private AppCheckAdapter adapter;
    public Context context = this;
    AppsManager appsManager;

    private Settings settingsUser;
    private Set<AppDetails> allApps;
    private Iterator<String> contextsIterator;
    private UserContext userContext;
    private String atualContextName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboard4_context_apps);
        this.appsManager = AppsManager.getInstance();
        this.settingsUser = Settings.getInstance(this);
        this.contextsIterator = settingsUser.getContextNames().iterator();
        this.allApps = appsManager.getAllApps(getPackageManager(), false);
        loadSettings();
    }

    private void loadSettings() {
        this.atualContextName = contextsIterator.next();
        this.userContext = settingsUser.getUserContext(atualContextName);

        ((TextView)findViewById(R.id.description)).setText("Please select the apps related to your "+atualContextName+" context");
        findViewById(R.id.next3).setVisibility(View.INVISIBLE);
        loadListView();
    }

    private void loadListView() {
        this.checkAppsMenuView = findViewById(R.id.check_apps_list);
        adapter = new AppCheckAdapter(this, allApps, userContext);
        this.checkAppsMenuView.setAdapter(adapter);
    }

    public void goNext(View v) {
        settingsUser.setUserContext(userContext);
        if(contextsIterator.hasNext()) {
            loadSettings();
        } else {
            Intent i = new Intent(this,Onboard5ScheduleActivity.class);
            startActivity(i);
            finish();
        }
    }
}
