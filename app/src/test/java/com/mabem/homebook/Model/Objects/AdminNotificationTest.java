package com.mabem.homebook.Model.Objects;

import com.mabem.homebook.Model.AdminNotification;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

/**
 * Not much to test here...
 * Test the constructor, see if UserId is initialized correctly.
 *
 */

public class AdminNotificationTest {
    AdminNotification an;


    @Before // each test
    public void setUp() throws Exception {
        // If you were to initialize the variable globally instead, the test cases wouldn't have been completely INDEPENDENT from one another!
        an = new AdminNotification("11", "22", "home1", "user1");


    }

    @After // each test
    public void tearDown() throws Exception {

    }


    @Test
    public void UserIdEqualToGivenUserId_returnsTrue() {
        assertThat(an.getUserId()).isEqualTo("11");
    }






}
