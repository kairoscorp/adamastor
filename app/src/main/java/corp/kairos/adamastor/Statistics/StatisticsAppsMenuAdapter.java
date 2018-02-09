package corp.kairos.adamastor.Statistics;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import corp.kairos.adamastor.AppDetails;
import corp.kairos.adamastor.R;

import static corp.kairos.adamastor.Util.getObjectByIndex;


public class StatisticsAppsMenuAdapter extends ArrayAdapter {
    private Context context;
    private Set<AppDetails> apps;
    private long mostUsedTime;

    public StatisticsAppsMenuAdapter(Context context, Set<AppDetails> apps) {
        super(context, R.layout.activity_allapps);
        this.context = context;
        this.apps = apps;
        this.mostUsedTime = apps.isEmpty() ? 0L : apps.iterator().next().getUsageStatistics();
    }

    @Override
    public int getCount() {
        return apps.size();
    }

    @Override
    public Object getItem(int position) {
        return getObjectByIndex(position, this.apps);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
        {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.single_app_statistics, parent, false);
        }

        AppDetails app = (AppDetails) getObjectByIndex(position, apps);
        ImageView appIcon = (ImageView)convertView.findViewById(R.id.app_statistics_image);
        appIcon.setImageDrawable(app.getIcon());

        TextView appLabel = (TextView)convertView.findViewById(R.id.app_statistcs_name);
        appLabel.setText(app.getLabel());

        ProgressBar appProgressBar = (ProgressBar)convertView.findViewById(R.id.app_statistics_progress_bar);
        long percentage = (app.getUsageStatistics() * 100) / mostUsedTime;
        appProgressBar.setProgress((int) percentage);

        TextView appTimeUsage = (TextView)convertView.findViewById(R.id.app_statistcs_time);

        long usageStatistics = app.getUsageStatistics();
        String timeUsage = "";
        long time = TimeUnit.MILLISECONDS.toHours(usageStatistics);
        if(time < 1) {
            time = TimeUnit.MILLISECONDS.toMinutes(usageStatistics);
            if(time < 1) {
                time = TimeUnit.MILLISECONDS.toSeconds(usageStatistics);
                if(time < 1) {
                    time = usageStatistics;
                    timeUsage = String.format("%dmilli",time);
                } else {
                    timeUsage = String.format("%dsec",time);
                }
            } else {
                timeUsage = String.format("%dmin",time);
            }
        } else {
            timeUsage = String.format("%dh%02dm",
                    TimeUnit.MILLISECONDS.toHours(usageStatistics),
                    TimeUnit.MILLISECONDS.toMinutes(usageStatistics) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(usageStatistics))
            );
        }

        appTimeUsage.setText(timeUsage + " . ("+(int)percentage+"%)");

        return convertView;
    }
}
