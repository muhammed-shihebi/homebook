package com.mabem.homebook.ViewModels;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.Observer;
import androidx.test.core.app.ApplicationProvider;

import com.mabem.homebook.Model.Home;
import com.mabem.homebook.Model.Item;
import com.mabem.homebook.Model.Member;
import com.mabem.homebook.Model.Receipt;
import com.mabem.homebook.Model.User;
import com.mabem.homebook.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class HomeViewModelTest {

    // Get an instance of the application to initialize the ViewModels with it
    Application application = (Application) ApplicationProvider.getApplicationContext();

    // Initialize the ViewModels
    // authViewModel is needed to login a user
    AuthViewModel authViewModel = new AuthViewModel(application);

    // homeViewModel is needed to get the current member
    HomeViewModel homeViewModel = new HomeViewModel(application);

    final Member[] currentMember = new Member[1];
    final Home[] currentHome = new Home[1];
    // returnedReceipt will hold the receipt returned from the database
    // It will be used to verify that returned receipt is the same as the expected one
    final Receipt[] returnedReceipt = new Receipt[1];

    @Before
    public void setUp() {
        // Initialize Objects
        application = (Application) ApplicationProvider.getApplicationContext();
        authViewModel = new AuthViewModel(application);
        homeViewModel = new HomeViewModel(application);

        //========================================= Login a user

        String userEmail = "homebooktester@gmail.com";
        String userPassword = "123123123";

        authViewModel.loginWithEmail(userEmail, userPassword);

        new Handler(Looper.getMainLooper()).post(() -> {
            authViewModel.getCurrentUser().observeForever(new Observer<User>() {
                @Override
                public void onChanged(User user) {

                    // Call to update the current Member
                    homeViewModel.updateCurrentMember();
                    authViewModel.getCurrentUser().removeObserver(this);
                }
            });
        });

        //========================================= Get the currentMember
        new Handler(Looper.getMainLooper()).post(() -> {
            homeViewModel.getCurrentMember().observeForever(new Observer<Member>() {
                @Override
                public void onChanged(Member member) {
                    currentMember[0] = member;

                    // Update currentHome using the Id of an existing home
                    homeViewModel.updateCurrentHome("25ziXg7T5Lt7JCvDieb5");
                    homeViewModel.getCurrentMember().removeObserver(this);
                }
            });
        });
    }

    /**
     * This is an instrumented and an integration test.
     * It will try to add a new Receipt to an existing home and then delete it
     * To perform the database operations, the functions available in the MainActivityViewModel are used.
     * This function will assert that the receipt is added correctly
     */

    @Test
    public void addReceiptTestCase() throws InterruptedException {

        final boolean[] statusChanged = {false};
        returnedReceipt[0] = new Receipt();
        final Receipt[] expectedReceipt = new Receipt[1];
        expectedReceipt[0] = new Receipt(
                "SLs9ug95mJkOWdP75et5",
                "Test Receipt",
                new Date(),
                0.0,
                "Nur",
                "MOYbEMGk6Da90swlVxEwOe7k3LA2"
        );

        //========================================= Get the currentHome
        new Handler(Looper.getMainLooper()).post(() -> {
            homeViewModel.getCurrentHome().observeForever(new Observer<Home>() {
                @Override
                public void onChanged(Home home) {
                    currentHome[0] = home;

                    // Add the Receipt to the database
                    homeViewModel.addReceipt(expectedReceipt[0]);
                    homeViewModel.getCurrentHome().removeObserver(this);
                }
            });
        });

        //========================================= Observe the result message to update the currentHome
        new Handler(Looper.getMainLooper()).post(() -> {
            homeViewModel.getResultMessage().observeForever(new Observer<String>() {
                @Override
                public void onChanged(String s) {
                    if (s != null && s.equals(application.getString(R.string.new_receipt_added_message))) {
                        // Update the home after adding a Receipt
                        homeViewModel.updateCurrentHome("25ziXg7T5Lt7JCvDieb5");
                        homeViewModel.getResultMessage().removeObserver(this);
                        homeViewModel.getCurrentHome().observeForever(new Observer<Home>() {
                            @Override
                            public void onChanged(Home home) {
                                if (currentHome[0] != home) {

                                    for (Receipt receipt : home.getReceipts()) {
                                        if (receipt.getId().equals(expectedReceipt[0].getId())) {
                                            returnedReceipt[0] = receipt;
                                            break;
                                        }
                                    }
                                    homeViewModel.getCurrentHome().removeObserver(this);
                                }
                            }
                        });
                    }
                }
            });
        });

        // To make sure all operations are done before checking the results
        Thread.sleep(5000);

        // Asserts to make sure the expected receipt is the same as the returned receipt
        assertEquals(expectedReceipt[0].getId(), returnedReceipt[0].getId());
        assertEquals(expectedReceipt[0].getDate(), returnedReceipt[0].getDate());
        assertEquals(expectedReceipt[0].getTotal(), returnedReceipt[0].getTotal());
        assertEquals(expectedReceipt[0].getMemberId(), returnedReceipt[0].getMemberId());
        assertEquals(expectedReceipt[0].getMemberName(), returnedReceipt[0].getMemberName());
        assertEquals(expectedReceipt[0].getName(), returnedReceipt[0].getName());
    }

    @After
    public void deleteReceipt() {
        new Handler(Looper.getMainLooper()).post(() -> {
            homeViewModel.deleteReceipt(returnedReceipt[0].getId());
        });
    }
}
