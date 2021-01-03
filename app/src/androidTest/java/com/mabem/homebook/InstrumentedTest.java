package com.mabem.homebook;

import android.app.Application;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.Observer;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.mabem.homebook.Model.User;
import com.mabem.homebook.ViewModels.AuthViewModel;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class InstrumentedTest {

    /**
     * This is an instrumented and an integration test.
     * It will try to login a user using the email and the password of this user.
     * The user already exist in the database.
     * To perform the database operations the functions available in the AuthViewModel are used.
     * This function will assert than that the returned user has the expected attributes.
     */

    @Test
    public void logInTestCase() {

        // Get an instance of the application to initialize the authViewModel with it
        Application application = (Application) ApplicationProvider.getApplicationContext();

        // Initialize the authViewModel
        AuthViewModel authViewModel = new AuthViewModel(application);

        // Create the expected user
        User expectedUser = new User(
                "MOYbEMGk6Da90swlVxEwOe7k3LA2",
                "Nur",
                "homebooktester@gmail.com",
                Uri.parse("https://firebasestorage.googleapis.com/v0/b/homebook-420a0.appspot.com/o/" +
                        "profile_images%2FMOYbEMGk6Da90swlVxEwOe7k3LA2?" +
                        "alt=media&token=6dc16c89-d069-4a31-b739-77a580a04c30")
        );

        final User[] returnedUser = {null};
        final boolean[] returnedUserValueChanged = {false};

        // Use the loginWithEmail function of the authViewModel to login a user
        authViewModel.loginWithEmail("homebooktester@gmail.com", "123123123");

        // Initialize the observer
        Observer<User> currentUserObserver = new Observer<User>() {
            @Override
            public void onChanged(User user) {
                returnedUserValueChanged[0] = true;

                // @user could be null if something was wrong when getting the user from the database
                returnedUser[0] = user;

                // Remove the observer
                authViewModel.getCurrentUser().removeObserver(this);
            }
        };

        // Necessary to make this observer code work in the UI thread
        new Handler(Looper.getMainLooper()).post(() -> {

            // Observe the user to be returned
            authViewModel.getCurrentUser().observeForever(currentUserObserver);
        });

        // Necessary to wait until the observed user change
        while (!returnedUserValueChanged[0]) {
        }

        // Asserts to make sure the expected user is the same as the returned user
        assertEquals(expectedUser.getId(), returnedUser[0].getId());
        assertEquals(expectedUser.getEmailAddress(), returnedUser[0].getEmailAddress());
        assertEquals(expectedUser.getName(), returnedUser[0].getName());
        assertEquals(expectedUser.getImageURI(), returnedUser[0].getImageURI());
    }
}