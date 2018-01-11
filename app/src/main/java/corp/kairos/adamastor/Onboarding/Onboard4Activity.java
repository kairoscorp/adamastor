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

/**
 * Created by jlsilva94 on 11/01/18.
 */

public class Onboard4Activity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener{
    public Settings sets;
    public boolean timeSet = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboard4);
        findViewById(R.id.next4).setVisibility(View.INVISIBLE);
        this.sets = new Settings(this);
    }



    public void showTimePicker(View v) {
        GregorianCalendar start = sets.getUserContext("Work").getInit();
        GregorianCalendar end = sets.getUserContext("Work").getEnd();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                this,
                start.get(GregorianCalendar.HOUR_OF_DAY),
                start.get(GregorianCalendar.MINUTE),
                true,
                end.get(GregorianCalendar.HOUR_OF_DAY),
                end.get(GregorianCalendar.MINUTE)

        );
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
        timeFrom.setText(formatter.format(sets.getUserContext("Work").getInit().getTime()));
        timeTo.setText(formatter.format(sets.getUserContext("Work").getEnd().getTime()));

        findViewById(R.id.time_text).setVisibility(View.VISIBLE);
        findViewById(R.id.next4).setVisibility(View.VISIBLE);
        findViewById(R.id.textFinal).setVisibility(View.VISIBLE);
    }

    public void goNext(View v) {
        sets.saveContextSettings("Work");
        sets.setOnboardingDone();
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
        finish();
    }


}
