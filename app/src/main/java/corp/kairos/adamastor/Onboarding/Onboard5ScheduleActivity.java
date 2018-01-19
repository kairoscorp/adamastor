package corp.kairos.adamastor.Onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.borax12.materialdaterangepicker.time.RadialPickerLayout;
import com.borax12.materialdaterangepicker.time.TimePickerDialog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import corp.kairos.adamastor.Home.HomeActivity;
import corp.kairos.adamastor.R;
import corp.kairos.adamastor.Settings.Settings;


public class Onboard5ScheduleActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener{
    public Settings sets;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboard5_schedule);
        this.sets = new Settings(this);
        
        GregorianCalendar from = new GregorianCalendar();
        GregorianCalendar to = new GregorianCalendar();
        from.set(GregorianCalendar.HOUR_OF_DAY,9);
        from.set(GregorianCalendar.MINUTE,0);
        to.set(GregorianCalendar.HOUR_OF_DAY,17);
        to.set(GregorianCalendar.MINUTE,0);
        sets.getUserContext("Work").setTimes(from, to);
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

        sets.getUserContext("Work").setTimes(from, to);

        TextView timeFrom = findViewById(R.id.work_from);
        TextView timeTo = findViewById(R.id.work_to);
        DateFormat formatter = new SimpleDateFormat("HH:mm");
        timeFrom.setText(formatter.format(from.getTime()));
        timeTo.setText(formatter.format(to.getTime()));
    }

    public void goNext(View v) {
        sets.saveContextSettings("Work");
        setContentView(R.layout.onboard6_final);
    }

    public void finish(View v) {
        sets.setOnboardingDone();
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
        finish();
    }


}
