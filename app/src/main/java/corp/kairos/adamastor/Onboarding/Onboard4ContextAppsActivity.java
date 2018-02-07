package corp.kairos.adamastor.Onboarding;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Iterator;
import java.util.Set;

import corp.kairos.adamastor.Animation.AnimationCompactActivity;
import corp.kairos.adamastor.AppDetails;
import corp.kairos.adamastor.AppsManager.AppsManager;
import corp.kairos.adamastor.R;
import corp.kairos.adamastor.Settings.ContextRelated.AppCheckAdapter;
import corp.kairos.adamastor.Settings.Settings;
import corp.kairos.adamastor.UserContext;
import corp.kairos.adamastor.onScroll;


public class Onboard4ContextAppsActivity extends AnimationCompactActivity {
    private ListView checkAppsMenuView;
    private AppCheckAdapter adapter;
    public Context context = this;
    AppsManager appsManager;

    private Settings settingsUser;
    private Set<AppDetails> allApps;
    private Iterator<String> contextsIterator;
    private UserContext userContext;
    private String atualContextName;
    private FloatingActionButton next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboard4_context_apps);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        this.appsManager = AppsManager.getInstance();
        this.settingsUser = Settings.getInstance(this);
        this.allApps = appsManager.getAllApps(getPackageManager(), false);
        this.next = findViewById(R.id.id_add_app_contexts_next);
        this.atualContextName = "Work";
        loadSettings();
    }

    @Override
    protected void onResume() {
        super.onResume();
        super.setAnimation("right");
    }

    @Override
    public void onBackPressed() {
        switch (atualContextName) {
            case "Work":
                super.setAnimation("left");
                super.onBackPressed();
                break;
            case "Home":
                atualContextName = "Work";
                animationLeft();
                loadSettings();
                break;
            case "Commute":
                atualContextName = "Home";
                animationLeft();
                loadSettings();
                break;
            default:
                break;
        }
    }

    private void loadSettings() {
        this.userContext = settingsUser.getUserContext(atualContextName);

        ImageView image = ((ImageView)findViewById(R.id.id_add_app_contexts_image));
        TextView label = ((TextView)findViewById(R.id.id_add_app_contexts_label));
        TextView description = ((TextView)findViewById(R.id.id_add_app_contexts_description));
        switch (atualContextName) {
            case "Home":
                image.setImageDrawable(getDrawable(R.drawable.ic_leisure_white));
                label.setText(R.string.leisure_name);
                description.setText(R.string.add_app_contexts_description_leisure);
                break;

            case "Work":
                image.setImageDrawable(getDrawable(R.drawable.ic_work_white));
                label.setText(R.string.work_name);
                description.setText(R.string.add_app_contexts_description_work);
                break;

            case "Commute":
                image.setImageDrawable(getDrawable(R.drawable.ic_commute_white));
                label.setText(R.string.commute_name);
                description.setText(R.string.add_app_contexts_description_commute);
                break;

            default:

        }
        loadListView();
    }

    private void loadListView() {
        this.checkAppsMenuView = findViewById(R.id.check_apps_list);
        this.checkAppsMenuView.setOnScrollListener(new onScroll(next));
        adapter = new AppCheckAdapter(this, allApps, userContext);
        this.checkAppsMenuView.setAdapter(adapter);
    }

    private void animationLeft() {
        checkAppsMenuView.startAnimation(AnimationUtils.loadAnimation(this,R.anim.slide_from_left));

    }
    private void animationRight() {
        checkAppsMenuView.startAnimation(AnimationUtils.loadAnimation(this,R.anim.slide_from_right));
    }

    public void goNext(View v) {
        switch (atualContextName) {
            case "Work":
                atualContextName = "Home";
                animationRight();
                loadSettings();
                break;
            case "Home":
                atualContextName = "Commute";
                animationRight();
                loadSettings();
                break;
            case "Commute":
                Intent i = new Intent(this,Onboard5ScheduleActivity.class);
                startActivity(i);
                break;
            default:
                break;
        }
    }

}
