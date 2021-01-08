package com.mabem.homebook.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mabem.homebook.Model.Database.Database;
import com.mabem.homebook.Model.Objects.Home;

import java.util.ArrayList;

/**
 * A ViewModel to link the MainActivity to the database
 * and maintain the status of the data in the MainActivity
 */

public class MainActivityViewModel extends AndroidViewModel {
    private final MutableLiveData<ArrayList<Home>> searchResults;
    private final MutableLiveData<String> resultMessage;
    private Database database;
    private boolean showResultDialog = false;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        database = Database.getInstance(application);
        searchResults = database.getSearchResult();
        resultMessage = database.getResultMessage();
    }

    public LiveData<ArrayList<Home>> getSearchResult() {
        return searchResults;
    }

    public LiveData<String> getResultMessage() {
        return resultMessage;
    }

    public void signOut() {
        database.signOut();
    }

    public void updateCurrentUser() {
        database.updateCurrentUser();
    }

    public void searchHome(String homeCode) {
        database.searchHome(homeCode);
    }

    public void sendJoinRequest(String homeId) {
        resultMessage.setValue(null);
        database.sendJoinRequest(homeId);
    }


    public boolean isShowResultDialog() {
        return showResultDialog;
    }

    public void setShowResultDialog(boolean showResultDialog) {
        this.showResultDialog = showResultDialog;
    }

    public void clearSearchResults() {
        database.clearSearchResults();
    }
}