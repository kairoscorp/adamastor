package corp.kairos.adamastor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import corp.kairos.adamastor.AllApps.AllAppsActivity;
import corp.kairos.adamastor.R;

public class HomeActivity extends Activity {

    private long mCurrentMillis;
    private float mVelocity;

    /**
     * The time constant used to calculate dampening in the low-pass filter of scroll velocity.
     * Cutoff frequency is set at 10 Hz.
     */
    public static final float SCROLL_VELOCITY_DAMPENING_RC = 1000f / (2f * (float) Math.PI * 10);
    private float mLastY;
    private float mDownX;
    private float mDownY;
    private int mLastDisplacement;
    private float mDisplacementY;
    private float mDisplacementX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransitionExit();
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransitionEnter();
    }

    protected void overridePendingTransitionEnter() {
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    protected void overridePendingTransitionExit() {
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransitionExit();
    }

    public void showAllAppsMenu(View v){
        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(50);
        Intent i = new Intent(this, AllAppsActivity.class);
        startActivity(i);
    }

    private float x1;
    private float x2;
    private float MIN_DISTANCE = 200;
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                float deltaX = x1 - x2;
                if (deltaX > MIN_DISTANCE) {
                    Intent i = new Intent(this, AllAppsActivity.class);
                    startActivity(i);
                }
                break;
        }
        return super.onTouchEvent(event);
    }

}