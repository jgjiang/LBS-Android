package com.ucc.tony.myhangout;

import android.media.Image;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;


import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


/**
 * Created by tony on 27/03/2017.
 */


@RunWith(AndroidJUnit4.class)
@LargeTest

public class MainActivityTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);


    @Test
    public void onCreate() throws Exception {
        onView(withId(R.id.textView)).check(matches(withText("Western Gateway Building, Mardyke, Cork, Ireland")));

    }


    @Test
    public void onSensorChanged() throws Exception {
        onView(withId(R.id.shakeimageView1)).perform(click()).check(matches(isDisplayed()));
        onView(withId(R.id.shakeimageView2)).perform(click()).check(matches(isDisplayed()));

    }




}