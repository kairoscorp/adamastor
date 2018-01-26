package corp.kairos.adamastor.Onboarding.dao;

public interface OnboardStateDAO {

    /**
     * Checks the persistence layer for a flag indicating the completion of the onboarding process
     *
     * @return True if Onboarding was already completed
     */
    boolean isOnboardingDone();

    /**
     * Sets the flag indicating the completion of the onboarding process to true
     */
    void setOnboardingDone();
}
