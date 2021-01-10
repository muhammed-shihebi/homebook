package com.mabem.homebook.Model.Objects;

import com.mabem.homebook.Model.Reminder;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;

public class ReminderTest {

    private String id = "";
    private String name = "";
    private Date date;
    private String frequency;

    private Instant inst;
    Reminder r, r2;

    @Before
    public void setUp() throws Exception {
        id = "testid";
        name = "testname";
        inst = Instant.now();
        frequency = Reminder.DAILY;
        date = Date.from(inst);


        r = new Reminder(id, name, frequency, date);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void reminderWithSameIdEqualToGivenReminder_returnsTrue() {
        inst = Instant.EPOCH;
        r2 = new Reminder(id, name, frequency, date);

        assertThat(r.equals(r2)).isTrue();
    }
    @Test
    public void reminderDifferentAsGivenObjectOfDifferentClassWithSimilarAttributes_returnsFalse() {

        class NotReminder {

            private String id = "testid";
            private String name = "testname";
            public Date date;
            private String frequency = "x";
        }


        NotReminder nr = new NotReminder();
        nr.date = Date.from(inst);


        assertThat(r.equals(nr)).isFalse();

    }


}
