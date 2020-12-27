package com.mabem.homebook.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mabem.homebook.Database.Database;
import com.mabem.homebook.Model.Home;
import com.mabem.homebook.Model.Member;
import com.mabem.homebook.Model.User;

import java.util.ArrayList;

public class MainActivityViewModel extends AndroidViewModel {
    private final Database database;
    private final MutableLiveData<ArrayList<Home>> searchResults;

    private boolean showResultDialog = false;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        database = Database.getInstance(application);
        searchResults = database.getSearchResult();
    }

    public LiveData<ArrayList<Home>> getSearchResult() { return searchResults; }

    public void signOut(){
        database.signOut();
    }

    public void updateCurrentUser(){
        database.updateCurrentUser();
    }

    public void searchHome(String homeCode){
        database.searchHome(homeCode);
    }

    public void sendJoinRequest(String homeId) {
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