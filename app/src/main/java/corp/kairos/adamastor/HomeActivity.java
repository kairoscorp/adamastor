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
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

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

    private UserContext currentContext;
    private int currentContextIndex;
    private UserContext[] contexts = new UserContext[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setContexts();
        this.currentContext = this.contexts[0];
        this.currentContextIndex = 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        adjustScreenToContext();
    }

    private void adjustScreenToContext() {
        TextView contextOnScreen = ((TextView) findViewById(R.id.id_context_label));

        if(!(contextOnScreen.getText().equals(currentContext.getContextName()))) {
            LinearLayout contextAppsList = findViewById(R.id.id_context_apps_list);
            contextAppsList.removeAllViews();
            contextOnScreen.setText(currentContext.getContextName());


            for (String app : currentContext.getContextApps()) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(10, 10, 10, 10);

                TextView newApp = new TextView(this);
                newApp.setText(app);
                newApp.setLayoutParams(params);

                contextAppsList.addView(newApp);
            }
        }
    }

    private void setContexts(){
        this.contexts[0] = new UserContext("Home", Arrays.asList("App1", "App2", "App3"));
        this.contexts[1] = new UserContext("Work", Arrays.asList("App4", "App5", "App6"));
        this.contexts[2] = new UserContext("Workout", Arrays.asList("App7", "App8", "App9"));
        this.contexts[3] = new UserContext("Travelling", Arrays.asList("App10", "App11", "App12"));
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

    public void changeContext(View view){
        this.currentContextIndex = (this.currentContextIndex + 1) % this.contexts.length;
        this.currentContext = this.contexts[this.currentContextIndex];
        adjustScreenToContext();
    }

}