package corp.kairos.adamastor.Animation;

import android.app.Activity;
import android.view.MotionEvent;

import corp.kairos.adamastor.R;


public class Animation {
    private float x1;
    private float y1;

    private float x2;
    private float y2;

    private float minDistanceHorizontal = 300;
    private float minDistanceVertical = 200;

    private Class rightActivity;
    private Class leftActivity;
    private Class upActivity;
    private Class downActivity;
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

    public Class getUpActivity() {
        return upActivity;
    }

    public void setUpActivity(Class upActivity) {
        this.upActivity = upActivity;
    }

    public Class getDownActivity() {
        return downActivity;
    }

    public void setDownActivity(Class downActivity) {
        this.downActivity = downActivity;
    }

    public void setAnimation(String animation) {
        this.animation = animation;
    }

    public String getAnimation(){
        return this.animation;
    }

    public Class onTouchEvent(MotionEvent event)
    {
        Class returnActivity = null;
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                y1 = event.getY();
                break;

            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                y2 = event.getY();

                float deltaX1 = x1 - x2;
                float deltaX2 = x2 - x1;
                float deltaY1 = y1 - y2;
                float deltaY2 = y2 - y1;

                if (deltaX1 > minDistanceHorizontal) {
                    if(this.rightActivity != null) {
                        this.animation = "right";
                        returnActivity = rightActivity;
                    }
                } else if (deltaX2 > minDistanceHorizontal) {
                    if(this.leftActivity != null) {
                        this.animation = "left";
                        returnActivity = leftActivity;
                    }
                } else if (deltaY1 > minDistanceVertical) {
                    if(this.downActivity != null) {
                        this.animation = "down";
                        returnActivity = downActivity;
                    }
                } else if (deltaY2 > minDistanceVertical) {
                    if(this.upActivity != null) {
                        this.animation = "up";
                        returnActivity = upActivity;
                    }
                }
                break;
        }
        return returnActivity;
    }

    private void overridePendingTransitionRight(Activity activity) {
        activity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    private void overridePendingTransitionLeft(Activity activity) {
        activity.overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    private void overridePendingTransitionUp(Activity activity) {
        activity.overridePendingTransition(R.anim.slide_from_down, R.anim.slide_to_up);
    }

    private void overridePendingTransitionDown(Activity activity) {
        activity.overridePendingTransition(R.anim.slide_from_up, R.anim.slide_to_down);
    }

    protected void overrideTransition(Activity activity){
        switch (animation) {
            case "right":
                overridePendingTransitionRight(activity);
                break;
            case "left":
                overridePendingTransitionLeft(activity);
                break;
            case "up":
                overridePendingTransitionUp(activity);
                break;
            case "down":
                overridePendingTransitionDown(activity);
                break;
        }
    }

}
