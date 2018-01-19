package corp.kairos.adamastor.ContextList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import corp.kairos.adamastor.AppDetail;
import corp.kairos.adamastor.R;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;


public class ContextSection extends StatelessSection {
    // TODO: Make this a metric with the screen height and the number of sections
    public static int MAX_NUMBER_OF_APPS_COMPRESSED = 4;
    public static boolean INITIAL_EXPANDED_STATE = false;

    private Context appContext;
    private SectionedRecyclerViewAdapter mAdapter;
    private String contextName;
    private List<AppDetail> contextApps;
    private boolean isExpanded = INITIAL_EXPANDED_STATE;


    public ContextSection(Context appContext, SectionedRecyclerViewAdapter adapter,
                          String contextName, List<AppDetail> contextApps) {
    super(new SectionParameters.Builder(R.layout.allapps_app)
                .headerResourceId(R.layout.context_header)
                .build());
        this.appContext = appContext;
        this.mAdapter = adapter;
        this.contextName = contextName;
        this.contextApps = contextApps;
    }

    @Override
    public int getContentItemsTotal() {
        return this.isExpanded ? contextApps.size() : Math.min(MAX_NUMBER_OF_APPS_COMPRESSED, contextApps.size());
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new AppViewHolder(view);
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
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder){
        HeaderViewHolder headerHolder = (HeaderViewHolder) holder;

        headerHolder.contextTitleView.setText(this.contextName);
        if (contextApps.size() <= MAX_NUMBER_OF_APPS_COMPRESSED)
            headerHolder.expandArrowView.setVisibility(View.GONE);

        headerHolder.rootView.setOnClickListener((View v) -> {
            isExpanded = !isExpanded;
            headerHolder.expandArrowView.setImageResource(
                    isExpanded ? R.drawable.ic_keyboard_arrow_up_black_24dp : R.drawable.ic_keyboard_arrow_down_black_24dp
            );
            mAdapter.notifyDataSetChanged();
        });
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
            // TODO: Make design default like this
            labelView.setTextColor(
                ResourcesCompat.getColor(
                    appContext.getResources(), R.color.secondaryTextColor, null
                ));
            labelView.setTextSize(18);
        }
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        View rootView;
        TextView contextTitleView;
        ImageView expandArrowView;

        HeaderViewHolder(View view) {
            super(view);

            rootView = view;
            contextTitleView = (TextView) view.findViewById(R.id.context_title);
            expandArrowView = (ImageView) view.findViewById(R.id.expand_arrow);
        }
    }
}
