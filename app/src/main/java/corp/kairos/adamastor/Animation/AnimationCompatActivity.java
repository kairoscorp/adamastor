package corp.kairos.adamastor.Animation;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

import corp.kairos.adamastor.R;


public abstract class AnimationCompatActivity extends AppCompatActivity {
    private Animation animation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        animation = new Animation();
    }

    public Class getRightActivity() {
        return animation.getRightActivity();
    }

    public void setRightActivity(Class rightActivity) {
        this.animation.setRightActivity(rightActivity);
    }

    public Class getLeftActivity() {
        return animation.getLeftActivity();
    }

    public void setLeftActivity(Class leftActivity) {
        this.animation.setLeftActivity(leftActivity);
    }

    public Class getUpActivity() {
        return animation.getUpActivity();
    }

    public void setUpActivity(Class upActivity) {
        this.animation.setUpActivity(upActivity);
    }

    public Class getDownActivity() {
        return animation.getDownActivity();
    }

    public void setDownActivity(Class downActivity) {
        this.animation.setDownActivity(downActivity);
    }

    public String getAnimation() {
        return animation.getAnimation();
    }

    public void setAnimation(String animation) {
        this.animation.setAnimation(animation);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        Class activity = this.animation.onTouchEvent(event);
        if(activity != null) {
            Intent i = new Intent(this, activity);
            startActivity(i);
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        this.animation.overrideTransition(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.animation.overrideTransition(this);
    }


    @Override
    public void finish() {
        super.finish();
        this.animation.overrideTransition(this);
    }
}
