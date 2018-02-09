package corp.kairos.adamastor.Statistics;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import corp.kairos.adamastor.Animation.AnimationCompatActivity;
import corp.kairos.adamastor.AppDetails;
import corp.kairos.adamastor.AppsManager.AppsManager;
import corp.kairos.adamastor.Home.HomeActivity;
import corp.kairos.adamastor.R;
import corp.kairos.adamastor.Settings.Settings;

public class StatisticsActivity extends AnimationCompatActivity {


    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    private static PackageManager packageManager;
    private static AppsManager appsManager;
    private static Settings settings;
    private static Map<String, TreeSet<AppDetails>> statistics;

    private TabLayout tabLayout;
    private PieChart chart;
    private ListView listView;
    private View mList;
    private View mChart;
    private FloatingActionButton mSwitch;
    private int mShortAnimationDuration;
    private boolean isShowingGraph = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        packageManager = this.getPackageManager();
        appsManager = AppsManager.getInstance();
        settings = Settings.getInstance(this);

        // Set animations
        super.setAnimation("right");
        super.setRightActivity(HomeActivity.class);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        chart = findViewById(R.id.pie_chart);
        listView = findViewById(R.id.stats_apps_list);
        mList = findViewById(R.id.stats_applications);
        mChart = findViewById(R.id.stats_contexts);

        mSwitch = (FloatingActionButton) findViewById(R.id.id_statistics_swap_button);

        // Initially hide the content view.
        mChart.setVisibility(View.GONE);

