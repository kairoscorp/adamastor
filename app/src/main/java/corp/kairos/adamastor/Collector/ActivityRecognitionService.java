package corp.kairos.adamastor.Collector;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

public class ActivityRecognitionService extends IntentService {

    private static final String TAG = ActivityRecognitionService.class.getName();

    public ActivityRecognitionService() {
        super("ActivityRecognitionService");
    }

    public ActivityRecognitionService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if(ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            handleDetectedActivities(result.getProbableActivities());
        }
    }

    private void handleDetectedActivities(List<DetectedActivity> probableActivities) {
        int mostSignificant = -1;
        int mostSignificantValue = 0;
        int result;
        for( DetectedActivity activity : probableActivities ) {
           if(activity.getConfidence()>mostSignificantValue && activity.getType() != DetectedActivity.ON_FOOT) {
               mostSignificant = activity.getType();
               mostSignificantValue = activity.getConfidence();
           }
        }

        switch(mostSignificant){
            case DetectedActivity.IN_VEHICLE: result = 3 ;break;
            case DetectedActivity.ON_BICYCLE: result = 3 ;break;
            case DetectedActivity.ON_FOOT: result = 2; break;
            case DetectedActivity.STILL: result = 1; break;
            case DetectedActivity.UNKNOWN: result = 1; break;
            case DetectedActivity.TILTING: result = 1; break;
            case DetectedActivity.WALKING: result = 2; break;
            case DetectedActivity.RUNNING: result = 2; break;
            default: result = 1; break;
        }
        Log.i("CollectorServiceLog", "Activity = " + result);
        Intent i = new  Intent("kairos.CollectorService.ACTIVITY_MONITOR_RESULT");
        i.putExtra("ActivityNow",result);
        sendBroadcast(i);
    }


}
