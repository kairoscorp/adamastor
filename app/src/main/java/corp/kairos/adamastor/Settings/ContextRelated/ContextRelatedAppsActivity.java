package corp.kairos.adamastor.Settings.ContextRelated;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import java.util.Set;
import corp.kairos.adamastor.AppDetail;
import corp.kairos.adamastor.R;
import corp.kairos.adamastor.Settings.Settings;
import corp.kairos.adamastor.UserContext;

public class ContextRelatedAppsActivity extends Activity {

    private ListView checkAppsMenuView;
    private AppCheckAdapter adapter;
    public Context context = this;

    private Settings sets;
    private Set<AppDetail> allApps;
    private UserContext uc;
    private String ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apps_onboarding);

        loadSettings(savedInstanceState);
        loadListView();
        checkSaveButtonClick();
    }

    private void loadSettings(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            this.ctx = extras.getString("CONTEXT");
        } else {
            this.ctx = (String) savedInstanceState.getSerializable("CONTEXT");
        }
        this.sets = new Settings(this);
        this.allApps = sets.getAllApps();
        this.uc = sets.getUserContext(ctx);
    }

    private void loadListView() {
        this.checkAppsMenuView = findViewById(R.id.check_apps_list);
        adapter = new AppCheckAdapter(this, allApps, uc);
        this.checkAppsMenuView.setAdapter(adapter);
    }

    private void checkSaveButtonClick() {
        Button myButton = findViewById(R.id.btn_save_check_apps);
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sets.saveContextSettings(ctx);
                finish();
            }
        });
    }

}
