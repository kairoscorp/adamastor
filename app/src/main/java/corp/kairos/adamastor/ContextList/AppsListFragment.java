package corp.kairos.adamastor.ContextList;


import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.Set;

import corp.kairos.adamastor.AppDetails;
import corp.kairos.adamastor.AppsManager.AppsManager;
import corp.kairos.adamastor.R;
import corp.kairos.adamastor.Settings.ContextRelated.AppCheckAdapter;
import corp.kairos.adamastor.Settings.Settings;
import corp.kairos.adamastor.UserContext;
import corp.kairos.adamastor.onScroll;


public class AppsListFragment extends Fragment {
    private AppsManager appsManager;
    private Set<AppDetails> allApps;
    private UserContext userContext;
    private AppCheckAdapter adapter;
    private String userContextName;


    public AppsListFragment() {}

    public static AppsListFragment newInstance(UserContext userContext) {
        AppsListFragment fragment = new AppsListFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("userContext", userContext);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appsManager = AppsManager.getInstance();
        this.allApps = appsManager.getAllApps(getActivity().getPackageManager(), false);
    }

    public UserContext getUserContext () {
        return this.userContext;
    }
    public void setUserContext(UserContext uc) {
        this.userContext = uc;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RelativeLayout parentLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_context_apps_list, container, false);
        ListView appsList = parentLayout.findViewById(R.id.apps_list_view);
        FloatingActionButton save = (FloatingActionButton) parentLayout.findViewById(R.id.btn_save_check_apps);
        appsList.setOnScrollListener(new onScroll(save));
        AppCheckAdapter adapter = new AppCheckAdapter(getContext(), allApps, userContext);

        appsList.setAdapter(adapter);
        return parentLayout;
    }

}
