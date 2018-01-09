package corp.kairos.adamastor.ContextList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;

import corp.kairos.adamastor.AllApps.AllAppsMenuAdapter;
import corp.kairos.adamastor.AppDetail;
import corp.kairos.adamastor.R;
import corp.kairos.adamastor.UserContext;

import static corp.kairos.adamastor.Util.getObjectByIndex;

public class ContextListAdapter extends BaseAdapter {

    private final UserContext[] userContexts;
    private final LayoutInflater inflater;
    private final Context context;


    public ContextListAdapter(Context ctx, UserContext[] userContexts){
        this.context = ctx;
        this.inflater = ((Activity) ctx).getLayoutInflater();
        this.userContexts = userContexts;
    }

    @Override
    public int getCount() {
        return userContexts.length;
    }

    @Override
    public Object getItem(int position) {
        return userContexts[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        if (convertView == null) {
            row = inflater.inflate(R.layout.context_list_item, parent, false);
        }

        TextView contextTitle = row.findViewById(R.id.context_title);
        GridView contextAppList = row.findViewById(R.id.context_app_list);
        UserContext currentContext = (UserContext) this.getItem(position);
        final Set<AppDetail> currentContextApps = new HashSet<>(currentContext.getContextApps());

        contextTitle.setText(currentContext.getContextName());

        contextAppList.setAdapter(new AllAppsMenuAdapter(this.context, currentContextApps));

        contextAppList.setOnItemClickListener((av, v, pos, id) -> {
            AppDetail app = (AppDetail) getObjectByIndex(pos, currentContextApps);
            Intent i = context.getPackageManager().getLaunchIntentForPackage(app.getName());
            context.startActivity(i);
        });

        return row;
    }
}
