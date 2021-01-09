package com.mabem.homebook.Model.Objects;

import com.mabem.homebook.Model.Objects.Home;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

/**
 * Compare two equal 'Home' s
 * Compare two notes with all values other than 'code' and 'id' are different
 * Compare home with another object
 * .... Should I test all getter and setters? Debatable, but no.
 */

public class HomeTest {
    Home h, h2;


    @Before // each test
    public void setUp() throws Exception {
        // If you were to initialize the variable globally instead, the test cases wouldn't have been completely INDEPENDENT from one another!
        h = new Home("11","example1", "22", true, null);


    }

    @After // each test
    public void tearDown() throws Exception {

    }

    @Test
    public void homeWithSameIdAndCodeEqualToGivenHome_returnsTrue() {
        h2 = new Home("11", "different", "22", false, null);
        assertThat(h.equals(h2)).isTrue();
    }

    @Test
    public void homeWithDifferentIdDifferentAsGivenHome_returnsFalse() {
        h2 = new Home("12", "different", "22", false, null);
        assertThat(h.equals(h2)).isFalse();
    }

    @Test
    public void homeDifferentAsGivenObjectOfDifferentClassWithSimilarAttributes_returnsFalse() {

        class NotHome {
            String id = "11";
            String name = "example1";
            String code = "22";
        }
        NotHome nh = new NotHome();
        assertThat(h.equals(nh)).isFalse();
    }

    @Test
    public void IdEqualToGivenId_returnsTrue() {
        assertThat(h.getId()).isEqualTo("11");
    }






}
