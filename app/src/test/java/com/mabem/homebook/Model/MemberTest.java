package com.mabem.homebook.Model;

import android.net.Uri;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static com.google.common.truth.Truth.assertThat;

public class MemberTest {
    /**
     * Testing functions of Item class except the getters and setters. Function names are self explaining.
     */

    Home h1;

    Member mem1, mem2;
    String id1 = "11";
    String name1 = "member1";
    String mail1 = "test@gmail.com";
    String pass1 = "123123123";
    Uri uri1 = null;
    HashMap<Home, Boolean> home_role1;


    @Before
    public void setUp() throws Exception {

        h1 = new Home("11", "example1", "22", true, null);
        home_role1 = new HashMap<Home, Boolean>();
        home_role1.put(h1, false); // false : non-admin member

        mem1 = new Member(id1, name1, mail1, pass1, uri1, home_role1);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void memberWithSameIdAndHomeRoleEqualToGivenMember_returnsTrue() {
        String name2 = "member2";
        String mail2 = "two@gmail.com";
        String pass2 = "999123123";
        mem2 = new Member(id1, name2, mail2, pass2, null, home_role1);
        assertThat(mem1.equals(mem2)).isTrue();
    }

    @Test
    public void memberIsNotAdminInAnyHomes_returnsFalse() {
        for (Home h : mem1.getHome_role().keySet()) {
            if (h.getId().equals(h1.getId()) && mem1.getHome_role().get(h)) {

                assertThat(mem1.getHome_role().get(h)).isFalse();
            }
        }
    }


}