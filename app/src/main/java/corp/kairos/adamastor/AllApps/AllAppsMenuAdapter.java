package corp.kairos.adamastor.AllApps;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;

import corp.kairos.adamastor.AppDetails;
import corp.kairos.adamastor.R;

import static corp.kairos.adamastor.Util.getObjectByIndex;

public class AllAppsMenuAdapter extends ArrayAdapter {
    protected Context context;
    private Set<AppDetails> allApps;
    private Set<AppDetails> filteredApps;

    public AllAppsMenuAdapter(Context context, Set<AppDetails> allApps) {
        super(context, R.layout.activity_allapps);
        this.context = context;
        this.allApps = allApps;
        this.filteredApps = allApps;
    }

    @Override
    public int getCount() {
        return filteredApps.size();
    }

    @Override
    public Object getItem(int position) {
        return getObjectByIndex(position, this.filteredApps);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View item = convertView;

        if (item == null)
        {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            item = inflater.inflate(R.layout.allapps_app, parent, false);
        }

        AppDetails app = (AppDetails) getObjectByIndex(position, this.filteredApps);

        TextView textViewTitle = (TextView) item.findViewById(R.id.app_text);
        ImageView imageViewIte = (ImageView) item.findViewById(R.id.app_image);

        textViewTitle.setText(app.getLabel());
        imageViewIte.setImageDrawable(app.getIcon());

        item.setOnClickListener(view -> {
            Intent i = this.context.getPackageManager().getLaunchIntentForPackage(app.getPackageName());
            context.startActivity(i);
        });

        return item;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();

                //If there's nothing to filter on, return the original data for your list
                if(charSequence == null || charSequence.length() == 0) {
                    results.values = allApps;
                    results.count = allApps.size();
                } else {
                    Set<AppDetails> filterResults = new HashSet<>();

                    for (AppDetails app : allApps)
                        if (app.getLabel().toLowerCase().contains(charSequence.toString().toLowerCase()))
                            filterResults.add(app);

                    results.values = filterResults;
                    results.count = filterResults.size();
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredApps = (Set<AppDetails>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

}