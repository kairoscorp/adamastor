package corp.kairos.adamastor.Statistics;

import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ListView;
import android.widget.TextView;

import java.util.Set;

import corp.kairos.adamastor.Animation.AnimationCompatActivity;
import corp.kairos.adamastor.AppDetails;
import corp.kairos.adamastor.AppsManager.AppsManager;
import corp.kairos.adamastor.R;
import corp.kairos.adamastor.Settings.Settings;
import corp.kairos.adamastor.Statistics.StatisticsAppsMenuAdapter;
import corp.kairos.adamastor.UserContext;

public class StatisticsActivity extends AnimationCompatActivity {


    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    private static PackageManager packageManager;
    private static AppsManager appsManager;
    private static Settings settings;
    private static UsageStatsManager usageStatsManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.packageManager = this.getPackageManager();
        this.appsManager = AppsManager.getInstance();
        
        // Set animations
        super.setAnimation("right");
        super.setRightActivity(HomeActivity.class);

        setContentView(R.layout.activity_statistics);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_contexts);
        setSupportActionBar(toolbar);

        packageManager = this.getPackageManager();
        appsManager = AppsManager.getInstance();
        settings = Settings.getInstance(this);
        usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        TextView homeTab = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        homeTab.setText("Home"); //tab label txt
        homeTab.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_work_white, 0, 0, 0);
        tabLayout.getTabAt(0).setCustomView(homeTab);

        TextView leisureTab = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        leisureTab.setText("Leisure");
        leisureTab.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_leisure_white, 0, 0, 0);
        tabLayout.getTabAt(1).setCustomView(leisureTab);

        TextView commuteTab = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        commuteTab.setText("Commute");
        commuteTab.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_commute_white, 0, 0, 0);
        tabLayout.getTabAt(2).setCustomView(commuteTab);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        FloatingActionButton swapButton =  findViewById(R.id.id_statistics_swap_button);
        swapButton.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_statistics_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();

            switch (sectionNumber) {
                case 1:
                    args.putString(ARG_SECTION_NUMBER, "Work");
                    break;

                case 2:
                    args.putString(ARG_SECTION_NUMBER, "Home");
                    break;

                default:
                    args.putString(ARG_SECTION_NUMBER, "Commute");
                    break;
            }
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_statistics_activity, container, false);

            UserContext context = settings.getUserContext(getArguments().getString(ARG_SECTION_NUMBER));
            Set<AppDetails> stats = appsManager.getAppStatisticsByContext(context, packageManager, usageStatsManager);

            StatisticsAppsMenuAdapter adapter = new StatisticsAppsMenuAdapter(this.getContext(), stats);

            ListView list = rootView.findViewById(R.id.stats_apps_list);

            list.setAdapter(adapter);

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
