package corp.kairos.adamastor.ContextList;

import android.os.Bundle;
import android.widget.ListView;

import corp.kairos.adamastor.AllApps.AllAppsActivity;
import corp.kairos.adamastor.AnimActivity;
import corp.kairos.adamastor.Home.HomeActivity;
import corp.kairos.adamastor.R;
import corp.kairos.adamastor.Settings.Settings;
import corp.kairos.adamastor.UserContext;

public class ContextListActivity extends AnimActivity {

    public static UserContext[] userContexts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_context_list);

        // Load Contexts from Settings
        Settings settings = new Settings(getApplicationContext());
        userContexts = settings.getUserContextsAsArray();

        // Set side activities
        setLeftActivity(HomeActivity.class);
        setRightActivity(AllAppsActivity.class);

        // Add adapter to list view
        ListView allContextList = findViewById(R.id.all_contexts_list);
        allContextList.setAdapter(new ContextListAdapter(this, userContexts));
    }

}
