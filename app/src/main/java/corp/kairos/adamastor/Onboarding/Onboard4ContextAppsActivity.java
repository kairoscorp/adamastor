package corp.kairos.adamastor.Onboarding;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;
import java.util.Set;

import corp.kairos.adamastor.AppDetail;
import corp.kairos.adamastor.R;
import corp.kairos.adamastor.Settings.ContextRelated.AppCheckAdapter;
import corp.kairos.adamastor.Settings.Settings;
import corp.kairos.adamastor.UserContext;


public class Onboard4ContextAppsActivity extends AppCompatActivity {
    private ListView checkAppsMenuView;
    private AppCheckAdapter adapter;
    public Context context = this;

    private Settings sets;
    private Set<AppDetail> allApps;
    private List<String> contexts;
    private UserContext uc;
    private String ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboard4_context_apps);
        this.sets = new Settings(this);
        this.contexts = sets.getContextNames();
        this.allApps = sets.getAllApps();
        loadSettings();
    }

    private void loadSettings() {
        this.ctx = contexts.listIterator().next();
        this.uc = sets.getUserContext(ctx);

        ((TextView)findViewById(R.id.description)).setText("Please select the apps related to your "+ctx+" context");
        findViewById(R.id.next3).setVisibility(View.INVISIBLE);
        loadListView();
        contexts.remove(ctx);
    }

    private void loadListView() {
        this.checkAppsMenuView = findViewById(R.id.check_apps_list);
        adapter = new AppCheckAdapter(this, allApps, uc);
        this.checkAppsMenuView.setAdapter(adapter);
    }

    public void goNext(View v) {
        sets.saveContextSettings(ctx);
        if(contexts.listIterator().hasNext()) {
            loadSettings();
        }
        else {
            Intent i = new Intent(this,Onboard5ScheduleActivity.class);
            startActivity(i);
            finish();
        }
    }

}
