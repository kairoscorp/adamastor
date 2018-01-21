package corp.kairos.adamastor.Home.dao;


import java.util.List;

import corp.kairos.adamastor.AppDetails;

public interface FavouriteAppsDAO {

    /**
     * Gets the set of favourite apps from the persistence layer
     *
     * @return A Set with the the favourite apps
     */
    List<AppDetails> getFavouriteApps();

    /**
     * Stores or updates the favourite apps in the persistence layer
     *
     * @param favouriteApps The set of apps to set the favourite apps to
     */
    void storeFavouriteApps(List<AppDetails> favouriteApps);

}
