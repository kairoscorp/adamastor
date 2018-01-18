package corp.kairos.adamastor.Animation;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

import java.util.Objects;

import corp.kairos.adamastor.R;


public abstract class AnimActivity extends Activity {

    private float x1;
    private float x2;
    private float MIN_DISTANCE = 300;

    private Class rightActivity;
    private Class leftActivity;
    private String animation;

    public Class getRightActivity() {
        return rightActivity;
    }

    public void setRightActivity(Class rightActivity) {
        this.rightActivity = rightActivity;
    }

    public Class getLeftActivity() {
        return leftActivity;
    }

    public void setLeftActivity(Class leftActivity) {
        this.leftActivity = leftActivity;
    }

    public void setAnimation(String animation) {
        this.animation = animation;
    }

    public String getAnimation(){
        return this.animation;
    }

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
                float deltaX1 = x1 - x2;
                if (deltaX1 > MIN_DISTANCE) {
                    if(this.rightActivity != null) {
                        this.animation = "right";
                        startActivity(rightActivity);
                    }
                } else {
                    float deltaX2 = x2 - x1;
                    if (deltaX2 > MIN_DISTANCE) {
                        if(this.leftActivity != null) {
                            this.animation = "left";
                            startActivity(leftActivity);
                        }
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    protected void startActivity(Class activity){
        Intent i = new Intent(this, activity);
        startActivity(i);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        switch (animation) {
            case "right":
                overridePendingTransitionRight();
                break;
            case "left":
                overridePendingTransitionLeft();
                break;
            case "top":
                overridePendingTransitionTop();
                break;
            case "down":
                overridePendingTransitionDown();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransitionLeft();
    }



    @Override
    public void finish() {
        super.finish();
        overridePendingTransitionLeft();
    }

    private void overridePendingTransitionRight() {
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    private void overridePendingTransitionLeft() {
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    private void overridePendingTransitionTop() {
        overridePendingTransition(R.anim.slide_from_up, R.anim.slide_to_down);
    }

    private void overridePendingTransitionDown() {
        overridePendingTransition(R.anim.slide_from_down, R.anim.slide_to_up);
    }
}
