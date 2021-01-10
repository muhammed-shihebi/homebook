package com.mabem.homebook.Model.Objects;

import com.mabem.homebook.Model.AdminNotification;
import com.mabem.homebook.Model.Notification;
import com.mabem.homebook.Model.UserNotification;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static com.google.common.truth.Truth.assertThat;

public class NotificationTest {
    Notification n;

    private ArrayList<AdminNotification> adminNotifications = new ArrayList<>();
    private ArrayList<UserNotification> userNotifications = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        n = new Notification(adminNotifications, userNotifications);
    }

    @After
    public void tearDown() throws Exception {
    }


    @Test
    public void adminNotificationsEqualToGivenAdminNotifications_returnsTrue() {
        assertThat(n.getAdminNotifications()).isEqualTo(adminNotifications);;
    }



}
