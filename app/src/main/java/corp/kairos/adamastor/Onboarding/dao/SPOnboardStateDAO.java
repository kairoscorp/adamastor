package corp.kairos.adamastor.Onboarding.dao;


import android.content.Context;
import android.content.SharedPreferences;

public class SPOnboardStateDAO implements OnboardStateDAO {

    /**
     * This parameter should always be the application context
     */
    private final Context appContext;

    /**
     * Creates a new instance of this class
     * <p/>
     * This DAO uses the SharedPreferences as a backend
     *
     * @param appContext The Android Context for this application
     */
    public SPOnboardStateDAO(Context appContext) {
        this.appContext = appContext;
    }

    @Override
    public boolean isOnboardingDone() {
        SharedPreferences sharedPref = this.appContext.getSharedPreferences("GENERAL", Context.MODE_PRIVATE);
        return sharedPref.getBoolean("OnboardingDone",false);
    }

    @Override
    public void setOnboardingDone() {
        SharedPreferences sharedPref = this.appContext.getSharedPreferences("GENERAL", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("OnboardingDone",true);
        editor.apply();
    }


}
