package corp.kairos.adamastor.userContext.dao;

import java.util.Set;

import corp.kairos.adamastor.AppDetails;
import corp.kairos.adamastor.userContext.UserContext;

public interface UserContextDAO {

    /**
     * Retrieves a user context from the persistence layer
     *
     * @param contextName The name of the context to be delete
     * @param allApps     A set of all installed applications

     * @return An instance of UserContext
     * @see UserContext
     */
    UserContext loadUserContext(String contextName, Set<AppDetails> allApps);

    /**
     * Stores a UserContext in the persistence layer
     *
     * @param userContext The instance to be stored
     * @see UserContext
     */
    void storeUserContext(UserContext userContext);

    /**
     * Deletes the records of the context named by the {@param contextName} param
     *
     * @param contextName The name of the context to be deleted
     */
    void deleteUserContext(String contextName);

    /**
     * Deletes the records of all contexts passed in the {@param contextNames} param
     *
     * @param contextNames  A set containing the names of the contexts to be deleted
     */
    void deleteAllUserContexts(Set<String> contextNames);

}
