package com.mabem.homebook.ViewModels;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.Observer;
import androidx.test.core.app.ApplicationProvider;

import com.mabem.homebook.Model.Home;
import com.mabem.homebook.Model.Member;
import com.mabem.homebook.Model.Receipt;
import com.mabem.homebook.Model.User;
import com.mabem.homebook.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class HomeViewModelTest {

    // Initialize Objects

    // Get an instance of the application to initialize the ViewModels with it
    Application application = (Application) ApplicationProvider.getApplicationContext();

    // Initialize the ViewModels
    // authViewModel is needed to login a user
    AuthViewModel authViewModel = new AuthViewModel(application);

    // homeViewModel is needed to get the current member
    HomeViewModel homeViewModel = new HomeViewModel(application);

    Member currentMember;
    Home currentHome;
    // returnedReceipt will hold the receipt returned from the database
    // It will be used to verify that returned receipt is the same as the expected one
    Receipt returnedReceipt = new Receipt();

    @Before
    public void setUp() {
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
                    currentMember = member;

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
    public void addNewReceiptTestCase() throws InterruptedException {

        Receipt expectedReceipt = new Receipt(
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
                    currentHome = home;

                    // Add the Receipt to the database
                    homeViewModel.addReceipt(expectedReceipt);
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
                                if (currentHome != home) {
                                    for (Receipt receipt : home.getReceipts()) {
                                        if (receipt.getId().equals(expectedReceipt.getId())) {
                                            returnedReceipt = receipt;
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
        assertEquals(expectedReceipt.getId(), returnedReceipt.getId());
        assertEquals(expectedReceipt.getDate(), returnedReceipt.getDate());
        assertEquals(expectedReceipt.getTotal(), returnedReceipt.getTotal());
        assertEquals(expectedReceipt.getMemberId(), returnedReceipt.getMemberId());
        assertEquals(expectedReceipt.getMemberName(), returnedReceipt.getMemberName());
        assertEquals(expectedReceipt.getName(), returnedReceipt.getName());
    }

    @After
    public void deleteReceipt() {
        new Handler(Looper.getMainLooper()).post(() -> {
            homeViewModel.deleteReceipt(returnedReceipt.getId());
        });
    }
}
