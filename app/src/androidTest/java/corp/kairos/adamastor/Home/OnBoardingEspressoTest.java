package corp.kairos.adamastor.Home;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import corp.kairos.adamastor.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

/**
 * Can only be tested one time
 * When adamastor is installed
 * Give the permission to the app and run this test
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class OnBoardingEspressoTest {

    @Rule
    public ActivityTestRule<HomeActivity> mActivityTestRule = new ActivityTestRule<>(HomeActivity.class);

    @Test
    public void onBoardingEspressoTest() {
        onView(
                allOf(withId(R.id.start), withText("Start"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        3),
                                0),
                        isDisplayed())).perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        onView(
                allOf(withId(R.id.mapWork))
        ).perform(click());
        onView(
                allOf(withId(R.id.mapHome))
        ).perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(
                allOf(withId(R.id.btn_pickHome), withText("Pick"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        2),
                                1),
                        isDisplayed())).perform(click());

        onView(
                allOf(withId(R.id.btn_pickWork), withText("Pick"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        5),
                                1),
                        isDisplayed())).perform(click());

        onView(
                allOf(withId(R.id.next2), withText("Next"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed())).perform(click());

        for(int i = 0; i < 4; i++) {
            onView(
                    allOf(withId(R.id.app_check_box), withText("Calculator"),
                            childAtPosition(
                                    childAtPosition(
                                            withClassName(is("android.support.constraint.ConstraintLayout")),
                                            0),
                                    0),
                            isDisplayed())).perform(click());

            onView(
                    allOf(withId(R.id.app_check_box), withText("Calendar"),
                            childAtPosition(
                                    childAtPosition(
                                            withClassName(is("android.support.constraint.ConstraintLayout")),
                                            0),
                                    0),
                            isDisplayed())).perform(click());

            onView(
                    allOf(withId(R.id.app_check_box), withText("Camera"),
                            childAtPosition(
                                    childAtPosition(
                                            withClassName(is("android.support.constraint.ConstraintLayout")),
                                            0),
                                    0),
                            isDisplayed())).perform(click());

            onView(
                    allOf(withId(R.id.next3), withText("Next"),
                            childAtPosition(
                                    childAtPosition(
                                            withClassName(is("android.widget.LinearLayout")),
                                            1),
                                    0),
                            isDisplayed())).perform(click());
        }


        ViewInteraction appCompatButton9 = onView(
                allOf(withId(R.id.next3), withText("Next"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        3),
                                0),
                        isDisplayed()));
        appCompatButton9.perform(click());

        ViewInteraction appCompatButton10 = onView(
                allOf(withId(R.id.finish), withText("Finish"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        appCompatButton10.perform(click());

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
