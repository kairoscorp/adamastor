package corp.kairos.adamastor.Statistics;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import corp.kairos.adamastor.Animation.AnimationCompactActivity;
import corp.kairos.adamastor.AppDetails;
import corp.kairos.adamastor.AppsManager.AppsManager;
import corp.kairos.adamastor.Home.HomeActivity;
import corp.kairos.adamastor.R;

public class StatisticsActivity extends AnimationCompactActivity {

    private PackageManager packageManager;

    private View mFromView;
    private View mToView;
    private PieChart mChart;
    private ListView mList;
    private FloatingActionButton mSwitch;
    private FloatingActionButton mRefresh;
    private int mShortAnimationDuration;
    private boolean isShowingGraph = true;
    private AppsManager appsManager;
    // private int measure = 0; // 0 - HOURS | 1 - MINUTES | 2 - SECONDS | 3 - MILLISECONDS
    private Measure measure = Measure.HOURS; // 0 - HOURS | 1 - MINUTES | 2 - SECONDS | 3 - MILLISECONDS

    private static final String TAG = StatisticsActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.packageManager = this.getPackageManager();
        this.appsManager = AppsManager.getInstance();
        
        // Set animations
        super.setAnimation("right");
        super.setRightActivity(HomeActivity.class);

        setContentView(R.layout.activity_statistics);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mChart = findViewById(R.id.stats_context);
        mList = findViewById(R.id.stats_apps_list);

        mToView = mChart;
        mFromView = mList;

        mSwitch = (FloatingActionButton) findViewById(R.id.id_stats_swich_button);
        mRefresh = (FloatingActionButton) findViewById(R.id.id_stats_refresh_button);

        // Initially hide the content view.
        mToView.setVisibility(View.GONE);

        // Retrieve and cache the system's default "short" animation time.
        mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);

        mSwitch.setOnClickListener(view -> {
            crossFade();
        });
        mRefresh.setOnClickListener(view -> {
            loadAppsStatistics();
            loadContextStatistics();

            Animation anim = AnimationUtils.loadAnimation(this, R.anim.fade_in);

            mChart.animate();
            mList.startAnimation(anim);
        });

        // Load Application Statistics View
        loadAppsStatistics();

        // Load Context Statistics View
        loadContextStatistics();
    }

    private void loadAppsStatistics() {
        Set<AppDetails> stats = this.appsManager.getAppsStatistics(packageManager, (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE));

        // Load view
        loadListView(stats);
    }

    private void loadContextStatistics() {
        Map<String, Long> contextStats = this.appsManager.getContextStatistics();

        // Load view
        loadGraphView(contextStats);
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
                        if(isShowingGraph) {
                            mSwitch.setImageResource(R.drawable.ic_list);
                        } else {
                            mSwitch.setImageResource(R.drawable.ic_pie_chart);
                        }
                        isShowingGraph = !isShowingGraph;
                    }
                });
    }

    private void loadListView(Set<AppDetails> appsStats){
        StatisticsAppsMenuAdapter adapter = new StatisticsAppsMenuAdapter(this, appsStats);
        mList.setAdapter(adapter);
    }

    private void loadGraphView(Map<String, Long> contextStats){
        List<PieEntry> pieEntries = new ArrayList<>();
        this.measure = Measure.HOURS;
        long percentage, finalTotal = 0;
        long timeInHours, timeInMinutes, timeInSeconds;
        for (Map.Entry<String, Long> stat : contextStats.entrySet()) {
            finalTotal += stat.getValue();
            timeInHours = TimeUnit.MILLISECONDS.toHours(stat.getValue());
            if(timeInHours < 1) {
                timeInMinutes = TimeUnit.MILLISECONDS.toMinutes(stat.getValue());
                measure = measure.max(Measure.MINUTES.id);
                if(timeInMinutes < 1) {
                    timeInSeconds = TimeUnit.MILLISECONDS.toSeconds(stat.getValue());
                    measure = measure.max(Measure.SECONDS.id);
                    if(timeInSeconds < 1) {
                        measure = measure.max(Measure.MILLISECONDS.id);
                    }
                }
            }
        }
        long value = 0;
        for (Map.Entry<String, Long> stat : contextStats.entrySet()) {
            value = measure.getValue(stat.getValue());
            percentage = (stat.getValue() * 100) / finalTotal;
            pieEntries.add(new PieEntry(value, stat.getKey() + " ("+percentage+"%)"));
        }

        PieDataSet dataSet = new PieDataSet(pieEntries, "");
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        // Data
        PieData pieData = new PieData(dataSet);
        pieData.setValueFormatter((value1, entry, dataSetIndex, viewPortHandler) -> Math.round(value1) + " " + measure.name);
        pieData.setValueTextSize(12f);

        Description description = new Description();
        description.setText("Applications usage by context.");
        mChart.setDescription(description);
        mChart.setCenterText("Usage Context");

        mChart.setEntryLabelColor(Color.DKGRAY);
        mChart.setCenterTextColor(Color.BLACK);
        mChart.setCenterTextSize(15f);
        mChart.animateY(800);
        mChart.setData(pieData);

        // undo all highlights
        mChart.highlightValues(null);
        mChart.invalidate();
    }

    private enum Measure {

        HOURS, MINUTES, SECONDS, MILLISECONDS;

        private int id;
        private String name;

        static {
            HOURS.id = 0;
            HOURS.name = "hours";

            MINUTES.id = 1;
            MINUTES.name = "minutes";

            SECONDS.id = 2;
            SECONDS.name = "seconds";

            MILLISECONDS.id = 3;
            MILLISECONDS.name = "milliseconds";
        }

        public long getValue(long value) {
            switch (this.id) {
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

        private Measure getMeasure(int id) {
            switch (id) {
                case 0:
                    return HOURS;
                case 1:
                    return MINUTES;
                case 2:
                    return SECONDS;
                case 3:
                    return MILLISECONDS;

                default:
                    return HOURS;
            }
        }

        public Measure max(int id) {
            return getMeasure(Math.max(this.id, id));
        }

    }
}
