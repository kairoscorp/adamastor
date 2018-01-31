import android.app.Activity;
import android.support.design.widget.FloatingActionButton;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.maps.MapView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowApplication;

import corp.kairos.adamastor.Onboarding.Onboard1WelcomeActivity;
import corp.kairos.adamastor.Onboarding.Onboard2SpecialPermissionActivity;
import corp.kairos.adamastor.Onboarding.Onboard3LocationActivity;
import corp.kairos.adamastor.Onboarding.Onboard4ContextAppsActivity;
import corp.kairos.adamastor.Onboarding.Onboard5ScheduleActivity;
import corp.kairos.adamastor.R;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
public class OnBoardingTest {

    private Activity onBoarding;

    @Test
    public void onBoardingWelcomeActivityTest() {
        this.onBoarding = Robolectric.setupActivity(Onboard1WelcomeActivity.class);
        Button startButton = this.onBoarding.findViewById(R.id.start);
        startButton.performClick();

        ShadowApplication application = shadowOf(RuntimeEnvironment.application);
        assertThat("Next activity has started", application.getNextStartedActivity(), is(notNullValue()));
    }

    /**
     * Will fail
     * appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
     android.os.Process.myUid(), this.getPackageName());
     * throws nullpointerexception
     */
    @Test
    public void onBoardingSpecialPermissionActivityTest() {
        this.onBoarding = Robolectric.setupActivity(Onboard2SpecialPermissionActivity.class);
        Button okButton = this.onBoarding.findViewById(R.id.okButtonOnBoard2);
        okButton.performClick();

        ShadowApplication application = shadowOf(RuntimeEnvironment.application);
        assertThat("Next activity has started", application.getNextStartedActivity(), is(notNullValue()));
    }

    /**
     * Test will pass
     * but offcourse will give an exception
     * cause GooglePlayServices are not available while
     * unit testing
     */
    @Test
    public void onBoardingLocationActivityTest() {
        this.onBoarding = Robolectric.setupActivity(Onboard3LocationActivity.class);
        MapView mapHome = this.onBoarding.findViewById(R.id.mapHome);
        MapView mapWork = this.onBoarding.findViewById(R.id.mapWork);
        Button btn_pickHome = this.onBoarding.findViewById(R.id.btn_pickHome);
        Button btn_pickWork = this.onBoarding.findViewById(R.id.btn_pickWork);
        Button next2 = this.onBoarding.findViewById(R.id.next2);

        mapHome.performClick();
        mapWork.performClick();
        btn_pickHome.performClick();
        btn_pickWork.performClick();
        next2.performClick();

        ShadowApplication application = shadowOf(RuntimeEnvironment.application);
        assertThat("Next activity has started", application.getNextStartedActivity(), is(notNullValue()));
    }

    @Test
    public void onBoardingContextAppsActivity() {
        this.onBoarding = Robolectric.setupActivity(Onboard4ContextAppsActivity.class);
        ListView check_apps_list = this.onBoarding.findViewById(R.id.check_apps_list);
        FloatingActionButton id_add_app_contexts_next = this.onBoarding.findViewById(R.id.id_add_app_contexts_next);

        check_apps_list.performClick();
        check_apps_list.performClick();
        check_apps_list.performClick();
        id_add_app_contexts_next.performClick();

        ShadowApplication application = shadowOf(RuntimeEnvironment.application);
        assertThat("Next activity has not started", application.getNextStartedActivity(), is(nullValue()));
    }

    @Test
    public void onBoardingSheduleActivity() {
        this.onBoarding = Robolectric.setupActivity(Onboard5ScheduleActivity.class);
        Button goNextButton = this.onBoarding.findViewById(R.id.next3);
        goNextButton.performClick();
        Button finishButton = this.onBoarding.findViewById(R.id.finish);
        finishButton.performClick();

        ShadowApplication application = shadowOf(RuntimeEnvironment.application);
        assertThat("Next activity has started", application.getNextStartedActivity(), is(notNullValue()));
    }
}
