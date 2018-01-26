package corp.kairos.adamastor.Home;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;

import corp.kairos.adamastor.AllApps.AllAppsMenuAdapter;
import corp.kairos.adamastor.AppDetails;
import corp.kairos.adamastor.R;
import corp.kairos.adamastor.UserContext;


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

        GridView grid = (GridView) parentLayout.findViewById(R.id.home_grid_view);
        grid.setNumColumns(min);
        CustomAdapter adapter = new CustomAdapter(getActivity(), appsToDisplay);
        grid.setAdapter(adapter);

        return parentLayout;
    }

    private class CustomAdapter extends AllAppsMenuAdapter {

        public CustomAdapter(Context context, Set apps) {
            super(context, apps);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            TextView appLabel = (TextView) view.getRootView().findViewById(R.id.app_text);
            appLabel.setTextColor(ContextCompat.getColor(this.context, R.color.primaryTextColor));
            return view;
        }
    }

}
