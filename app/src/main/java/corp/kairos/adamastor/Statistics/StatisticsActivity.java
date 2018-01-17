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

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import corp.kairos.adamastor.AppDetail;
import corp.kairos.adamastor.AppsManager.AppsManager;
import corp.kairos.adamastor.Collector.CollectorService;
import corp.kairos.adamastor.R;

public class StatisticsActivity extends AppCompatActivity {

    private PackageManager packageManager;

    private View mFromView;
    private View mToView;
    private FloatingActionButton mFab;
    private int mShortAnimationDuration;
    private boolean isShowingList = true;
    private AppsManager appsManager;
    private int measure = 0; // 0 - HOURS | 1 - MINUTES | 2 - SECONDS | 3 - MILLISECONDS

    private CollectorService collectorService;

    private static final String TAG = StatisticsActivity.class.getName();

    private Map<String, Long> contextStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.packageManager = this.getPackageManager();
        this.appsManager = AppsManager.getInstance();

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
        fab.setOnClickListener(view -> crossFade());

        // Load Application Statistics View
        loadAppsStatistics();

        // Load Context Statistics View
        loadContextStatistics();
    }

    private void loadAppsStatistics() {
        Set<AppDetailStats> stats = this.appsManager.getAppsStatistics(packageManager, (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE));

        // Load view
        loadListView(stats);
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
        long timeInHours, timeInMinutes, timeInSeconds;
        for (Map.Entry<String, Long> stat : contextStats.entrySet()) {
            finalTotal += stat.getValue();
            timeInHours = TimeUnit.MILLISECONDS.toHours(stat.getValue());
            if(timeInHours < 1) {
                timeInMinutes = TimeUnit.MILLISECONDS.toMinutes(stat.getValue());
                measure = Math.max(1, measure);
                if(timeInMinutes < 1) {
                    timeInSeconds = TimeUnit.MILLISECONDS.toSeconds(stat.getValue());
                    measure = Math.max(2, measure);
                    if(timeInSeconds < 1) {
                        measure = Math.max(3, measure);
                    }
                }
            }
        }
        long value = 0;
        for (Map.Entry<String, Long> stat : contextStats.entrySet()) {
            value = getMeasureValueByIndex(measure, stat.getValue());
            percentage = (stat.getValue() * 100) / finalTotal;
            pieEntries.add(new PieEntry(value, stat.getKey() + " ("+percentage+"%)"));
        }

        PieDataSet dataSet = new PieDataSet(pieEntries, "");
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        // Data
        PieData pieData = new PieData(dataSet);
        pieData.setValueFormatter((value1, entry, dataSetIndex, viewPortHandler) -> Math.round(value1) + " " + getMeasureNameByIndex(measure));
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

    private long getMeasureValueByIndex(int measure, long value){
        switch (measure) {
            case 0:
                return TimeUnit.MILLISECONDS.toHours(value);
            case 1:
                return TimeUnit.MILLISECONDS.toMinutes(value);
            case 2:
                return TimeUnit.MILLISECONDS.toSeconds(value);
            case 3:
                return value;
            default:
                return 0;
        }
    }

    private String getMeasureNameByIndex(int measure){
        switch (measure) {
            case 0:
                return "hours";
            case 1:
                return "minutes";
            case 2:
                return "seconds";
            case 3:
                return "milliseconds";
            default:
                return "";
        }
    }
}