        // Retrieve and cache the system's default "short" animation time.
        mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);

        mSwitch.setOnClickListener(view -> {
            crossFade();
        });

        mSwitch.setOnLongClickListener(view -> {
            Context context = getApplicationContext();
            CharSequence text;
            int duration = Toast.LENGTH_SHORT;
            if(appsManager.switchStatisticsManager()) {
                text = "Real Statistics!";
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            } else {
                text = "Fictional Statistics!";
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
            loadAppsStatistics();
            loadContextStatistics();

            chart.animateY(1200);
            mList.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

            return true;
        });
    }

    @Override
    public void onResume(){
        super.onResume();

        // Load Application Statistics View
        loadAppsStatistics();

        // Load Context Statistics View
        loadContextStatistics();

    }

    private void loadAppsStatistics(){
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        statistics = appsManager.getAppsStatisticsByContext(settings.getContextNames(), packageManager);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.stats_applications);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
    }

    private void loadContextStatistics(){
        Map<String, Long> contextStats = appsManager.getContextStatistics();
        long finalTotal = 0;
        for (Map.Entry<String, Long> stat : contextStats.entrySet()) {
            finalTotal += stat.getValue();
        }
        List<PieEntry> pieEntries = new ArrayList<>();
        for (Map.Entry<String, Long> stat : contextStats.entrySet()) {
            String timeUsage = "";
            long percentage = (stat.getValue() * 100) / finalTotal;
            long timePerContext = stat.getValue();
            long time = TimeUnit.MILLISECONDS.toHours(timePerContext);
            if(time < 1) {
                time = TimeUnit.MILLISECONDS.toMinutes(timePerContext);
                if(time < 1) {
                    time = TimeUnit.MILLISECONDS.toSeconds(timePerContext);
                    if(time < 1) {
                        time = timePerContext;
                        timeUsage = String.format("%d milliseconds",time);
                    } else {
                        timeUsage = String.format("%d seconds",time);
                    }
                } else {
                    timeUsage = String.format("%d minutes",time);
                }
            } else {
                timeUsage = String.format("%dh%02dm",
                        TimeUnit.MILLISECONDS.toHours(timePerContext),
                        TimeUnit.MILLISECONDS.toMinutes(timePerContext) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timePerContext))
                );
            }
            timeUsage += " ("+(int)percentage+"%)";
            pieEntries.add(new PieEntry(stat.getValue(), stat.getKey() + ": " + timeUsage));
        }

        PieDataSet dataSet = new PieDataSet(pieEntries, "");
        dataSet.setDrawValues(false);
        final int[] COLORS = {Color.rgb(196,196,22), Color.rgb(33,125,191), Color.rgb(255,121,97), Color.rgb(186,0,13), Color.rgb(66,66,66), Color.rgb(245,245,245), Color.rgb(109,109,109)};
        ArrayList<Integer> colors = new ArrayList<>();

        for(int c: COLORS) colors.add(c);
        dataSet.setColors(colors);

        // Data
        PieData pieData = new PieData(dataSet);
        pieData.setDrawValues(false);

        Description description = new Description();
        description.setText("");

        PieChart chart = findViewById(R.id.pie_chart);
        chart.getLegend().setEnabled(false);
        chart.setDrawEntryLabels(false);
        chart.setDrawHoleEnabled(false);
        chart.setDescription(description);
        chart.setCenterText("");
        chart.setData(pieData);
        LinearLayout customLegend = findViewById(R.id.legend_list);
        customLegend.removeAllViews();
        for(LegendEntry l : chart.getLegend().getEntries()) {
            LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = vi.inflate(R.layout.custom_legend, null);

            // fill in any details dynamically here
            TextView textView = (TextView) v.findViewById(R.id.legend_text);
            textView.setText(l.label);
            ImageView color = (ImageView) v.findViewById(R.id.legend_color);
            color.setBackgroundColor(l.formColor);

            // insert into main view
            customLegend.addView(v);
        }

        // undo all highlights
        chart.highlightValues(null);
        mChart.invalidate();
    }

    private void crossFade() {
        if(isShowingGraph) {
            crossFadeToList();
        } else {
            crossFadeToGraph();
        }
        isShowingGraph = !isShowingGraph;
    }

    private void crossFadeToList(){
        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        mChart.setAlpha(0f);
        mChart.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        mChart.animate()
                .alpha(1f)
                .setDuration(mShortAnimationDuration)
                .setListener(null);
        chart.animateY(1200);

        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
        tabLayout.animate()
                .alpha(0f)
                .setDuration(mShortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        tabLayout.setVisibility(View.GONE);
                    }
                });

        mList.animate()
                .alpha(0f)
                .setDuration(mShortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mList.setVisibility(View.GONE);
                        mSwitch.setImageResource(R.drawable.ic_list);
                    }
                });
    }

    private void crossFadeToGraph(){
        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        mList.setAlpha(0f);
        mList.setVisibility(View.VISIBLE);

        tabLayout.setAlpha(0f);
        tabLayout.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        tabLayout.animate()
                .alpha(1f)
                .setDuration(mShortAnimationDuration)
                .setListener(null);

        mList.animate()
                .alpha(1f)
                .setDuration(mShortAnimationDuration)
                .setListener(null);

        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
        mChart.animate()
                .alpha(0f)
                .setDuration(mShortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mChart.setVisibility(View.GONE);
                        mSwitch.setImageResource(R.drawable.ic_pie_chart);
                    }
                });
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String ARG_SECTION_STATISTICS = "section_statistics";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            String contextName;
            switch (sectionNumber) {
                case 1:
                    contextName = "Work";
                    args.putString(ARG_SECTION_NUMBER, contextName);
                    break;

                case 2:
                    contextName = "Leisure";
                    args.putString(ARG_SECTION_NUMBER, contextName);
                    break;

                default:
                    contextName = "Commute";
                    args.putString(ARG_SECTION_NUMBER, contextName);
                    break;
            }

            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup stats_applications,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_statistics_activity, stats_applications, false);

            RelativeLayout no_stats = rootView.findViewById(R.id.no_stats_info);
            ListView list = rootView.findViewById(R.id.stats_apps_list);

            TreeSet<AppDetails> stats = statistics.get(getArguments().getString(ARG_SECTION_NUMBER));

            if(stats.isEmpty()) {
                no_stats.setVisibility(View.VISIBLE);
                list.setVisibility(View.INVISIBLE);
            } else {
                list.setVisibility(View.VISIBLE);
                no_stats.setVisibility(View.INVISIBLE);
                StatisticsAppsMenuAdapter adapter = new StatisticsAppsMenuAdapter(this.getContext(), stats);
                list.setAdapter(adapter);
            }

            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }
}