package com.mabem.homebook.ViewModels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.mabem.homebook.Database.Database;
import com.mabem.homebook.Model.User;

public class AuthViewModel extends AndroidViewModel {
    private final Database database;
    private final MutableLiveData<User> currentUser;

    public AuthViewModel(@NonNull Application application) {
        super(application);
        database = new Database(application);
        currentUser = database.getCurrentUser();
    }

    //========================================= Getters

    public MutableLiveData<User> getCurrentUser(){
        return currentUser;
    }

    //========================================= Methods

    public void loginWithEmail(String email, String password) {
        database.loginWithEmail(email, password);
    }

    public void signUpWithEmail(String email, String password, String name){
        database.signUpWithEmail(email, password, name);
    }

    public void loginWithGoogle(String idToken) {
        database.loginWithGoogle(idToken);
    }

    public void forgotPassword(String email) {
        database.forgotPassword(email);
    }
}