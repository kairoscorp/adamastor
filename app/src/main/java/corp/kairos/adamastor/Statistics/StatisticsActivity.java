package corp.kairos.adamastor.Statistics;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import corp.kairos.adamastor.AllApps.AllAppsActivity;
import corp.kairos.adamastor.AppDetail;
import corp.kairos.adamastor.Collector.CollectorService;
import corp.kairos.adamastor.R;

public class StatisticsActivity extends AppCompatActivity {

    private PackageManager packageManager;

    private View mFromView;
    private View mToView;
    private FloatingActionButton mFab;
    private int mShortAnimationDuration;
    private boolean isShowingList = true;

    private CollectorService collectorService;

    private static final String TAG = StatisticsActivity.class.getName();

    private Map<String, Long> contextStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.packageManager = this.getPackageManager();

        collectorService = CollectorService.getInstance();

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
        loadAppsStatistics();

        // Load Context Statistics View
        loadContextStatistics();
    }

    private void loadAppsStatistics() {
        Map<String, AppDetailStats> appsStats = new TreeMap<>();
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,  time - 10000000, time);
            if (appList != null && appList.size() > 0) {
                for (UsageStats usageStats : appList) {
                    try {
                        ApplicationInfo appInfo = packageManager.getApplicationInfo(usageStats.getPackageName(), PackageManager.GET_META_DATA);
                        String appName = usageStats.getPackageName();
                        String appLabel = packageManager.getApplicationLabel(appInfo).toString();
                        Drawable appIcon = packageManager.getApplicationIcon(appInfo);
                        Long appTotalTime = usageStats.getTotalTimeInForeground();
                        AppDetailStats appDetailStats = new AppDetailStats(appLabel, appName, appIcon, appTotalTime);
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


        // Get only the statistics of the installed apps
        Map<String, AppDetailStats> filteredAppsStats = new TreeMap<>();
        Set<AppDetail> allApps = AllAppsActivity.getAllApps(getPackageManager());
        for(AppDetail app: allApps) {
            if(appsStats.containsKey(app.getLabel())) {
                filteredAppsStats.put(app.getLabel(), appsStats.get(app.getLabel()));
            }
        }

        // Load view
        loadListView(new TreeSet<>(filteredAppsStats.values()));
    }

    private void loadContextStatistics() {
        contextStats = collectorService.getContextStatistics();

        // Load view
         loadGraphView();
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

    private void loadGraphView(){
        List<PieEntry> pieEntries = new ArrayList<>();
        long percentage, finalTotal = 0;
        for (Map.Entry<String, Long> stat : contextStats.entrySet()) {
            finalTotal += stat.getValue();
        }
        for (Map.Entry<String, Long> stat : contextStats.entrySet()) {
            percentage = (stat.getValue() * 100) / finalTotal;
            pieEntries.add(new PieEntry(TimeUnit.MILLISECONDS.toHours(stat.getValue()), stat.getKey() + " ("+percentage+"%)"));
        }

        PieDataSet dataSet = new PieDataSet(pieEntries, "");
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        // Data
        PieData pieData = new PieData(dataSet);
        pieData.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return Math.round(value) + " hours";
            }
        });
        pieData.setValueTextSize(12f);

        PieChart chart = (PieChart) findViewById(R.id.stats_context);
        Description description = new Description();
        description.setText("Applications usage by context.");
        chart.setDescription(description);
        chart.setCenterText("Usage Context");
        chart.setCenterTextColor(R.color.colorPrimary);
        chart.setCenterTextSize(17f);
        chart.animateY(1000);
        chart.setData(pieData);

        // undo all highlights
        chart.highlightValues(null);
        chart.invalidate();
    }
}
