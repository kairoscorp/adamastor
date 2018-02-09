package corp.kairos.adamastor.Home;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import corp.kairos.adamastor.AllApps.AllAppsRecyclerViewAdapter;
import corp.kairos.adamastor.AppDetails;
import corp.kairos.adamastor.ContextList.AppViewHolder;
import corp.kairos.adamastor.R;
import corp.kairos.adamastor.Settings.Settings;
import corp.kairos.adamastor.UserContext;


public class PlaceholderFragment extends Fragment {
    private static String PARCELABLE_KEY = "userContext";

    UserContext fragmentContext;
    RecyclerView mGrid;
    AllAppsRecyclerViewAdapter mAdapter;
    private boolean isFirstStart = true;

    public PlaceholderFragment() {
        // Required empty public constructor
    }

    public static PlaceholderFragment newInstance(UserContext userContext) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(PlaceholderFragment.PARCELABLE_KEY, userContext);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.fragmentContext = getArguments().getParcelable(PlaceholderFragment.PARCELABLE_KEY);
        assert this.fragmentContext != null;
        for (AppDetails app : this.fragmentContext.getContextApps())
            try {
                app.loadIcon(getActivity().getPackageManager());
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LinearLayout parentLayout = (LinearLayout) inflater.inflate(R.layout.context_fragment, container, false);

        // TODO: Change for most used apps?
        int min = Math.min(4, fragmentContext.getContextApps().size());
        Set<AppDetails> appsToDisplay = new HashSet<>(fragmentContext.getContextApps().subList(0, min));

        mGrid = parentLayout.findViewById(R.id.home_grid_view);
        mGrid.setLayoutManager(new GridLayoutManager(getContext(), min){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        mAdapter = new AllAppsRecyclerViewAdapter(getContext(), appsToDisplay) {
            @Override
            public void onBindViewHolder(AppViewHolder holder, int position) {
                super.onBindViewHolder(holder, position);
                holder.labelView.setTextColor(Color.WHITE);
            }
        };

        mGrid.setAdapter(mAdapter);

        return parentLayout;
    }

    // TODO: Find a more elegant way to update the fragment content
    @Override
    public void onStart() {
        super.onStart();
        if (isFirstStart)
            isFirstStart = false;
        else
            updateFragmentContent();
    }

    public void updateFragmentContent() {
        Settings settings = Settings.getInstance(getContext());
        fragmentContext = settings.getUserContext(fragmentContext.getContextName());
        int min = Math.min(4, fragmentContext.getContextApps().size());
        mGrid.setLayoutManager(new GridLayoutManager(getContext(), min){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        List<AppDetails> appsToDisplay = new ArrayList<>(fragmentContext.getContextApps().subList(0, min));
        mAdapter.updateData(appsToDisplay);
    }
}
