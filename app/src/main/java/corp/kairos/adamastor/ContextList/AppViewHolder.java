package corp.kairos.adamastor.ContextList;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import corp.kairos.adamastor.AppDetails;
import corp.kairos.adamastor.Home.HomeActivity;
import corp.kairos.adamastor.R;

public class AppViewHolder extends RecyclerView.ViewHolder {
    public View rootView;
    public TextView labelView;
    public ImageView iconView;
    public AppDetails app;

    public AppViewHolder(View itemView, Context appContext) {
        super(itemView);

        rootView = itemView;
        iconView = (ImageView) itemView.findViewById(R.id.app_image);
        labelView = (TextView) itemView.findViewById(R.id.app_text);

        rootView.setOnClickListener(v -> {
            // TODO: Find a more elegant way to deal with coloring the background of the section 3)
            if (app.getPackageName().length() > 0) {
                Intent i = appContext.getPackageManager().getLaunchIntentForPackage(app.getPackageName());
                appContext.startActivity(i);
            }
        });
        rootView.setOnLongClickListener(v -> {
            HomeActivity.showOptions(appContext, rootView.getRootView(), app);
            return true;
        });


    }
}
