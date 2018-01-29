package corp.kairos.adamastor.ContextList;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import corp.kairos.adamastor.R;

public class AppViewHolder extends RecyclerView.ViewHolder {
    public View rootView;
    public TextView labelView;
    public ImageView iconView;
    public String packageName;

    public AppViewHolder(View itemView, Context appContext) {
        super(itemView);

        rootView = itemView;
        iconView = (ImageView) itemView.findViewById(R.id.app_image);
        labelView = (TextView) itemView.findViewById(R.id.app_text);

        labelView.setTextColor(
                ResourcesCompat.getColor(
                        appContext.getResources(), R.color.secondaryTextColor, null
                ));

        rootView.setOnClickListener(v -> {
            Intent i = appContext.getPackageManager().getLaunchIntentForPackage(this.packageName);
            appContext.startActivity(i);
        });

    }
}
