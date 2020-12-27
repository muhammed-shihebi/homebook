package com.mabem.homebook.Model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

/**
 * Compare two equal 'Home' s
 * Compare two notes with all values other than 'code' and 'id' are different

 */

public class HomeTest {
    Home h;

    @Before // each test
    public void setUp() throws Exception {
        // If you were to initialize the variable globally instead, the test cases wouldn't have been completely INDEPENDENT from one another!
        h = new Home("11","example1", "22", true, null);
    }

    @After // each test
    public void tearDown() throws Exception {
    }

    @Test
    public void setMember_role() {

    }

    @Test
    public void setReminders() {
    }

    @Test
    public void getId() {
        assertThat(h.getId()).isEqualTo("11");
    }

    @Test
    public void getName() {

    }

    @Test
    public void getCode() {
    }

    @Test
    public void isVisibility() {
    }

    @Test
    public void getMember_role() {
    }

    @Test
    public void getReminders() {
    }

    @Test
    public void getReceipts() {
    }

    @Test
    public void testEquals() {
    }



}
