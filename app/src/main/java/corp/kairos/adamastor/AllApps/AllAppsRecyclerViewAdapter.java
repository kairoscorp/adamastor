package corp.kairos.adamastor.AllApps;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import corp.kairos.adamastor.AppDetails;
import corp.kairos.adamastor.ContextList.AppViewHolder;
import corp.kairos.adamastor.R;

public class AllAppsRecyclerViewAdapter extends RecyclerView.Adapter<AppViewHolder> implements Filterable {

    private Context appContext;
    private List<AppDetails> appsToDisplay;
    private final List<AppDetails> initialApps;
    private final LayoutInflater inflater;

    public AllAppsRecyclerViewAdapter(Context appContext, Set<AppDetails> appsToDisplay) {
        this.appContext = appContext;
        this.inflater = LayoutInflater.from(appContext);
        List<AppDetails> apps = new ArrayList<>(appsToDisplay);
        this.initialApps = apps;
        this.appsToDisplay = apps;
    }

    @Override
    public AppViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.allapps_app, parent, false);
        return new AppViewHolder(view, this.appContext);
    }

    @Override
    public void onBindViewHolder(AppViewHolder holder, int position) {
        AppViewHolder itemHolder = (AppViewHolder) holder;
        AppDetails currentItem = this.appsToDisplay.get(position);

        itemHolder.iconView.setImageDrawable(currentItem.getIcon());
        itemHolder.labelView.setText(currentItem.getLabel());
        itemHolder.packageName = currentItem.getPackageName();
    }

    @Override
    public int getItemCount() {
        return appsToDisplay.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();

                //If there's nothing to filter on, return the original data for your list
                if(charSequence == null || charSequence.length() == 0) {
                    results.values = initialApps;
                    results.count = initialApps.size();
                } else {
                    List<AppDetails> filterResults = new ArrayList<>();

                    for (AppDetails app : initialApps)
                        if (app.getLabel().toLowerCase().contains(charSequence.toString().toLowerCase()))
                            filterResults.add(app);

                    results.values = filterResults;
                    results.count = filterResults.size();
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                appsToDisplay = (List<AppDetails>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
