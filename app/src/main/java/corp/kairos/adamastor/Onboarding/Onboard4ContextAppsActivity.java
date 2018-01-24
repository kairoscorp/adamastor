package corp.kairos.adamastor.Onboarding;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
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
    private FloatingActionButton next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboard4_context_apps);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        this.appsManager = AppsManager.getInstance();
        this.settingsUser = Settings.getInstance(this);
        this.contextsIterator = settingsUser.getContextNames().iterator();
        this.allApps = appsManager.getAllApps(getPackageManager(), false);
        this.next = findViewById(R.id.id_add_app_contexts_next);
        loadSettings();
    }

    private void loadSettings() {
        this.atualContextName = contextsIterator.next();
        this.userContext = settingsUser.getUserContext(atualContextName);


        ImageView image = ((ImageView)findViewById(R.id.id_add_app_contexts_image));
        TextView label = ((TextView)findViewById(R.id.id_add_app_contexts_label));
        TextView description = ((TextView)findViewById(R.id.id_add_app_contexts_description));
        switch (userContext.getContextName()) {
            case "Home":
                image.setImageDrawable(getDrawable(R.drawable.ic_home_white));
                label.setText(R.string.home_name);
                description.setText(R.string.add_app_contexts_description_home);
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
        this.checkAppsMenuView.setOnScrollListener(new onScroll());
        adapter = new AppCheckAdapter(this, allApps, userContext);
        this.checkAppsMenuView.setAdapter(adapter);
    }

    public void goNext(View v) {
        settingsUser.setUserContext(userContext);
        Log.i("INFO", userContext.toString());
        if(contextsIterator.hasNext()) {
            loadSettings();
        } else {
            Intent i = new Intent(this,Onboard5ScheduleActivity.class);
            startActivity(i);
            finish();
        }
    }

    public class onScroll implements AbsListView.OnScrollListener {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {}

        private int lastVisibleItem = 0;
        private int lastY = 0;

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            int top = 0;
            if(view.getChildAt(0) != null){
                top = view.getChildAt(0).getTop();
            }

            if(firstVisibleItem > lastVisibleItem){
                //scroll down
                next.hide();
            }else if(firstVisibleItem < lastVisibleItem){
                //scroll up
                next.show();
            }else{
                if(top < lastY){
                    //scroll down
                    next.hide();
                }else if(top > lastY){
                    //scroll up
                    next.show();
                }
            }

            lastVisibleItem = firstVisibleItem;
            lastY = top;
        }
    }
}
