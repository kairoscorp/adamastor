package corp.kairos.adamastor.Settings.ContextRelated;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import corp.kairos.adamastor.Animation.AnimationCompatActivity;
import corp.kairos.adamastor.R;
import corp.kairos.adamastor.Settings.Settings;
import corp.kairos.adamastor.UserContext;


public class EditContextAppsActivity extends AnimationCompatActivity {
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Settings userSettings;
    private UserContext workContext;
    private UserContext leisureContext;
    private UserContext commuteContext;
    private ViewPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAnimation("right");
        userSettings = Settings.getInstance(getApplicationContext());
        workContext = userSettings.getUserContext("Work");
        leisureContext = userSettings.getUserContext("Leisure");
        commuteContext = userSettings.getUserContext("Commute");

        setContentView(R.layout.activity_select_context_apps);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.context_tabs);
        createViewPager(viewPager);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
    }

    @Override
    public void onBackPressed() {
        setAnimation("left");
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void createViewPager(ViewPager viewPager) {
        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        AppsListFragment fragment = new AppsListFragment();
        fragment.setUserContext(workContext);
        pagerAdapter.addFrag(fragment, "Work");

        fragment = new AppsListFragment();
        fragment.setUserContext(leisureContext);
        pagerAdapter.addFrag(fragment, "Leisure");

        fragment = new AppsListFragment();
        fragment.setUserContext(commuteContext);
        pagerAdapter.addFrag(fragment, "Commute");
        viewPager.setAdapter(pagerAdapter);
    }

    public void save(View v) {
        Settings userSettings = Settings.getInstance(getApplicationContext());
        for (int i = 0; i < pagerAdapter.getCount(); i++) {
            AppsListFragment f = (AppsListFragment) pagerAdapter.getItem(i);
            userSettings.setUserContext(f.getUserContext());
        }
        userSettings.saveContextSettings();
        setAnimation("left");
        finish();
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
