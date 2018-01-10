package corp.kairos.adamastor.Onboarding;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import java.util.Set;

import corp.kairos.adamastor.AllApps.AppDetail;
import corp.kairos.adamastor.R;
import corp.kairos.adamastor.UserContext;

import static corp.kairos.adamastor.Util.getObjectByIndex;

/**
 * Created by jlsilva94 on 28/12/2017.
 */

public class AppCheckAdapter extends ArrayAdapter {
    private Context context;
    public Set<AppDetail> apps;
    public UserContext uc;

    public AppCheckAdapter(Context context, Set apps, UserContext uc) {
        super(context, R.layout.apps_onboarding);
        this.context = context;
        this.apps = apps;
        this.uc = uc;
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

    private class ViewHolder {
        CheckBox nameBox;
        ImageView icon;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.single_app_onboarding, parent, false);

            holder = new ViewHolder();
            holder.icon = row.findViewById(R.id.app_check_image);
            holder.nameBox = row.findViewById(R.id.app_check_box);
            row.setTag(holder);


            holder.nameBox.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    AppDetail app = (AppDetail) cb.getTag();
                    if (cb.isChecked()) {
                        uc.addApp(app);
                    } else {
                        uc.removeApp(app);
                    }


                }
            });
        } else {
            holder = (ViewHolder) row.getTag();
        }

        AppDetail app = (AppDetail) getObjectByIndex(position, this.apps);

        holder.nameBox.setText(app.getLabel());
        holder.icon.setImageDrawable(app.getIcon());
        holder.nameBox.setChecked(uc.appExists(app));
        holder.nameBox.setTag(app);

        return row;
    }
}
