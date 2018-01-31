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
import corp.kairos.adamastor.Util;

import static corp.kairos.adamastor.Util.getObjectByIndex;


public class StatisticsAppsMenuAdapter extends ArrayAdapter {
    private Context context;
    private Set<AppDetails> apps;
    private long finalTotal;

    public StatisticsAppsMenuAdapter(Context context, Set<AppDetails> apps) {
        super(context, R.layout.activity_allapps);
        this.context = context;
        this.apps = apps;
        this.finalTotal = apps.iterator().hasNext() ? apps.iterator().next().getUsageStatistics() : 0L;
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
        long percentage = (app.getUsageStatistics() * 100) / finalTotal;
        appProgressBar.setProgress((int) percentage);

        TextView appTimeUsage = (TextView)convertView.findViewById(R.id.app_statistcs_time);

        String measure = "hours";
        long time = TimeUnit.MILLISECONDS.toHours(app.getUsageStatistics());
        if(time < 1) {
            time = TimeUnit.MILLISECONDS.toMinutes(app.getUsageStatistics());
            measure = "minutes";
            if(time < 1) {
                time = TimeUnit.MILLISECONDS.toSeconds(app.getUsageStatistics());
                measure = "seconds";
                if(time < 1) {
                    time = app.getUsageStatistics();
                    measure = "milliseconds";
                }
            }
        }

        appTimeUsage.setText(time + " " + measure);

        return convertView;
    }
}
