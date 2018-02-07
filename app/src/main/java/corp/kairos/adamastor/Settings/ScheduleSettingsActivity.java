package corp.kairos.adamastor.Settings;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import corp.kairos.adamastor.Onboarding.Onboard5ScheduleActivity;
import corp.kairos.adamastor.R;


public class ScheduleSettingsActivity extends Onboard5ScheduleActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAnimation("left");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Button save = findViewById(R.id.next3);
        save.setText("save changes");
        findViewById(R.id.worktime_title).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.worktime_description)).setTextSize(16);

        super.from = workContext.getInit();
        super.to = workContext.getEnd();

        TextView timeFrom = findViewById(R.id.work_from);
        TextView timeTo = findViewById(R.id.work_to);
        DateFormat formatter = new SimpleDateFormat("HH:mm");
        timeFrom.setText(formatter.format(from.getTime()));
        timeTo.setText(formatter.format(to.getTime()));
    }

    @Override
    public boolean onSupportNavigateUp() {
        setAnimation("right");
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        setAnimation("right");
        finish();
    }

    @Override
    public void goNext(View v) {
        super.workContext.setTimes(from, to);
        super.settingsUser.setUserContext(workContext);
        super.settingsUser.saveContextSettings();
        setAnimation("right");
        finish();
    }
}
