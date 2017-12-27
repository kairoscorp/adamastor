package corp.kairos.adamastor;

import java.util.ArrayList;
import java.util.List;

import corp.kairos.adamastor.AllApps.AppDetail;

/**
 * Created by kiko on 23-12-2017.
 */

public class UserContext {
    private String contextName;
    private List<AppDetail> contextApps;

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
