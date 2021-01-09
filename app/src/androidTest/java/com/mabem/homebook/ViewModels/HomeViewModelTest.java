package com.mabem.homebook.ViewModels;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.Observer;
import androidx.test.core.app.ApplicationProvider;

import com.mabem.homebook.Model.Objects.Home;
import com.mabem.homebook.Model.Objects.Member;
import com.mabem.homebook.Model.Objects.Receipt;
import com.mabem.homebook.Model.Objects.User;
import com.mabem.homebook.R;

import org.junit.After;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class HomeViewModelTest {
    final Receipt[] returnedReceipt = new Receipt[1];
    /**
     * This is an instrumented and an integration test.
     * It will try to add a new Receipt to an existing home and then delete it
     * To perform the database operations, the functions available in the MainActivityViewModel are used.
     * This function will assert that the receipt is added correctly
     */


    // Get an instance of the application to initialize the ViewModels with it
    Application application = (Application) ApplicationProvider.getApplicationContext();
    // Initialize the ViewModels
    // authViewModel is needed to login a user
    // homeViewModel is needed to get the current member
    AuthViewModel authViewModel = new AuthViewModel(application);
    HomeViewModel homeViewModel = new HomeViewModel(application);

    @Test
    public void addReceiptTestCase() {
        //========================================= Login a user
        authViewModel.loginWithEmail("homebooktester@gmail.com", "123123123");

        new Handler(Looper.getMainLooper()).post(() -> {
            authViewModel.getCurrentUser().observeForever(new Observer<User>() {
                @Override
                public void onChanged(User user) {
                    new Handler(Looper.getMainLooper()).post(() -> {
//                        Log.i("unique", "addReceipt: 1");
                        // Call to update the current Member
                        homeViewModel.updateCurrentMember();
                        authViewModel.getCurrentUser().removeObserver(this);
                    });
                }
            });
        });

        //========================================= Get the currentMember

        final Member[] currentMember = new Member[1];

        new Handler(Looper.getMainLooper()).post(() -> {
            homeViewModel.getCurrentMember().observeForever(new Observer<Member>() {
                @Override
                public void onChanged(Member member) {
                    currentMember[0] = member;
//                    Log.i("unique", "addReceipt: 2");
                    // Update currentHome using the Id of an existing home
                    homeViewModel.updateCurrentHome("25ziXg7T5Lt7JCvDieb5");
                    homeViewModel.getCurrentMember().removeObserver(this);
                }
            });
        });

        //========================================= Get the currentHome

        final Receipt[] expectedReceipt = new Receipt[1];
        final boolean[] statusChanged = {false};
        final Home[] currentHome = new Home[1];

        new Handler(Looper.getMainLooper()).post(() -> {
            homeViewModel.getCurrentHome().observeForever(new Observer<Home>() {
                @Override
                public void onChanged(Home home) {

                    currentHome[0] = home;

                    expectedReceipt[0] = new Receipt(
                            "",
                            "Test Receipt",
                            new Date(),
                            0.0,
                            currentMember[0].getName(),
                            currentMember[0].getId()
                    );

//                    Log.i("unique", "addReceipt: 3");

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
//                        Log.i("unique", "addReceipt: 4");
                        // Update the home after the
                        homeViewModel.updateCurrentHome("25ziXg7T5Lt7JCvDieb5");
                        homeViewModel.getResultMessage().removeObserver(this);
                        homeViewModel.getCurrentHome().observeForever(new Observer<Home>() {
                            @Override
                            public void onChanged(Home home) {
                                if (currentHome[0] != home) {
//                                    Log.i("unique", "addReceipt: 5");
                                    for (Receipt receipt : home.getReceipts()) {
//                                        Log.i("unique", "addReceipt: 5 " + receipt.getName());
                                        if (receipt.getName().equals(expectedReceipt[0].getName())) {
                                            returnedReceipt[0] = receipt;
                                            statusChanged[0] = true;
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

        while (!statusChanged[0]) {
        }

        // Asserts to make sure the expected receipt is the same as the returned receipt
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
