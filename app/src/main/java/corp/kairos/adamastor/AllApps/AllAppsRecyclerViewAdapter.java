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

    private Context mAppContext;
    private List<AppDetails> mAppsToDisplay;
    private final List<AppDetails> mInitialApps;
    private final LayoutInflater mInflater;

    public AllAppsRecyclerViewAdapter(Context appContext, Set<AppDetails> appsToDisplay) {
        this.mAppContext = appContext;
        this.mInflater = LayoutInflater.from(appContext);
        this.mInitialApps = new ArrayList<>(appsToDisplay);
        this.mAppsToDisplay = new ArrayList<>(appsToDisplay);
    }

    @Override
    public AppViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.allapps_app, parent, false);
        return new AppViewHolder(view, this.mAppContext);
    }

    @Override
    public void onBindViewHolder(AppViewHolder holder, int position) {
        AppDetails currentItem = this.mAppsToDisplay.get(position);

        holder.iconView.setImageDrawable(currentItem.getIcon());
        holder.labelView.setText(currentItem.getLabel());
        holder.packageName = currentItem.getPackageName();
    }

    @Override
    public int getItemCount() {
        return mAppsToDisplay.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();

                // If there's nothing to filter on, return the original data for your list
                if(charSequence == null || charSequence.length() == 0) {
                    results.values = mInitialApps;
                    results.count = mInitialApps.size();
                } else {
                    List<AppDetails> filterResults = new ArrayList<>();

                    for (AppDetails app : mInitialApps)
                        if (app.getLabel().toLowerCase().contains(charSequence.toString().toLowerCase()))
                            filterResults.add(app);

                    results.values = filterResults;
                    results.count = filterResults.size();
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                List<AppDetails> filteredApps = (List<AppDetails>) filterResults.values;
                applyAndAnimateRemovals(filteredApps);
                applyAndAnimateAdditions(filteredApps);
                applyAndAnimateMovedItems(filteredApps);
            }
        };
    }

    /**
     * Animate Filter Logic
     */
    private void applyAndAnimateRemovals(List<AppDetails> newModels) {
        for (int i = mAppsToDisplay.size() - 1; i >= 0; i--) {
            final AppDetails model = mAppsToDisplay.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<AppDetails> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final AppDetails model = newModels.get(i);
            if (!mAppsToDisplay.contains(model))
                addItem(i, model);
        }
    }

    private void applyAndAnimateMovedItems(List<AppDetails> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final AppDetails model = newModels.get(toPosition);
            final int fromPosition = mAppsToDisplay.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition)
                moveItem(fromPosition, toPosition);
        }
    }

    private void removeItem(int position) {
        mAppsToDisplay.remove(position);
        notifyItemRemoved(position);
    }

    private void addItem(int position, AppDetails model) {
        mAppsToDisplay.add(position, model);
        notifyItemInserted(position);
    }

    private void moveItem(int fromPosition, int toPosition) {
        final AppDetails model = mAppsToDisplay.remove(fromPosition);
        mAppsToDisplay.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

}
