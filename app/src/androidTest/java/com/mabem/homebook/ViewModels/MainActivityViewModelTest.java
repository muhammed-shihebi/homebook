package com.mabem.homebook.ViewModels;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.Observer;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.mabem.homebook.Model.Home;
import com.mabem.homebook.Model.Member;
import com.mabem.homebook.Model.User;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class MainActivityViewModelTest {
    /**
     * This is an instrumented and an integration test.
     * It will try to search for a home using its code.
     * The home already exist in the database.
     * To perform the database operations the functions available in the MainActivityViewModel are used.
     * This function will assert than that the returned home has the expected attributes.
     */

    @Test
    public void searchHomeTestCase() {

        // Get an instance of the application to initialize the ViewModels with it
        Application application = (Application) ApplicationProvider.getApplicationContext();

        // Initialize the ViewModels
        // authViewModel is needed to login a user
        // homeViewModel is needed to get the current member
        // mainActivityViewModel is needed to perform the home search
        AuthViewModel authViewModel = new AuthViewModel(application);
        HomeViewModel homeViewModel = new HomeViewModel(application);
        MainActivityViewModel mainActivityViewModel = new MainActivityViewModel(application);

        //========================================= Login a user
        authViewModel.loginWithEmail("homebooktester@gmail.com", "123123123");

        new Handler(Looper.getMainLooper()).post(() -> {
            authViewModel.getCurrentUser().observeForever(new Observer<User>() {
                @Override
                public void onChanged(User user) {
                    new Handler(Looper.getMainLooper()).post(() -> {

                        // Call to update the current Member
                        homeViewModel.updateCurrentMember();
                        authViewModel.getCurrentUser().removeObserver(this);
                    });
                }
            });
        });

        //========================================= Get the current Member
        new Handler(Looper.getMainLooper()).post(() -> {
            homeViewModel.getCurrentMember().observeForever(new Observer<Member>() {
                @Override
                public void onChanged(Member member) {

                    // Call to search for a home using its code
                    mainActivityViewModel.searchHome("4d0028c6");
                    homeViewModel.getCurrentMember().removeObserver(this);
                }
            });
        });

        //========================================= Observe the search result
        // Create the expected home
        String homeName = "sasouki";
        String homeId = "b6GesqM1MrMwz7QPTGPC";
        Home expectedHome = new Home(homeId, homeName);

        final ArrayList<Home>[] returnedSearchResult = new ArrayList[1];
        final boolean[] returnedSearchResultChanged = {false};

        new Handler(Looper.getMainLooper()).post(() -> {
            mainActivityViewModel.getSearchResult().observeForever(new Observer<ArrayList<Home>>() {
                @Override
                public void onChanged(ArrayList<Home> homes) {
                    returnedSearchResult[0] = homes;
                    returnedSearchResultChanged[0] = true;
                    mainActivityViewModel.getSearchResult().removeObserver(this);
                }
            });
        });

        while (!returnedSearchResultChanged[0]) {
        }

        // Asserts to make sure the expected home is the same as the returned home
        assertEquals(expectedHome.getId(), returnedSearchResult[0].get(0).getId());
        assertEquals(expectedHome.getName(), returnedSearchResult[0].get(0).getName());
    }
}
