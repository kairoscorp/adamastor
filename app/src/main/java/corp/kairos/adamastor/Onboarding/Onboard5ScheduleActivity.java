package corp.kairos.adamastor.Onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.transition.TransitionManager;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import com.borax12.materialdaterangepicker.time.RadialPickerLayout;
import com.borax12.materialdaterangepicker.time.TimePickerDialog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import corp.kairos.adamastor.Animation.AnimationCompactActivity;
import corp.kairos.adamastor.Home.HomeActivity;
import corp.kairos.adamastor.R;
import corp.kairos.adamastor.Settings.Settings;
import corp.kairos.adamastor.UserContext;


public class Onboard5ScheduleActivity extends AnimationCompactActivity implements TimePickerDialog.OnTimeSetListener{
    private final int ONBOARDING_SCHEDULE = 5;
    private final int ONBOARDING_FINISH = 6;

    private Settings settingsUser;
    private UserContext workContext;
    private int screen = ONBOARDING_SCHEDULE;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboard5_schedule);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        this.settingsUser = Settings.getInstance(this);

        GregorianCalendar from = new GregorianCalendar();
        GregorianCalendar to = new GregorianCalendar();
        from.set(GregorianCalendar.HOUR_OF_DAY,9);
        from.set(GregorianCalendar.MINUTE,0);
        to.set(GregorianCalendar.HOUR_OF_DAY,17);
        to.set(GregorianCalendar.MINUTE,0);

        workContext = settingsUser.getUserContext("Work");
        workContext.setTimes(from, to);
        settingsUser.setUserContext(workContext);
    }

    @Override
    protected void onResume() {
        super.onResume();
        super.setAnimation("right");
    }

    @Override
    public void onBackPressed() {
            super.setAnimation("left");
            super.onBackPressed();
    }

    public void showTimePicker(View v) {
        TimePickerDialog tpd = TimePickerDialog.newInstance(this, 9, 0, true, 17, 0);
        tpd.show(getFragmentManager(), "Timepickerdialog");
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int hourOfDayEnd, int minuteEnd) {
        GregorianCalendar from = new GregorianCalendar();
        GregorianCalendar to = new GregorianCalendar();
        from.set(GregorianCalendar.HOUR_OF_DAY,hourOfDay);
        from.set(GregorianCalendar.MINUTE,minute);
        to.set(GregorianCalendar.HOUR_OF_DAY,hourOfDayEnd);
        to.set(GregorianCalendar.MINUTE,minuteEnd);

        workContext.setTimes(from, to);
        settingsUser.setUserContext(workContext);

        TextView timeFrom = findViewById(R.id.work_from);
        TextView timeTo = findViewById(R.id.work_to);
        DateFormat formatter = new SimpleDateFormat("HH:mm");
        timeFrom.setText(formatter.format(from.getTime()));
        timeTo.setText(formatter.format(to.getTime()));
    }

    public void goNext(View v) {
        settingsUser.saveContextSettings();
        Intent i = new Intent(this, Onboard6FinalActivity.class);
        startActivity(i);
    }
}
