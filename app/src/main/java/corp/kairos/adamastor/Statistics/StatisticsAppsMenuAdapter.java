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
    private long finalTotal;

    public StatisticsAppsMenuAdapter(Context context, Set<AppDetails> apps) {
        super(context, R.layout.activity_allapps);
        this.context = context;
        this.apps = apps;
        finalTotal = 0;
        for(AppDetails app : apps) {
            finalTotal += app.getTotalUsedTime();
        }
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
            convertView = inflater.inflate(R.layout.statistics_app_layout, parent, false);
        }

        AppDetails app = (AppDetails) getObjectByIndex(position, apps);
        ImageView appIcon = (ImageView)convertView.findViewById(R.id.app_stats_image);
        appIcon.setImageDrawable(app.getIcon());

        TextView appLabel = (TextView)convertView.findViewById(R.id.app_stats_label);
        appLabel.setText(app.getLabel());

        ProgressBar appProgressBar = (ProgressBar)convertView.findViewById(R.id.app_stats_progress_bar);
        long percentage = (app.getTotalUsedTime() * 100) / finalTotal;
        appProgressBar.setProgress((int) percentage);

        TextView appTimeUsage = (TextView)convertView.findViewById(R.id.app_stats_usage_time);

        appTimeUsage.setText(TimeUnit.MILLISECONDS.toMinutes(app.getTotalUsedTime()) + " minutes");

        TextView appPercentage = (TextView)convertView.findViewById(R.id.app_stats_percentage);
        String percentageText = percentage + " %";
        if(percentage < 1) {
            percentageText = "< 0 %";
        }
        appPercentage.setText(percentageText);

        return convertView;
    }
}
