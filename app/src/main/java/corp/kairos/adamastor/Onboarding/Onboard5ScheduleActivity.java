package corp.kairos.adamastor.Onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.borax12.materialdaterangepicker.time.RadialPickerLayout;
import com.borax12.materialdaterangepicker.time.TimePickerDialog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import corp.kairos.adamastor.Animation.AnimationCompactActivity;
import corp.kairos.adamastor.Home.HomeActivity;
import corp.kairos.adamastor.Onboarding.dao.OnboardStateDAO;
import corp.kairos.adamastor.Onboarding.dao.SPOnboardStateDAO;
import corp.kairos.adamastor.R;
import corp.kairos.adamastor.Settings.Settings;
import corp.kairos.adamastor.userContext.UserContext;


public class Onboard5ScheduleActivity extends AnimationCompactActivity implements TimePickerDialog.OnTimeSetListener{

    private Settings settingsUser;
    private UserContext workContext;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setAnimation("up");
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
        settingsUser.saveContextSettings(Onboard5ScheduleActivity.this);
        setContentView(R.layout.onboard6_final);
    }

    public void finish(View v) {
        OnboardStateDAO onboardStateDAO = new SPOnboardStateDAO(Onboard5ScheduleActivity.this);
        onboardStateDAO.setOnboardingDone();
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
        finish();
    }
}
