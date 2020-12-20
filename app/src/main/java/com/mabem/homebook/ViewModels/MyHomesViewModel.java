package com.mabem.homebook.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.mabem.homebook.Database.Database;
import com.mabem.homebook.Model.Home;
import com.mabem.homebook.Model.Member;
import com.mabem.homebook.Model.User;

public class MyHomesViewModel extends AndroidViewModel {


    private final Database database;
    private final MutableLiveData<Member> currentMember;
    private final MutableLiveData<String> resultMessage;

    public MyHomesViewModel(@NonNull Application application) {
        super(application);
        database = Database.getInstance(application);
        currentMember = database.getCurrentMember();
        resultMessage = database.getResultMessage();
    }

    public MutableLiveData<Member> getCurrentMember() {
        return currentMember;
    }
    public MutableLiveData<String> getResultMessage() {
        return resultMessage;
    }

    public void updateCurrentMember(){
        database.updateCurrentMember();
    }

    public void addNewHome(String homeName, boolean isPrivate){
        database.createHome(homeName, isPrivate);
    }

}
