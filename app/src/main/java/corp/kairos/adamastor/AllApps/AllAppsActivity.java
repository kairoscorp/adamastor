package corp.kairos.adamastor.AllApps;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.SearchView;

import java.util.Set;

import corp.kairos.adamastor.Animation.AnimationActivity;
import corp.kairos.adamastor.AppDetails;
import corp.kairos.adamastor.AppsManager.AppsManager;
import corp.kairos.adamastor.Home.HomeActivity;
import corp.kairos.adamastor.R;

import static corp.kairos.adamastor.ContextList.ContextListActivity.NUMBER_OF_COLUMNS;

public class AllAppsActivity extends AnimationActivity {
    private PackageManager packageManager;
    private AppsManager appsManager;

    private Set<AppDetails> allApps;

    private SearchView searchView;
    private RecyclerView allAppsMenuView;
    private AllAppsRecyclerViewAdapter mAdapter;
    static public int back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allapps);

        searchView = findViewById(R.id.search_view);
        allAppsMenuView = findViewById(R.id.allapps_grid);

        // Managers
        appsManager = AppsManager.getInstance();
        packageManager = getPackageManager();

        allApps = appsManager.getAllApps(packageManager, true);

        // Set animations
        super.setAnimation("up");
        super.setUpActivity(HomeActivity.class);

        // Setup views
        allAppsMenuView = findViewById(R.id.allapps_grid);
        allAppsMenuView.setLayoutManager(new GridLayoutManager(AllAppsActivity.this, NUMBER_OF_COLUMNS));
        mAdapter = new AllAppsRecyclerViewAdapter(AllAppsActivity.this, allApps);
        allAppsMenuView.setAdapter(mAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                mAdapter.getFilter().filter(query);
                allAppsMenuView.scrollToPosition(0);
                return false;
            }
        });
        back=0;
    }
    public void contextFragment(){
        back++;
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            if (back == 1) {
                if (getFragmentManager().findFragmentByTag("OPTIONS") != null) {
                    HomeActivity.hideSetContext(this);
                }
                if (getFragmentManager().findFragmentByTag("CONTEXT") != null) {
                    getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag("CONTEXT")).commit();
                }
                if (findViewById(R.id.select_context) != null) {
                    ((ViewGroup) findViewById(R.id.select_context).getParent()).removeView(findViewById(R.id.select_context));
                }
                back--;
            } else {
                finish();
            }
            getWindow().setStatusBarColor(0);
            getWindow().setNavigationBarColor(0);

        } else {
            super.onBackPressed();
        }
    }
}
