package com.cse403chorecenter.chorecenterapp;

import android.view.View;
import android.widget.ImageView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.instanceOf;

@RunWith(AndroidJUnit4ClassRunner.class)
public class CreateRewardTest {

    @Before
    public void fillTestAccountInfo() {
        UserLogin.ACCOUNT_ID = "frontend_test";
        UserLogin.ACCOUNT_DISPLAY_NAME = UserLogin.ACCOUNT_ID;
        UserLogin.ACCOUNT_ID_TOKEN = UserLogin.ACCOUNT_ID;
    }

    @Test
    public void testCreateReward() {
        try (ActivityScenario<ParentNavigation> ignored = ActivityScenario.launch(ParentNavigation.class)) {
            onView(withId(R.id.button_home_create_reward)).perform(click());

            // click without input
            waitViewShown(withId(R.id.button_create_reward));
            onView(withId(R.id.button_create_reward)).perform(click());
            onView(withId(R.id.text_create_reward)).check(matches(withText("please input both fields")));

            // non-integer points input
            onView(withId(R.id.editCreateReward)).perform(typeText("test")).perform(closeSoftKeyboard());
            onView(withId(R.id.editCreateRewardPoints)).perform(typeText("1000.12")).perform(closeSoftKeyboard());
            onView(withId(R.id.button_create_reward)).perform(click());
            onView(withId(R.id.text_create_reward)).check(matches(withText("please input a number for the reward points")));

            // valid input
            onView(withId(R.id.editCreateReward)).perform(clearText(), typeText("test")).perform(closeSoftKeyboard());
            onView(withId(R.id.editCreateRewardPoints)).perform(clearText(), typeText("1000")).perform(closeSoftKeyboard());
            onView(withId(R.id.button_create_reward)).perform(click());
            onView(withId(R.id.text_create_reward)).check(matches(withText("CREATED")));
        }

        // Important: Due to current unavailability of test server, we need to manually delete
        // rewards created for testing. This also passively tests the delete reward functionality.
        try (ActivityScenario<ParentNavigation> ignored = ActivityScenario.launch(ParentNavigation.class)) {
            onView(withId(R.id.button_home_all_rewards)).perform(click());

            onView(withId(R.id.parentAllRewardHistoryRecyclerView))
                    .perform(actionOnItemAtPosition(0, TestViewAction.clickChildViewWithId(R.id.parentDeleteRewardBtn)));
            onView(withText("Delete")).inRoot(isDialog()).check(matches(isDisplayed())).perform(click());
            waitViewShown(withId(com.google.android.material.R.id.snackbar_text));
            onView(withId(com.google.android.material.R.id.snackbar_text)).check(matches(withText("Delete successful")));
        }
    }

    public void waitViewShown(Matcher<View> matcher) {
        IdlingResource idlingResource = new ViewShownIdlingResource(matcher);///
        try {
            IdlingRegistry.getInstance().register(idlingResource);
            onView(matcher).check(matches(isDisplayed()));
        } finally {
            IdlingRegistry.getInstance().unregister(idlingResource);
        }
    }
}
