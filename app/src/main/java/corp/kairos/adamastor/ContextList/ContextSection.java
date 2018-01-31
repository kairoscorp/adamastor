package corp.kairos.adamastor.ContextList;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;

import corp.kairos.adamastor.AppDetails;
import corp.kairos.adamastor.R;
import corp.kairos.adamastor.UserContext;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;


public class ContextSection extends StatelessSection {
    // TODO: Make this a metric with the screen height and the number of sections
    public static int MAX_NUMBER_OF_APPS_COMPRESSED = 0;
    public static boolean INITIAL_EXPANDED_STATE = false;

    private Context appContext;
    private SectionedRecyclerViewAdapter mAdapter;
    private String contextName;
    private List<AppDetails> contextApps;
    private boolean isExpanded;


    public ContextSection(Context appContext, SectionedRecyclerViewAdapter adapter,
                          String contextName, List<AppDetails> contextApps, boolean initialExpanded) {
    super(new SectionParameters.Builder(R.layout.allapps_app)
                .headerResourceId(R.layout.context_header)
                .build());
        this.appContext = appContext;
        this.mAdapter = adapter;
        this.contextName = contextName;
        this.contextApps = contextApps;
        this.isExpanded = initialExpanded;
    }

    @Override
    public int getContentItemsTotal() {
        return this.isExpanded ? contextApps.size() : Math.min(MAX_NUMBER_OF_APPS_COMPRESSED, contextApps.size());
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new AppViewHolder(view, this.appContext);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        AppViewHolder itemHolder = (AppViewHolder) holder;
        AppDetails currentItem = this.contextApps.get(position);

        itemHolder.iconView.setImageDrawable(currentItem.getIcon());
        itemHolder.labelView.setText(currentItem.getLabel());
        itemHolder.packageName = currentItem.getPackageName();
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder){
        HeaderViewHolder headerHolder = (HeaderViewHolder) holder;

        headerHolder.contextTitleView.setText(this.contextName);
        int iconId = UserContext.getContextIcon(this.contextName);
        headerHolder.contextTitleView.setCompoundDrawablesWithIntrinsicBounds(iconId, 0, 0, 0);

        if (contextApps.size() <= MAX_NUMBER_OF_APPS_COMPRESSED)
            headerHolder.expandArrowView.setVisibility(View.GONE);


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

            view.setOnClickListener(v -> {
                isExpanded = !isExpanded;
                expandArrowView.setImageResource(
                        isExpanded ? R.drawable.ic_expand_less_black_24dp : R.drawable.ic_expand_more_black_24dp
                );
                mAdapter.notifyDataSetChanged();
            });
        }
    }
}
