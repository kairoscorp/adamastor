package corp.kairos.adamastor.Home.dao;

import android.content.pm.PackageManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import corp.kairos.adamastor.AppDetails;

public class StaticFavouriteAppsDAO implements FavouriteAppsDAO {

    private static final String TAG = "StaticFavAppsDAO";
    private final PackageManager pm;

    public StaticFavouriteAppsDAO(PackageManager pm) {
        this.pm = pm;
    }

    @Override
    public List<AppDetails> getFavouriteApps() {
        List<AppDetails> favouriteApps = new ArrayList<>();
        List<String> staticPackageNames = Arrays.asList(
                "com.android.chrome",
                "com.google.android.gm",
                "com.android.phone",
                "com.google.android.apps.messaging"
        );

        for(String appPackage: staticPackageNames) {
            try {
                favouriteApps.add(new AppDetails(this.pm, appPackage));
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "Favourite app package name not found");
                e.printStackTrace();
            }
        }

        return favouriteApps;
    }

    @Override
    public void storeFavouriteApps(List<AppDetails> favouriteApps) {
        throw new UnsupportedOperationException("Static DAO can only be used to retrieve apps");
    }
}
