package corp.kairos.adamastor.AppsManager;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class AppsBroadcastService extends BroadcastReceiver {
    private AppsManager appsManager = AppsManager.getInstance();

    public void onReceive(Context context, Intent intent) {
        this.appsManager.force = true;
    }
}