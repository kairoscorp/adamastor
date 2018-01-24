package corp.kairos.adamastor.Home;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.List;

import corp.kairos.adamastor.AppDetails;
import corp.kairos.adamastor.ContextList.AppViewHolder;
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
        List<AppDetails> appsToDisplay = fragmentContext.getContextApps().subList(0, min);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView list = (RecyclerView) parentLayout.findViewById(R.id.home_recycler_view);

        list.setLayoutManager(layoutManager);
        list.setAdapter(new ListAdapter(appsToDisplay));

        return parentLayout;
    }

    class ListAdapter extends RecyclerView.Adapter<AppViewHolder> {

        List<AppDetails> appsToDisplay;

        public ListAdapter(List<AppDetails> appsToDisplay) {
            this.appsToDisplay = appsToDisplay;
        }

        @Override
        public AppViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.allapps_app, parent, false);
            return new AppViewHolder(view, parent.getContext());
        }

        @Override
        public void onBindViewHolder(AppViewHolder holder, int position) {
            AppDetails boundApp = appsToDisplay.get(position);
            holder.iconView.setImageDrawable(boundApp.getIcon());
            holder.labelView.setText(boundApp.getLabel());
        }

        @Override
        public int getItemCount() {
            return appsToDisplay.size();
        }
    }
}
