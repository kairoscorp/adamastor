package corp.kairos.adamastor.Home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;

import corp.kairos.adamastor.AllApps.AllAppsActivity;
import corp.kairos.adamastor.AllApps.AllAppsRecyclerViewAdapter;
import corp.kairos.adamastor.AppDetails;
import corp.kairos.adamastor.ContextList.AppViewHolder;
import corp.kairos.adamastor.R;
import corp.kairos.adamastor.UserContext;

import static corp.kairos.adamastor.ContextList.ContextListActivity.NUMBER_OF_COLUMNS;
import static corp.kairos.adamastor.Util.getObjectByIndex;


public class PlaceholderFragment extends Fragment {
    private static String PARCELABLE_KEY = "userContext";

    UserContext fragmentContext;

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

        RecyclerView grid = parentLayout.findViewById(R.id.home_grid_view);
        grid.setLayoutManager(new GridLayoutManager(getContext(), min));
        AllAppsRecyclerViewAdapter adapter = new AllAppsRecyclerViewAdapter(getContext(), appsToDisplay) {
            @Override
            public void onBindViewHolder(AppViewHolder holder, int position) {
                super.onBindViewHolder(holder, position);
                holder.labelView.setTextColor(Color.WHITE);
            }
        };


        grid.setAdapter(adapter);

        return parentLayout;
    }

}
