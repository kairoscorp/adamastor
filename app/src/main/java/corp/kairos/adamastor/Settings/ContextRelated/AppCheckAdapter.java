package corp.kairos.adamastor.Settings.ContextRelated;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Set;

import corp.kairos.adamastor.AppDetails;
import corp.kairos.adamastor.R;
import corp.kairos.adamastor.UserContext;

import static corp.kairos.adamastor.Util.getObjectByIndex;


public class AppCheckAdapter extends ArrayAdapter {
    private Context context;
    public Set<AppDetails> apps;
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
        CheckBox checkBox;
        ImageView imageView;
        TextView textView;
        RelativeLayout relativeLayout;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.single_app_onboarding, parent, false);

            holder = new ViewHolder();
            holder.checkBox = (CheckBox) row.findViewById(R.id.app_check_box);
            holder.imageView= (ImageView) row.findViewById(R.id.app_check_image);
            holder.textView = (TextView) row.findViewById(R.id.app_check_label);
            holder.relativeLayout = (RelativeLayout) row.findViewById(R.id.app_check);
            row.setTag(holder);

            holder.relativeLayout.setOnClickListener((View v) -> {
                RelativeLayout relativeLayout = (RelativeLayout) v;
                CheckBox checkBox = (CheckBox) relativeLayout.findViewById(R.id.app_check_box);
                AppDetails app = (AppDetails) checkBox.getTag();
                if (checkBox.isChecked()) {
                    checkBox.setChecked(false);
                    uc.removeApp(app);
                } else {
                    checkBox.setChecked(true);
                    uc.addApp(app);
                }
            });
        } else {
            holder = (ViewHolder) row.getTag();
        }

        AppDetails app = (AppDetails) getObjectByIndex(position, this.apps);

        holder.textView.setText(app.getLabel());
        holder.imageView.setImageDrawable(app.getIcon());
        holder.checkBox.setChecked(uc.appExists(app));
        holder.checkBox.setTag(app);

        return row;
    }
}
