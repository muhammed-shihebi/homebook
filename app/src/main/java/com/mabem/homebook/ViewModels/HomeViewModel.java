package com.mabem.homebook.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.mabem.homebook.Database.Database;
import com.mabem.homebook.Model.Home;
import com.mabem.homebook.Model.Member;
import com.mabem.homebook.Model.Receipt;

public class HomeViewModel extends AndroidViewModel {

    private final Database database;
    private final MutableLiveData<Member> currentMember;
    private final MutableLiveData<Home> currentHome;
    private final MutableLiveData<Receipt> currentReceipt;
    private final MutableLiveData<String> resultMessage;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        database = Database.getInstance(application);
        currentMember = database.getCurrentMember();
        currentReceipt = database.getCurrentReceipt();
        resultMessage = database.getResultMessage();
        currentHome = database.getCurrentHome();
    }


    public MutableLiveData<Member> getCurrentMember() {
        return currentMember;
    }
    public MutableLiveData<Home> getCurrentHome() {
        return currentHome;
    }
    public MutableLiveData<Receipt> getCurrentReceipt() {
        return currentReceipt;
    }
    public MutableLiveData<String> getResultMessage() {
        return resultMessage;
    }


    public void updateCurrentMember(){
        database.updateCurrentMember();
    }
    public void updateCurrentHome(String id){
        database.updateCurrentHome(id);
    }

    public void updateCurrentReceipt(String id){
        database.updateCurrentReceipt(id);
    }

}
