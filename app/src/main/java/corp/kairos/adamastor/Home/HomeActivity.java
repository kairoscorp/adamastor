package corp.kairos.adamastor.Home;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
import corp.kairos.adamastor.OptionsMenu;
import corp.kairos.adamastor.R;
import corp.kairos.adamastor.Settings.ContextRelated.ContextRelatedSettingsActivity;
import corp.kairos.adamastor.Settings.Settings;
import corp.kairos.adamastor.Statistics.StatisticsActivity;
import corp.kairos.adamastor.UserContext;

public class HomeActivity extends AnimationCompactActivity {

    public static final String TAG = HomeActivity.class.toString();
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private PackageManager packageManager;
    private Settings userSettings;
    private AppsManager appsManager;
    private BackgroundChanger backgroundChanger;
    private UserContext[] userContexts;
    private List<AppDetails> favouriteApps;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TextView monthDayTextView;
    private TextView weekdayYearTextView;

    private boolean permissionsGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.userSettings = Settings.getInstance(this);

        if (!userSettings.isOnboardingDone()) {
            // Set animation
            super.setAnimation("up");

            Intent i = new Intent(this, Onboard1WelcomeActivity.class);
            startActivity(i);
            finish();
        } else {
            this.packageManager = getPackageManager();
            this.appsManager = AppsManager.getInstance();
            this.appsManager.setupApps(packageManager);
            setContentView(R.layout.activity_home);
            this.viewPager = (ViewPager) findViewById(R.id.home_view_pager);
            this.tabLayout = (TabLayout) findViewById(R.id.context_tabs);
            this.monthDayTextView = (TextView) findViewById(R.id.month_day_text_view);
            this.weekdayYearTextView = (TextView) findViewById(R.id.weekday_year_text_view);

            checkPermissions();
            if (permissionsGranted)
                bindCollectorService();

            // Set navigation
            super.setAnimation("up");
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
                this.startActivity(intent);
            });
            img.setOnLongClickListener(view -> {
                showOptions(this, img.getRootView(), app);
                return true;
            });
            parentLayout.addView(img);
            i++;
        }

    }

    private void setupContextDisplayer() {
        // TODO: Make this method get last active context
        this.userContexts = userSettings.getUserContextsAsArray();

        setupViewPager();

        setupTabs();
    }

    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        for (UserContext context : this.userContexts)
            adapter.addFrag(PlaceholderFragment.newInstance(context));

        this.viewPager.setAdapter(adapter);
    }

    private void setupTabs() {
        this.tabLayout.setupWithViewPager(this.viewPager);
        int i = 0;
        for (UserContext context : this.userContexts) {
            int iconCode;
            // TODO: make contexts carry their own icon
            switch (context.getContextName()) {
                case "Work":
                    iconCode = R.drawable.ic_work_black_24dp;
                    break;
                case "Home":
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

            // Setup Icon and Tab Tag
            TabLayout.Tab tab = this.tabLayout.getTabAt(i);
            tab.setIcon(iconCode);
            Drawable icon = tab.getIcon();
            icon = DrawableCompat.wrap(icon);
            tab.setTag(context.getContextName());
            DrawableCompat.setTintList(icon, getResources().getColorStateList(R.color.main_screen_tab_icon_colors, null));
            i++;
        }

        addTabEventListener(this.tabLayout);
        // TODO: Set last active context
        // TODO: Substitute for the persisted
        int starterBackground = getSelectedTabBackground(this.tabLayout.getTabAt(0));
        BackgroundChanger.changeWallpaper(getApplicationContext(), starterBackground);
    }

    private void addTabEventListener(TabLayout tabLayout) {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                BackgroundChanger.changeWallpaper(getApplicationContext(), getSelectedTabBackground(tab));
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

    private int getSelectedTabBackground(TabLayout.Tab tab) {
        String tabTag = (String) tab.getTag();
        assert tabTag != null;
        switch (tabTag) {
            case "Work":
                return R.drawable.work_background;
            case "Travel":
            case "Commute":
                return R.drawable.commute_background;
            case "Home":
            case "Leisure":
            default:
                return R.drawable.leisure_background;
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
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION)
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                permissionsGranted = true;
            else
                permissionsGranted = false;
    }

    private static void showOptionMenu(Context ctx, AppDetails appDetail, int viewId) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("app", appDetail);
        Fragment options = new OptionsMenu();
        options.setArguments(bundle);
        FragmentTransaction transaction = ((Activity) ctx).getFragmentManager().beginTransaction();
        transaction.replace(viewId, options, "OPTIONS");
        transaction.addToBackStack("OPTIONS");
        transaction.commit();
    }


    public static void showOptions(Context ctx, View rootView, AppDetails app) {
        FrameLayout optionMenu = new FrameLayout(ctx);
        optionMenu.setBackgroundResource(android.R.color.transparent);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.BOTTOM;
        params.setMargins(0, 0, 0, getNavigationBarHeight(ctx));
        optionMenu.setLayoutParams(params);
        ViewGroup parentView = (ViewGroup) rootView;
        parentView.addView(optionMenu);
        optionMenu.setId(R.id.view_option);
        showOptionMenu(ctx, app, R.id.view_option);
    }

    public static int getNavigationBarHeight(Context ctx) {
        Resources resources = ctx.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            if (getFragmentManager().findFragmentByTag("OPTIONS") != null) {
                getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag("OPTIONS")).commit();
            }
            if (getFragmentManager().findFragmentByTag("CONTEXT") != null) {
                getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag("CONTEXT")).commit();
            }
            if (findViewById(R.id.select_context) != null) {
                ((ViewGroup) findViewById(R.id.select_context).getParent()).removeView(findViewById(R.id.select_context));
                getWindow().setStatusBarColor(0);
                getWindow().setNavigationBarColor(0);
            }

        } else {
            super.onBackPressed();
        }
    }

}