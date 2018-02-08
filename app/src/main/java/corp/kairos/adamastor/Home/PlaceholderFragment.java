package corp.kairos.adamastor.Home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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

import corp.kairos.adamastor.AppDetails;
import corp.kairos.adamastor.R;
import corp.kairos.adamastor.UserContext;

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

        GridView grid = (GridView) parentLayout.findViewById(R.id.home_grid_view);
        grid.setNumColumns(min);
        CustomAdapter adapter = new CustomAdapter(getActivity(), appsToDisplay);
        grid.setAdapter(adapter);

        return parentLayout;
    }

    private class CustomAdapter extends ArrayAdapter {

        protected Context context;
        private Set<AppDetails> allApps;
        private Set<AppDetails> filteredApps;

        public CustomAdapter(Context context, Set<AppDetails> allApps) {
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
            ImageView imageViewItem = (ImageView) item.findViewById(R.id.app_image);

            textViewTitle.setText(app.getLabel());
            textViewTitle.setTextColor(ContextCompat.getColor(this.context, R.color.primaryTextColor));
            imageViewItem.setImageDrawable(app.getIcon());

            item.setOnClickListener(view -> {
                Intent i = this.context.getPackageManager().getLaunchIntentForPackage(app.getPackageName());
                context.startActivity(i);
            });

            return item;
        }

    }

}
