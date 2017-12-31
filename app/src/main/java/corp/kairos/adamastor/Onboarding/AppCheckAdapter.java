package corp.kairos.adamastor.Onboarding;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import java.util.Set;

import corp.kairos.adamastor.R;

import static corp.kairos.adamastor.Util.getObjectByIndex;

/**
 * Created by jlsilva94 on 28/12/2017.
 */

public class AppCheckAdapter extends ArrayAdapter {
    private android.content.Context context;
    public Set<AppCheckDetail> apps;

    public AppCheckAdapter(android.content.Context context, Set apps) {
        super(context, R.layout.apps_onboarding);
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
            holder.icon = (ImageView) row.findViewById(R.id.app_check_image);
            holder.nameBox = (CheckBox) row.findViewById(R.id.app_check_box);
            row.setTag(holder);


            holder.nameBox.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    AppCheckDetail app = (AppCheckDetail) cb.getTag();
                    app.setSelected(cb.isChecked());
                    Log.println(Log.INFO,"CLICKED",app.label);

                }
            });
        } else {
            holder = (ViewHolder) row.getTag();
        }

        AppCheckDetail app = (AppCheckDetail) getObjectByIndex(position, this.apps);

        holder.nameBox.setText(app.label);
        holder.icon.setImageDrawable(app.icon);
        holder.nameBox.setChecked(app.isSelected());
        holder.nameBox.setTag(app);

        return row;
    }
}
