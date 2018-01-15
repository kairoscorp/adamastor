package corp.kairos.adamastor.Onboarding;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;

import corp.kairos.adamastor.AppDetail;
import corp.kairos.adamastor.R;
import corp.kairos.adamastor.Settings.Onboarding.AppCheckAdapter;
import corp.kairos.adamastor.Settings.Settings;
import corp.kairos.adamastor.UserContext;


public class Onboard3Activity extends AppCompatActivity {
    private ListView checkAppsMenuView;
    private AppCheckAdapter adapter;
    public Context context = this;

    private Settings sets;
    private Set<AppDetail> allApps;
    public Set<String> contexts;
    private UserContext uc;
    private String ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboard3);
        findViewById(R.id.next3).setVisibility(View.INVISIBLE);
        loadSettings(savedInstanceState);
        loadListView();
    }

    private void loadSettings(Bundle savedInstanceState) {
        Object[] array;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();

            array = (Object[]) extras.get("CONTEXTS");
        } else {
            array = (Object[]) savedInstanceState.get("CONTEXTS");
        }
        Set<String> ss = new HashSet<>();
        for (int i=0;  i < (array.length); i++ ) {
            ss.add((String)array[i]);
        }
        this.sets = new Settings(this);
        this.contexts = ss;
        this.allApps = sets.getAllApps();
        this.ctx = contexts.iterator().next();
        this.contexts.remove(ctx);
        this.uc = sets.getUserContext(ctx);

        ((TextView)findViewById(R.id.description)).setText("Please select the apps related to your "+ctx+" context");
    }

    private void loadListView() {
        this.checkAppsMenuView = findViewById(R.id.check_apps_list);
        adapter = new AppCheckAdapter(this, allApps, uc);
        this.checkAppsMenuView.setAdapter(adapter);
    }

    public void goNext(View v) {
        sets.saveContextSettings(ctx);
        if(contexts.size() > 0) {
            Intent i = new Intent(this,Onboard3Activity.class);
            i.putExtra("CONTEXTS", contexts.toArray());
            startActivity(i);
        }
        else {
            Intent i = new Intent(this,Onboard4Activity.class);
            startActivity(i);
        }
        finish();
    }

}
