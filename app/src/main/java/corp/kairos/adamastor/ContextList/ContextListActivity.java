package corp.kairos.adamastor.ContextList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.apache.commons.lang3.ArrayUtils;

import corp.kairos.adamastor.Animation.AnimationActivity;
import corp.kairos.adamastor.Home.HomeActivity;
import corp.kairos.adamastor.R;
import corp.kairos.adamastor.Settings.Settings;
import corp.kairos.adamastor.UserContext;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class ContextListActivity extends AnimationActivity {
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
        Settings settings = Settings.getInstance(getApplicationContext());
        userContexts = ArrayUtils.addAll(settings.getUserContextsAsArray(), settings.getZeroContext());

        // Set side activities
        super.setAnimation("left");
        super.setLeftActivity(HomeActivity.class);

        setupViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupViews();
    }

    private void setupViews() {
        mAdapter = new SectionedRecyclerViewAdapter();
        // TODO: Reorder contexts by active first
        // TODO: Reorder apps by most relevant first
        for (int i = 0; i < userContexts.length; i++) {
            UserContext aContext = userContexts[i];
            if (aContext.getContextApps().size() > 0)
                mAdapter.addSection(
                    new ContextSection(
                        this,
                        this.mAdapter,
                        aContext.getContextName(),
                        aContext.getContextApps(),
                        (i == 0 || i == userContexts.length-1)
                    )
                );
        }

        GridLayoutManager glm = new GridLayoutManager(this, NUMBER_OF_COLUMNS);
        glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (mAdapter.getSectionItemViewType(position)) {
                    case SectionedRecyclerViewAdapter.VIEW_TYPE_HEADER:
                    case SectionedRecyclerViewAdapter.VIEW_TYPE_FOOTER:
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
