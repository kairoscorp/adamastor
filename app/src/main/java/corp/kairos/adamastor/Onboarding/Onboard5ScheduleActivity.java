package corp.kairos.adamastor.Onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.borax12.materialdaterangepicker.time.RadialPickerLayout;
import com.borax12.materialdaterangepicker.time.TimePickerDialog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import corp.kairos.adamastor.Animation.AnimationCompatActivity;
import corp.kairos.adamastor.R;
import corp.kairos.adamastor.Settings.Settings;
import corp.kairos.adamastor.UserContext;



public class Onboard5ScheduleActivity extends AnimationCompatActivity implements TimePickerDialog.OnTimeSetListener{
    public Settings settingsUser;
    public UserContext workContext;
    public GregorianCalendar from = new GregorianCalendar();
    public GregorianCalendar to = new GregorianCalendar();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboard5_schedule);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        this.settingsUser = Settings.getInstance(this);
        workContext = settingsUser.getUserContext("Work");

        from.set(GregorianCalendar.HOUR_OF_DAY,9);
        from.set(GregorianCalendar.MINUTE,0);
        to.set(GregorianCalendar.HOUR_OF_DAY,17);
        to.set(GregorianCalendar.MINUTE,0);
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
        TimePickerDialog tpd = TimePickerDialog.newInstance(this, from.get(Calendar.HOUR_OF_DAY), from.get(Calendar.MINUTE), true, to.get(Calendar.HOUR_OF_DAY), to.get(Calendar.MINUTE));
        tpd.show(getFragmentManager(), "Timepickerdialog");
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int hourOfDayEnd, int minuteEnd) {
        from.set(GregorianCalendar.HOUR_OF_DAY,hourOfDay);
        from.set(GregorianCalendar.MINUTE,minute);
        to.set(GregorianCalendar.HOUR_OF_DAY,hourOfDayEnd);
        to.set(GregorianCalendar.MINUTE,minuteEnd);

        TextView timeFrom = findViewById(R.id.work_from);
        TextView timeTo = findViewById(R.id.work_to);
        DateFormat formatter = new SimpleDateFormat("HH:mm");
        timeFrom.setText(formatter.format(from.getTime()));
        timeTo.setText(formatter.format(to.getTime()));
    }

    public void goNext(View v) {
        workContext.setTimes(from, to);
        settingsUser.setUserContext(workContext);
        settingsUser.saveContextSettings();
        Intent i = new Intent(this, Onboard6FinalActivity.class);
        startActivity(i);
    }
}
