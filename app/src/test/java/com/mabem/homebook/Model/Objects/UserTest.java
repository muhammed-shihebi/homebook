package com.mabem.homebook.Model.Objects;

import android.net.Uri;

import com.mabem.homebook.Model.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;

public class UserTest {
    private String id = "";
    private String name = "";
    private String emailAddress = "";
    private String password = "";
    private Uri imageURI = null;
    User u, u2;

    @Before
    public void setUp() throws Exception {

        id = "testid";
        name = "testname";
        emailAddress = "testemail";
        password = "testpassword";


        u = new User(id, name, emailAddress, password, imageURI);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void userWithSameIdEqualToGivenUser_returnsTrue() {

        u2 = new User(id, "not" + name, "not" + emailAddress, "not" + password, imageURI);

        assertThat(u.equals(u2)).isTrue();
    }
    @Test
    public void userDifferentAsGivenObjectOfDifferentClassWithSimilarAttributes_returnsFalse() {

        class NotUser {

            private String id = "testid";
            private String name = "testname";
            private String emailAddress = "testemail";
            private String password = "testpassword";
        }


        NotUser nu = new NotUser();


        assertThat(u.equals(nu)).isFalse();

    }

    @Test
    public void idEqualToGivenId_returnsTrue() {
        assertThat(u.getId()).isEqualTo(id);
    }

}
