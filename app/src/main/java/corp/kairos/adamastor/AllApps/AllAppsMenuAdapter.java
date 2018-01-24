package corp.kairos.adamastor.AllApps;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Set;

import corp.kairos.adamastor.AppDetails;
import corp.kairos.adamastor.R;

import static corp.kairos.adamastor.Util.getObjectByIndex;

public class AllAppsMenuAdapter extends ArrayAdapter {
    private Context context;
    private Set<AppDetails> apps;

    public AllAppsMenuAdapter(Context context, Set apps) {
        super(context, R.layout.allapps_menu);
        this.context = context;
        this.apps = apps;
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
        View row = convertView;

        if (row == null)
        {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.allapps_app, parent, false);
        }

        AppDetails app = (AppDetails) getObjectByIndex(position, this.apps);

        TextView textViewTitle = (TextView) row.findViewById(R.id.app_text);
        ImageView imageViewIte = (ImageView) row.findViewById(R.id.app_image);

        textViewTitle.setText(app.getLabel());
        imageViewIte.setImageDrawable(app.getIcon());


        return row;
    }
}