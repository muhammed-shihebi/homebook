package com.mabem.homebook.Model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class UserNotificationTest {
    UserNotification un;
    private String homeName;
    private String type;
    private String homeId;

    @Before
    public void setUp() throws Exception {
        type = UserNotification.TYPE_ACCEPT;
        homeName = "testhomename";
        homeId = "testhomeid";
        un = new UserNotification(homeName, type, homeId);
    }

    @After
    public void tearDown() throws Exception {
    }


    @Test
    public void typeEqualToGivenType_returnsTrue() {
        assertThat(un.getType()).isEqualTo(UserNotification.TYPE_ACCEPT);
    }


}
