package corp.kairos.adamastor.AllApps;

<<<<<<< HEAD
=======

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
>>>>>>> fd86672... add Inital View and code OptionMenu
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridView;

import java.util.Set;

import corp.kairos.adamastor.Animation.AnimationActivity;
import corp.kairos.adamastor.AppDetails;
import corp.kairos.adamastor.AppsManager.AppsManager;
import corp.kairos.adamastor.Home.HomeActivity;
import corp.kairos.adamastor.OptionsMenu;
import corp.kairos.adamastor.R;

import static corp.kairos.adamastor.Util.getObjectByIndex;

public class AllAppsActivity extends AnimationActivity {
    private PackageManager packageManager;
    private AppsManager appsManager;

    private Set<AppDetails> allApps;

    private GridView allAppsMenuView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allapps_menu);

        this.allAppsMenuView = findViewById(R.id.allapps_menu);
        // Managers
        this.appsManager = AppsManager.getInstance();
        this.packageManager = getPackageManager();


        this.allApps = this.appsManager.getAllApps(packageManager, true);

        // Set animations
        super.setAnimation("up");
        super.setUpActivity(HomeActivity.class);

        // Views
        AllAppsMenuAdapter adapter = new AllAppsMenuAdapter(this, this.allApps);
        this.allAppsMenuView.setAdapter(adapter);
    }

    private void addClickListener(){
        this.allAppsMenuView.setOnItemClickListener((av, v, pos, id) -> {
            AppDetails app = (AppDetails) getObjectByIndex(pos, allApps);
            Intent i = packageManager.getLaunchIntentForPackage(app.getPackageName());
            startActivity(i);
        });
        this.allAppsMenuView.setOnItemLongClickListener((parent, view, position, id) -> {
            AppDetails app = (AppDetails) getObjectByIndex(position, allApps);
            FrameLayout optionMenu = new FrameLayout(getApplicationContext());
            optionMenu.setBackgroundResource(android.R.color.transparent);
            FrameLayout.LayoutParams params =  new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            params.gravity= Gravity.BOTTOM;
            optionMenu.setLayoutParams(params);
            ViewGroup parentView =  findViewById(android.R.id.content);
            parentView.addView(optionMenu);
            optionMenu.setId(R.id.view_option);
            showOptionMenu(app,R.id.view_option);
            return true;
        });
    }

    public void showOptionMenu(AppDetails appDetail,int viewId){
        Bundle bundle = new Bundle();
        bundle.putSerializable("app",appDetail);
        Fragment options = new OptionsMenu();
        options.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(viewId,options);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0)
           getFragmentManager().popBackStackImmediate();
        else super.onBackPressed();
    }
}
