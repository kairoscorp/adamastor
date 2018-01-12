package corp.kairos.adamastor.Statistics;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import corp.kairos.adamastor.AllApps.AllAppsMenuAdapter;
import corp.kairos.adamastor.AppDetail;
import corp.kairos.adamastor.R;
import static corp.kairos.adamastor.Util.getObjectByIndex;


public class StatisticsActivity extends AppCompatActivity {

    private PackageManager packageManager;
    private View mFromView;
    private View mToView;
    private FloatingActionButton mFab;
    private boolean isShowingList = true;

    private int mShortAnimationDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.packageManager = this.getPackageManager();
        setContentView(R.layout.activity_statistics);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mToView = findViewById(R.id.stats_context);
        mFromView = findViewById(R.id.stats_apps_list);
        mFab = (FloatingActionButton) findViewById(R.id.fab);

        // Initially hide the content view.
        mToView.setVisibility(View.GONE);

        // Retrieve and cache the system's default "short" animation time.
        mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crossFade();
            }
        });

        // Load Application Statistics View
        Set<AppDetailStats> appsStats = getAppsStatistics();
        loadListView(appsStats);
    }

    private Set<AppDetailStats> getAppsStatistics() {
        Map<String, AppDetailStats> appsStats = new TreeMap<>();
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,  time - 10000000, time);
            if (appList != null && appList.size() > 0) {
                for (UsageStats usageStats : appList) {
                    try {
                        ApplicationInfo appInfo = packageManager.getApplicationInfo(usageStats.getPackageName(), PackageManager.GET_META_DATA);
                        String appName = packageManager.getApplicationLabel(appInfo).toString();
                        String appLabel = usageStats.getPackageName();
                        Drawable appIcon = packageManager.getApplicationIcon(appInfo);
                        Long appTotalTime = usageStats.getTotalTimeInForeground();
                        AppDetailStats appDetailStats = new AppDetailStats(appName, appLabel, appIcon, appTotalTime);
                        if(appTotalTime > 0) {
                            if(appsStats.containsKey(appLabel)) {
                                appDetailStats.setTotalTime(appTotalTime + appsStats.get(appLabel).getTotalTime());
                            }
                            appsStats.put(appLabel, appDetailStats);
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        // This should never happen
                        Log.e("STATS", "Unknown package name");
                    }
                }
            }
        }

        return new TreeSet<>(appsStats.values());
    }

    private void getContextStatistics(String context) {


    }

    private void crossFade() {
        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        mToView.setAlpha(0f);
        mToView.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        mToView.animate()
                .alpha(1f)
                .setDuration(mShortAnimationDuration)
                .setListener(null);

        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
        mFromView.animate()
                .alpha(0f)
                .setDuration(mShortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mFromView.setVisibility(View.GONE);

                        // Swap the views
                        View temp = mToView;
                        mToView = mFromView;
                        mFromView = temp;
                        if(isShowingList) {
                            mFab.setImageResource(R.drawable.ic_list);
                        } else {
                            mFab.setImageResource(R.drawable.ic_graph);
                        }
                        isShowingList = !isShowingList;
                    }
                });
    }

    private void loadListView(Set<AppDetailStats> appsStats){
        ListView list = (ListView)findViewById(R.id.stats_apps_list);
        StatisticsAppsMenuAdapter adapter = new StatisticsAppsMenuAdapter(this, appsStats);
        list.setAdapter(adapter);
    }
}
