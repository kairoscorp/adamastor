package corp.kairos.adamastor;

import java.util.ArrayList;
import java.util.List;

import corp.kairos.adamastor.AllApps.AppDetail;

/**
 * Created by kiko on 23-12-2017.
 */

public class UserContext {
    private String contextName;
    private List<String> contextApps;

    public UserContext(String contextName, List<String> contextApps) {
        this.contextName = contextName;
        this.contextApps = contextApps;
    }

    public String getContextName() {
        return contextName;
    }

    public List<String> getContextApps() {
        return contextApps;
    }
}
