package com.mabem.homebook.Model.Objects;

import com.mabem.homebook.Model.UserNotification;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class UserNotificationTest {
    private String homeName;
    private String type;
    private String homeId;

    UserNotification un;

    @Before
    public void setUp() throws Exception {
        type = UserNotification.TYPE_ACCEPT;
        homeName = "testhomename";
        homeId = "testhomeid";
        un = new UserNotification(homeName,type,homeId);
    }

    @After
    public void tearDown() throws Exception {
    }


    @Test
    public void typeEqualToGivenType_returnsTrue() {
        assertThat(un.getType()).isEqualTo(UserNotification.TYPE_ACCEPT);
    }


}
