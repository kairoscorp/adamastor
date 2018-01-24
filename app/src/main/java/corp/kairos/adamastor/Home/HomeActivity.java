package corp.kairos.adamastor.Home;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import corp.kairos.adamastor.AllApps.AllAppsActivity;
import corp.kairos.adamastor.Animation.AnimationCompactActivity;
import corp.kairos.adamastor.AppDetails;
import corp.kairos.adamastor.AppsManager.AppsManager;
import corp.kairos.adamastor.Collector.CollectorService;
import corp.kairos.adamastor.ContextList.ContextListActivity;
import corp.kairos.adamastor.Home.dao.FavouriteAppsDAO;
import corp.kairos.adamastor.Home.dao.StaticFavouriteAppsDAO;
import corp.kairos.adamastor.Onboarding.Onboard1WelcomeActivity;
import corp.kairos.adamastor.R;
import corp.kairos.adamastor.Settings.ContextRelated.ContextRelatedSettingsActivity;
import corp.kairos.adamastor.Settings.Settings;
import corp.kairos.adamastor.Statistics.StatisticsActivity;
import corp.kairos.adamastor.UserContext;
import corp.kairos.adamastor.Util;

// TODO: Find a way to not need this as to not have duplicated code.
public class HomeActivity extends AnimationCompactActivity {

    public static final String TAG = HomeActivity.class.toString();
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private PackageManager packageManager;
    private Settings settingsUser;
    private AppsManager appsManager;


    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TextView monthDayTextView;
    private TextView weekdayYearTextView;

    private UserContext[] userContexts;
    private List<AppDetails> favouriteApps;

    private boolean permissionsGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.packageManager = getPackageManager();
        this.settingsUser = Settings.getInstance(this);
        this.appsManager = AppsManager.getInstance();
        this.appsManager.setupApps(packageManager);

