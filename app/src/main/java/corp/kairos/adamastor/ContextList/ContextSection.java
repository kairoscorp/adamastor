package corp.kairos.adamastor.ContextList;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
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

    private Context mAppContext;
    private SectionedRecyclerViewAdapter mAdapter;
    private String mContextName;
    private List<AppDetails> mContextApps;
    private boolean mIsExpanded;


    public ContextSection(Context appContext, SectionedRecyclerViewAdapter adapter,
                          String contextName, List<AppDetails> contextApps) {
        super(new SectionParameters.Builder(R.layout.allapps_app)
                .headerResourceId(R.layout.context_header)
                .footerResourceId(R.layout.context_footer)
                .build());
        mAppContext = appContext;
        mAdapter = adapter;
        mContextName = contextName;
        mContextApps = new ArrayList<>(contextApps);
        // TODO: Find a more elegant way to deal with coloring the background of the section 1)
        int numberOfPlaceholderApps = ContextListActivity.NUMBER_OF_COLUMNS - mContextApps.size() % ContextListActivity.NUMBER_OF_COLUMNS;
        if (numberOfPlaceholderApps != ContextListActivity.NUMBER_OF_COLUMNS)
            for (int i = 0; i < numberOfPlaceholderApps; i++) {
                mContextApps.add(new AppDetails("", "", null));
            }
        mIsExpanded = initialExpanded;
    }

    @Override
    public int getContentItemsTotal() {
        return mIsExpanded ? mContextApps.size() : Math.min(MAX_NUMBER_OF_APPS_COMPRESSED, mContextApps.size());
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        // TODO: Find a more elegant way to deal with coloring the background of the section 2)
        View rootView = view.getRootView();
        rootView.setBackgroundColor(Color.WHITE);

        return new AppViewHolder(view, this.mAppContext);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        AppViewHolder itemHolder = (AppViewHolder) holder;
        AppDetails currentItem = mContextApps.get(position);

        itemHolder.iconView.setImageDrawable(currentItem.getIcon());
        itemHolder.labelView.setText(currentItem.getLabel());
        itemHolder.app = currentItem;
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        HeaderViewHolder headerHolder = (HeaderViewHolder) holder;

        headerHolder.contextTitleView.setText(this.mContextName);
        int iconId = UserContext.getContextIcon(this.mContextName);
        headerHolder.contextTitleView.setCompoundDrawablesWithIntrinsicBounds(iconId, 0, 0, 0);

        if (mContextApps.size() <= MAX_NUMBER_OF_APPS_COMPRESSED)
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
                mIsExpanded = !mIsExpanded;
                expandArrowView.setImageResource(
                        mIsExpanded ? R.drawable.ic_expand_less_black_24dp : R.drawable.ic_expand_more_black_24dp
                );
                mAdapter.notifyDataSetChanged();
            });
        }
    }


}
