package corp.kairos.adamastor.ContextList;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

import corp.kairos.adamastor.Animation.AnimationActivity;
import corp.kairos.adamastor.Home.HomeActivity;
import corp.kairos.adamastor.R;
import corp.kairos.adamastor.Settings.Settings;
import corp.kairos.adamastor.UserContext;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class ContextListActivity extends AnimationActivity {
    public static int NUMBER_OF_COLUMNS = 4;
    private RecyclerView mRecyclerView;
    public SectionedRecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_context_list);
        mRecyclerView = (RecyclerView) findViewById(R.id.all_contexts_recycler_view);

        // Load Contexts from Settings
        Settings settings = Settings.getInstance(getApplicationContext());

        List<UserContext> userContexts = settings.getOrderedUserContexts();

        // Set side activities
        super.setAnimation("left");
        super.setLeftActivity(HomeActivity.class);

        setupViews(userContexts);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setupViews(List<UserContext> userContexts) {
        mAdapter = new SectionedRecyclerViewAdapter();
        // TODO: Reorder apps by most relevant first
        for (int i = 0; i < userContexts.size(); i++) {
            UserContext aContext = userContexts.get(i);
            if (aContext.getContextApps().size() > 0)
                mAdapter.addSection(
                    new ContextSection(
                        this,
                        this.mAdapter,
                        aContext.getContextName(),
                        aContext.getContextApps(),
                        (i == 0 || i == userContexts.size()-1)
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


    public void selectContextApps(View v) {
        startActivity(new Intent(getApplicationContext(),EditContextAppsActivity.class));

    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            if (getFragmentManager().findFragmentByTag("OPTIONS") != null) {
                getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag("OPTIONS")).commit();
            }
            if (getFragmentManager().findFragmentByTag("CONTEXT") != null) {
                getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag("CONTEXT")).commit();
            }
            if (findViewById(R.id.select_context) != null) {
                ((ViewGroup) findViewById(R.id.select_context).getParent()).removeView(findViewById(R.id.select_context));
                getWindow().setStatusBarColor(0);
                getWindow().setNavigationBarColor(0);
            }

        } else {
            super.onBackPressed();
        }
    }
}
