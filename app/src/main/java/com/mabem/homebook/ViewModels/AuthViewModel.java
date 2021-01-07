package com.mabem.homebook.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mabem.homebook.Model.Database.Database;
import com.mabem.homebook.Model.Objects.User;

/**
 * A ViewModel to link the fragments Login, SignUp, Splash to the database
 * and maintain the status of the data in these fragments
 */

public class AuthViewModel extends AndroidViewModel {
    private final Database database;
    private final MutableLiveData<User> currentUser;
    private final MutableLiveData<String> resultMessage;

    public AuthViewModel(@NonNull Application application) {
        super(application);
        database = Database.getInstance(application);
        currentUser = database.getCurrentUser();
        resultMessage = database.getResultMessage();
    }

    //========================================= Getters

    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    public LiveData<String> getResultMessage() {
        return resultMessage;
    }

    //========================================= Methods

    public void loginWithEmail(String email, String password) {
        resultMessage.postValue(null);
        database.loginWithEmail(email, password);
    }

    public void signUpWithEmail(String email, String password, String name) {
        resultMessage.setValue(null);
        database.signUpWithEmail(email, password, name);
    }

    public void loginWithGoogle(String idToken) {
        resultMessage.setValue(null);
        database.loginWithGoogle(idToken);
    }

    public void forgotPassword(String email) {
        resultMessage.setValue(null);
        database.forgotPassword(email);
    }

    public void changePassword() {
        resultMessage.setValue(null);
        String email = currentUser.getValue().getEmailAddress();
        database.forgotPassword(email);
    }
}