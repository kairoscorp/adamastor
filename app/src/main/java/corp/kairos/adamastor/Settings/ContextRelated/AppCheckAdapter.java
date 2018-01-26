package corp.kairos.adamastor.Settings.ContextRelated;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Set;

import corp.kairos.adamastor.AppDetails;
import corp.kairos.adamastor.R;
import corp.kairos.adamastor.userContext.UserContext;

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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.single_app_onboarding, parent, false);

            LinearLayout linearLayout = (LinearLayout) row.findViewById(R.id.app_check);

            linearLayout.setOnClickListener((View v) -> {
                CheckBox checkBox = (CheckBox) v.findViewById(R.id.app_check_box);
                AppDetails app = (AppDetails) checkBox.getTag();
                if (checkBox.isChecked()) {
                    checkBox.setChecked(false);
                    uc.removeApp(app);
                } else {
                    checkBox.setChecked(true);
                    uc.addApp(app);
                }
            });
        }

        AppDetails app = (AppDetails) getObjectByIndex(position, this.apps);

        CheckBox checkBox = (CheckBox) row.findViewById(R.id.app_check_box);
        ImageView imageView= (ImageView) row.findViewById(R.id.app_check_image);
        TextView textView = (TextView) row.findViewById(R.id.app_check_label);

        textView.setText(app.getLabel());
        imageView.setImageDrawable(app.getIcon());
        checkBox.setChecked(uc.appExists(app));
        checkBox.setTag(app);

        return row;
    }
}
