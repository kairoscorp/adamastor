package corp.kairos.adamastor;

import java.util.ArrayList;
import java.util.List;

import corp.kairos.adamastor.AllApps.AppDetail;

/**
 * Created by kiko on 23-12-2017.
 */

/*
* This class represents a context
* */
public class UserContext {
    private String contextName; //The name of the context
    private List<AppDetail> contextApps; //The list of apps associated with this context

    public UserContext(String contextName, List<AppDetail> contextApps) {
        this.contextName = contextName;
        this.contextApps = contextApps;
    }

    public String getContextName() {
        return contextName;
    }

    public List<AppDetail> getContextApps() {
        return contextApps;
    }
}