        if (!settingsUser.isOnboardingDone()) {
            // Set animation
            super.setAnimation("up");

            Intent i = new Intent(this, Onboard1WelcomeActivity.class);
            startActivity(i);
            finish();

        } else {
            setContentView(R.layout.activity_home);
            this.viewPager = (ViewPager) findViewById(R.id.home_context_view_pager);
            this.tabLayout = (TabLayout) findViewById(R.id.context_tabs);
            this.monthDayTextView = (TextView) findViewById(R.id.month_day_text_view);
            this.weekdayYearTextView = (TextView) findViewById(R.id.weekday_year_text_view);

            checkPermissions();
            if (permissionsGranted)
                bindCollectorService();

            // Set navigation
            this.setDownActivity(AllAppsActivity.class);
            this.setLeftActivity(StatisticsActivity.class);
            this.setRightActivity(ContextListActivity.class);

            setupDates();

            setupFavouriteApps();

            setupContextDisplayer();
        }
    }

    @SuppressLint("SimpleDateFormat")
    private void setupDates() {
        Date time = Calendar.getInstance().getTime();
         SimpleDateFormat df = new SimpleDateFormat("MMMM dd");
        this.monthDayTextView.setText(df.format(time));

        df = new SimpleDateFormat("EEEE, yyyy");
        this.weekdayYearTextView.setText(df.format(time));
    }


    private void setupFavouriteApps() {
        // Load favorite apps
        FavouriteAppsDAO favAppsDAO = new StaticFavouriteAppsDAO(getPackageManager());
        this.favouriteApps = favAppsDAO.getFavouriteApps();
        Log.i("setupFavouriteApps", Integer.toString(favouriteApps.size()));

        // Pass favourite apps to the views
        int maxAppsPerSide = this.favouriteApps.size() / 2;
        LayoutInflater layoutInflater = getLayoutInflater();
        LinearLayout leftLinearLayout = findViewById(R.id.left_side_favourites);
        LinearLayout rightLinearLayout = findViewById(R.id.right_side_favourites);

        int i = 1;
        for (AppDetails app : this.favouriteApps) {
            // Choose what layout to fill
            LinearLayout parentLayout = i <= maxAppsPerSide ? leftLinearLayout : rightLinearLayout;

            // Create new Image view for this fav app
            ImageView img = (ImageView) layoutInflater.inflate(R.layout.favourite_app_icon, parentLayout, false);
            img.setImageDrawable(app.getIcon());
            img.setOnClickListener(v -> {
                Intent intent = getPackageManager().getLaunchIntentForPackage(app.getPackageName());
                HomeActivity.this.startActivity(intent);
            });
            parentLayout.addView(img);
            i++;
        }

    }

    private void setupContextDisplayer() {
        loadContexts();

        setupViewPager();

        setupTabs();
    }

    // TODO: Make this method get last active context
    private void loadContexts() {
//        Settings settings = new Settings(this);
//        this.userContexts = settings.getUserContextsAsArray();
        this.userContexts = Util.createDummyContextList("HomeActivity", getPackageManager());
    }

    private void setupViewPager() {
        ContextsViewPagerAdapter adapter = new ContextsViewPagerAdapter(getSupportFragmentManager());

        for (UserContext context : this.userContexts) {
            PlaceholderFragment ctxFragment = PlaceholderFragment.newInstance(context);
            adapter.addFrag(ctxFragment, context.getContextName());
        }
        this.viewPager.setAdapter(adapter);
    }

    private void setupTabs() {
        this.tabLayout.setupWithViewPager(this.viewPager);
        int i = 0;
        for (UserContext context : this.userContexts) {
            int iconCode;
            switch (context.getContextName()) {
                case "Work":
                    iconCode = R.drawable.ic_work_black_24dp;
                    break;
                case "Leisure":
                    iconCode = R.drawable.ic_leisure_black_24dp;
                    break;
                case "Travel":
                case "Commute":
                    iconCode = R.drawable.ic_commute_black_24dp;
                    break;
                default:
                    iconCode = R.drawable.ic_settings_black_24dp;
            }

            // Setup Icon Colors
            TabLayout.Tab tab = this.tabLayout.getTabAt(i);
            tab.setIcon(iconCode);
            Drawable icon = tab.getIcon();
            icon = DrawableCompat.wrap(icon);
            DrawableCompat.setTintList(icon, getResources().getColorStateList(R.color.main_screen_tab_icon_colors));
            i++;
        }

        addTabEventListener(this.tabLayout);
        // TODO: Set last active context
        // TODO: Substitute for the persisted
        Util.setBackground(getApplicationContext(), R.drawable.commute_background);
    }

    private void addTabEventListener(TabLayout tabLayout) {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int backgroundResource, tabPosition = tab.getPosition();
                if (tabPosition == 0) backgroundResource = R.drawable.commute_background;
                else if (tabPosition == 1) backgroundResource = R.drawable.leisure_background;
                    // Default
                else backgroundResource = R.drawable.work_background;
                Util.setBackground(getApplicationContext(), backgroundResource);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Do Nothing
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Do Nothing
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPermissions();
        if (permissionsGranted)
            bindCollectorService();
    }

    public void showAllAppsMenu(View v) {
        super.setAnimation("down");
        Intent i = new Intent(this, AllAppsActivity.class);
        startActivity(i);
    }

    public void showSettings(View v) {
        super.setAnimation("up");
        Intent i = new Intent(this, ContextRelatedSettingsActivity.class);
        startActivity(i);
    }

    private void checkPermissions() {
        if ((ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) ||
                (ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) ||
                (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) ||
                (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.GET_ACCOUNTS)
                        != PackageManager.PERMISSION_GRANTED)) {

            requestPermissions();

        } else {
            Log.i(TAG, "permissions OK");
            permissionsGranted = true;
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCOUNT_MANAGER,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.PACKAGE_USAGE_STATS,
                        Manifest.permission.GET_ACCOUNTS},
                MY_PERMISSIONS_REQUEST_LOCATION);
    }

    private void bindCollectorService() {
        Log.i(TAG, "Binding Service");
        Intent intent = new Intent(this, CollectorService.class);
        startService(intent);

    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == MY_PERMISSIONS_REQUEST_LOCATION)
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                permissionsGranted = true;
            else
                permissionsGranted = false;
    }

}