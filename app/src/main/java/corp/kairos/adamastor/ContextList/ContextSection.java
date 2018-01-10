package corp.kairos.adamastor.ContextList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import corp.kairos.adamastor.AppDetail;
import corp.kairos.adamastor.R;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;


public class ContextSection extends StatelessSection {
    String contextName;
    List<AppDetail> contextApps;
    Context appContext;

    public ContextSection(Context appContext, String contextName, List<AppDetail> contextApps) {
        super(new SectionParameters.Builder(R.layout.allapps_app)
                .headerResourceId(R.layout.context_header)
                .build());
        this.appContext = appContext;
        this.contextName = contextName;
        this.contextApps = contextApps;
    }

    @Override
    public int getContentItemsTotal() {
        return contextApps.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new AppViewHolder(view);
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        AppViewHolder itemHolder = (AppViewHolder) holder;
        AppDetail currentItem = this.contextApps.get(position);

        itemHolder.iconView.setImageDrawable(currentItem.getIcon());
        itemHolder.labelView.setText(currentItem.getLabel());
        itemHolder.packageName = currentItem.getPackageName();

        itemHolder.rootView.setOnClickListener((View v) -> {
            Intent i = this.appContext.getPackageManager().getLaunchIntentForPackage(currentItem.getPackageName());
            this.appContext.startActivity(i);

        });
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder){
        HeaderViewHolder headerHolder = (HeaderViewHolder) holder;

        headerHolder.contextTitleView.setText(this.contextName);
    }


    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView contextTitleView;

        HeaderViewHolder(View view) {
            super(view);
            contextTitleView = (TextView) view.findViewById(R.id.context_title);
        }
    }

    private class AppViewHolder extends RecyclerView.ViewHolder {
        View rootView;
        TextView labelView;
        ImageView iconView;
        String packageName;

        AppViewHolder(View itemView) {
            super(itemView);

            rootView = itemView;
            iconView = (ImageView) itemView.findViewById(R.id.app_image);
            labelView = (TextView) itemView.findViewById(R.id.app_text);
            labelView.setTextColor(Color.WHITE);
        }
    }

}
