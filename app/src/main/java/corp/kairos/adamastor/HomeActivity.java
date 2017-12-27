package corp.kairos.adamastor;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import corp.kairos.adamastor.AllApps.AllAppsActivity;
import corp.kairos.adamastor.AllApps.AppDetail;
import corp.kairos.adamastor.R;

public class HomeActivity extends Activity {

    private static final String TAG = HomeActivity.class.getName();

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
    private View.OnClickListener clickHandler;
    private PackageManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setContexts();
        this.currentContext = this.contexts[0];
        this.currentContextIndex = 0;

        addClickListener();
    }

    private void addClickListener(){
        clickHandler = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeApp ha = (HomeApp) v;
                Intent i = pm.getLaunchIntentForPackage(ha.getPackageName());
                HomeActivity.this.startActivity(i);
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        adjustScreenToContext();
    }

    /*
    * This method should be called when the home screen needs to change in order to show the
    * correct apps for the current context*/
    private void adjustScreenToContext() {
        TextView contextOnScreen = ((TextView) findViewById(R.id.id_context_label));

        //Just refresh the view if the context actually changed
        if(!(contextOnScreen.getText().equals(currentContext.getContextName()))) {
            LinearLayout contextAppsList = findViewById(R.id.id_context_apps_list);
            contextAppsList.removeAllViews();
            contextOnScreen.setText(currentContext.getContextName());

            for (AppDetail app : currentContext.getContextApps()) {
                LayoutInflater inflater = LayoutInflater.from(this);
                HomeApp inflatedView = (HomeApp) inflater.inflate(R.layout.home_app, null);

                inflatedView.setPackageName(app.getName());
                TextView textViewTitle = (TextView) inflatedView.findViewById(R.id.app_text);
                ImageView imageViewIte = (ImageView) inflatedView.findViewById(R.id.app_image);

                textViewTitle.setText(app.getLabel());
                imageViewIte.setImageDrawable(app.getIcon());
                inflatedView.setOnClickListener(clickHandler);

                contextAppsList.addView(inflatedView);
            }
        }
    }

    private void setContexts(){
        /*
        * Creating 4 static contexts to simulate the intended behaviour */
        List<String> homeApps = Arrays.asList("com.google.android.talk", "com.facebook.katana");
        List<String> workApps = Arrays.asList("com.google.android.gm", "com.Slack", "com.google.android.calendar");
        List<String> workoutApps = Arrays.asList("com.google.android.apps.fitness");
        List<String> travellingApps = Arrays.asList("com.google.android.apps.maps");

        List<List<String>> apps = Arrays.asList(homeApps, workApps, workoutApps, travellingApps);

        pm = getPackageManager();

        /*
        * These loops just associate different apps with different contexts*/
        int n = 0;
        for(List<String> appsList : apps){
            List<AppDetail> appDetails = new ArrayList<>();
            for(String p: appsList){
                try {
                    ApplicationInfo ai = pm.getApplicationInfo(p,0);

                    String label = (String) pm.getApplicationLabel(ai);
                    String name = ai.packageName.toString();
                    Drawable icon = pm.getApplicationIcon(ai);

                    AppDetail app = new AppDetail(label, name, icon);
                    appDetails.add(app);
                } catch (PackageManager.NameNotFoundException e) {
                    Log.e(TAG, "App not found");
                    e.printStackTrace();
                }
            }
            this.contexts[n] = new UserContext("Context " + n, appDetails);
            n++;
        }
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

    /*
    * The mechanism for manual context change is pressing the context label.
    * This method is called when the context label is pressed.
    */
    public void changeContext(View view){
        this.currentContextIndex = (this.currentContextIndex + 1) % this.contexts.length;
        this.currentContext = this.contexts[this.currentContextIndex];
        adjustScreenToContext();
    }

}