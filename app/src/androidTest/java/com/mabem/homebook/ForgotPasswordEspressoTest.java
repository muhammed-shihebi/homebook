package com.mabem.homebook;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


@RunWith(AndroidJUnit4.class) // Because we want to use the junit4 testing framework
@LargeTest // Because we have many test cases inside (UnitTests are SmallTest, integrated test-> MediumTest, UITest -> LargeTest
@FixMethodOrder (MethodSorters.NAME_ASCENDING) // So that we run tests in ascending order (of their names)

public class ForgotPasswordEspressoTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = // Means that test case should start its execution with the main activity
            new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp() throws Exception {
        // what should happen before running each test case



        //onView(withText(containsString("A4"))).inRoot(isPlatformPopup()).check(matches(isDisplayed())); // GIBI BIR SEYLER YAPIP ANLAMAK GEREKIYOR ASLINDA ACIK MI SU AN
    }
    @After
    public void tearDown() throws Exception {
        // Do after each test case
        // Close the app etc
        //
    }


    @Test
    public void canSeeSettings() {
        onView(withText("Forgot Passsword")).check(matches(isDisplayed()));
    }




}


