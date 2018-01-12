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

import corp.kairos.adamastor.AppDetail;
import corp.kairos.adamastor.R;

import static corp.kairos.adamastor.Util.getObjectByIndex;


public class StatisticsAppsMenuAdapter extends ArrayAdapter {
    private Context context;
    private Set<AppDetailStats> apps;
    private long finalTotal;

    public StatisticsAppsMenuAdapter(Context context, Set<AppDetailStats> apps) {
        super(context, R.layout.allapps_menu);
        this.context = context;
        this.apps = apps;
        finalTotal = 0;
        for(AppDetailStats app : apps) {
            finalTotal += app.getTotalTime();
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

        AppDetailStats app = (AppDetailStats) getObjectByIndex(position, apps);
        ImageView appIcon = (ImageView)convertView.findViewById(R.id.app_stats_image);
        appIcon.setImageDrawable(app.getIcon());

        TextView appLabel = (TextView)convertView.findViewById(R.id.app_stats_label);
        appLabel.setText(app.getLabel());

        ProgressBar appProgressBar = (ProgressBar)convertView.findViewById(R.id.app_stats_progress_bar);
        long percentage = (app.getTotalTime() * 100) / finalTotal;
        appProgressBar.setProgress((int) percentage);

        TextView appTimeUsage = (TextView)convertView.findViewById(R.id.app_stats_usage_time);
        appTimeUsage.setText(Math.round(app.getTotalTime() * ((10 / 6) * Math.pow(10, -5))) + " minutes");

        TextView appPercentage = (TextView)convertView.findViewById(R.id.app_stats_percentage);
        String percentageText = percentage + " %";
        if(percentage < 1) {
            percentageText = "< 0 %";
        }
        appPercentage.setText(percentageText);

        return convertView;
    }
}
