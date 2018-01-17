package corp.kairos.adamastor.ContextList;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.apache.commons.lang3.ArrayUtils;

import corp.kairos.adamastor.AllApps.AllAppsActivity;
import corp.kairos.adamastor.AnimActivity;
import corp.kairos.adamastor.Home.HomeActivity;
import corp.kairos.adamastor.R;
import corp.kairos.adamastor.Settings.Settings;
import corp.kairos.adamastor.UserContext;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class ContextListActivity extends AnimActivity {
    public static int NUMBER_OF_COLUMNS = 4;
    private UserContext[] userContexts;
    private RecyclerView mRecyclerView;
    public SectionedRecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_context_list);
        mRecyclerView = (RecyclerView) findViewById(R.id.all_contexts_recycler_view);

        // Load Contexts from Settings
        Settings settings = new Settings(getApplicationContext());
        userContexts = ArrayUtils.addAll(settings.getUserContextsAsArray(), settings.getZeroContext());

        // Set side activities
        setLeftActivity(HomeActivity.class);
        setRightActivity(AllAppsActivity.class);

        setupViews();
    }

    private void setupViews() {
        mAdapter = new SectionedRecyclerViewAdapter();

        for (UserContext aContext: userContexts) {
            if (aContext.getContextApps().size() > 0)
                mAdapter.addSection(
                    new ContextSection(
                        this,
                        this.mAdapter,
                        aContext.getContextName(),
                        aContext.getContextApps()
                    )
                );
        }


        GridLayoutManager glm = new GridLayoutManager(this, NUMBER_OF_COLUMNS);
        glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch(mAdapter.getSectionItemViewType(position)) {
                    case SectionedRecyclerViewAdapter.VIEW_TYPE_HEADER:
                        return NUMBER_OF_COLUMNS;
                    default:
                        return 1;
                }
            }
        });

        mRecyclerView.setLayoutManager(glm);
        mRecyclerView.setAdapter(mAdapter);
    }

}