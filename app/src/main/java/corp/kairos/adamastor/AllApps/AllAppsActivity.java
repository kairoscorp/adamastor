package corp.kairos.adamastor.AllApps;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allapps);

        this.searchView = findViewById(R.id.search_view);
        this.allAppsMenuView = findViewById(R.id.allapps_grid);

        // Managers
        this.appsManager = AppsManager.getInstance();
        this.packageManager = getPackageManager();

        this.allApps = this.appsManager.getAllApps(packageManager, true);

        // Set animations
        super.setAnimation("up");
        super.setUpActivity(HomeActivity.class);

        // Setup views
        this.allAppsMenuView = findViewById(R.id.allapps_grid);
        this.allAppsMenuView.setLayoutManager(new GridLayoutManager(this, NUMBER_OF_COLUMNS));
        AllAppsRecyclerViewAdapter adapter = new AllAppsRecyclerViewAdapter(AllAppsActivity.this, this.allApps);
        this.allAppsMenuView.setAdapter(adapter);

        this.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                adapter.getFilter().filter(query);
                return false;
            }
        });
    }

}
