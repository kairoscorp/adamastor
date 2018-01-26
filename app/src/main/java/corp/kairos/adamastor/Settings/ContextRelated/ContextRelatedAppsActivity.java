package corp.kairos.adamastor.Settings.ContextRelated;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.Set;

import corp.kairos.adamastor.AppDetails;
import corp.kairos.adamastor.AppsManager.AppsManager;
import corp.kairos.adamastor.R;
import corp.kairos.adamastor.Settings.Settings;
import corp.kairos.adamastor.UserContext;

public class ContextRelatedAppsActivity extends Activity {

    private ListView checkAppsMenuView;
    private AppCheckAdapter adapter;
    public Context context = this;
    private AppsManager appsManager;

    private Settings settingsUser;
    private Set<AppDetails> allApps;
    private UserContext uc;
    private String ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.appsManager = AppsManager.getInstance();

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
        this.settingsUser = Settings.getInstance(this);
        this.allApps = this.appsManager.getAllApps(this.getPackageManager(), true);
        this.uc = settingsUser.getUserContext(ctx);
    }

    private void loadListView() {
        this.checkAppsMenuView = findViewById(R.id.check_apps_list);
        adapter = new AppCheckAdapter(this, allApps, uc);
        this.checkAppsMenuView.setAdapter(adapter);
    }

    private void checkSaveButtonClick() {
        Button myButton = findViewById(R.id.btn_save_check_apps);
        myButton.setOnClickListener(v -> {
            settingsUser.saveContextSettings();
            finish();
        });
    }

}
