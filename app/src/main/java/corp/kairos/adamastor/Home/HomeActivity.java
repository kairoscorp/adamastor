package corp.kairos.adamastor.Home;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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


// TODO: Find a way to not need this as to not have duplicated code.
public class HomeActivity extends AnimationCompactActivity {

    public static final String TAG = HomeActivity.class.toString();
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private PackageManager packageManager;
    private Settings settingsUser;
    private AppsManager appsManager;


    private TabLayout tabLayout;
    private ViewPager viewPager;

    private Settings settings;
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

            checkPermissions();
            if (permissionsGranted)
                bindCollectorService();

            // Set navigation
            this.setDownActivity(AllAppsActivity.class);
            this.setLeftActivity(StatisticsActivity.class);
            this.setRightActivity(ContextListActivity.class);

            // Load settings & Setup Contexts
//            setupContextViews();
//            contexts = settings.getUserContextsAsArray();
//            this.currentContextIndex = 0;
//            this.currentContext = this.contexts[0];

            setupFavouriteApps();

            setupContextDisplayer();
        }
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
        this.userContexts = settingsUser.getUserContextsAsArray();
    }

    private void setupViewPager() {
        this.viewPager = (ViewPager) findViewById(R.id.home_context_view_pager);

        ContextsViewPagerAdapter adapter = new ContextsViewPagerAdapter(getSupportFragmentManager());

        for (UserContext context : this.userContexts)
            adapter.addFrag(new ContextFragment(), context.getContextName());

        this.viewPager.setAdapter(adapter);
    }

    private void setupTabs() {
        this.tabLayout = (TabLayout) findViewById(R.id.context_tabs);
        this.tabLayout.setupWithViewPager(this.viewPager);
//        this.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                LinearLayout linearLayout = findViewById(R.id.home_activity);
//                linearLayout.setBackground();
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//                // Do Nothing
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//                // Do Nothing
//            }
//        });
        int i = 0;
        for (UserContext context : this.userContexts) {
            TextView tab = (TextView) LayoutInflater.from(this).inflate(R.layout.context_tab_header, null);
            tab.setText(context.getContextName());
            int icon;
            switch (context.getContextName()) {
                case "Work":
                    icon = R.drawable.ic_work_black_24dp;
                    break;
                case "Leisure":
                    icon = R.drawable.ic_leisure_black_24dp;
                    break;
                case "Travel":
                case "Commute":
                    icon = R.drawable.ic_commute_black_24dp;
                    break;
                default:
                    icon = R.drawable.ic_settings_black_24dp;
            }
            tab.setCompoundDrawablesWithIntrinsicBounds(0, 0, icon, 0);
            tabLayout.getTabAt(i).setCustomView(tab);
            i++;
        }
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