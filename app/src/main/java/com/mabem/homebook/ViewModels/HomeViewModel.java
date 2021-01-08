package com.mabem.homebook.ViewModels;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mabem.homebook.Model.Database.Database;
import com.mabem.homebook.Model.Objects.Home;
import com.mabem.homebook.Model.Objects.Member;
import com.mabem.homebook.Model.Objects.Receipt;
import com.mabem.homebook.Model.Objects.Reminder;

/**
 * A ViewModel to link fragments after logging in to the database
 * and maintain the status of the data in these fragments
 */

public class HomeViewModel extends AndroidViewModel {

    private final Database database;
    private final MutableLiveData<Member> currentMember;
    private final MutableLiveData<Home> currentHome;
    private final MutableLiveData<Receipt> currentReceipt;
    private final MutableLiveData<String> resultMessage;

    private boolean shouldShowResultMessage = false;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        database = Database.getInstance(application);
        currentMember = database.getCurrentMember();
        currentReceipt = database.getCurrentReceipt();
        resultMessage = database.getResultMessage();
        currentHome = database.getCurrentHome();
    }


    public LiveData<Member> getCurrentMember() {
        return currentMember;
    }

    public LiveData<Home> getCurrentHome() {
        return currentHome;
    }

    public LiveData<Receipt> getCurrentReceipt() {
        return currentReceipt;
    }

    public LiveData<String> getResultMessage() {
        return resultMessage;
    }

    public void updateUser(Member m, Uri localUri) {
        resultMessage.setValue(null);
        database.updateMember(m, localUri);
    }

    public void updateCurrentMember() {
        resultMessage.setValue(null);
        database.updateCurrentMember();
    }


    public void addNewHome(String homeName, boolean isPrivate) {
        resultMessage.setValue(null);
        database.createHome(homeName, isPrivate);
    }

    public void updateCurrentHome(String id) {
        database.updateCurrentHome(id);
    }

    public void updateHomeWithMembers() {
        resultMessage.setValue(null);
        database.updateHomeWithMembers();
    }

    public void updateHomeWithReminders() {
        resultMessage.setValue(null);
        database.updateHomeWithReminders();
    }

    public void leaveHome() {
        resultMessage.setValue(null);
        database.leaveHome();
    }

    public void updateHome(Home h) {
        resultMessage.setValue(null);
        database.updateHome(h);
    }

    public void deleteHome() {
        resultMessage.setValue(null);
        database.deleteHome();
    }

    public void updateCurrentReceipt(String id) {
        resultMessage.setValue(null);
        database.updateCurrentReceipt(id);
    }

    public void addReceipt(Receipt r) {
        resultMessage.setValue(null);
        database.addReceipt(r);
    }

    public void updateReceipt(Receipt r) {
        resultMessage.setValue(null);
        database.updateReceipt(r);
    }

    public void deleteReceipt(String id) {
        resultMessage.setValue(null);
        database.deleteReceipt(id);
    }

    public void updateReminder(Reminder r) {
        resultMessage.setValue(null);
        database.updateReminder(r);
    }

    public void setReminder(Reminder r) {
        resultMessage.setValue(null);
        database.setReminder(r);
    }

    public void deleteReminder(String id) {
        resultMessage.setValue(null);
        database.deleteReminder(id);
    }

    public boolean getShouldShowResultMessage() {
        return shouldShowResultMessage;
    }

    public void setShouldShowResultMessage(boolean shouldShowResultMessage) {
        this.shouldShowResultMessage = shouldShowResultMessage;
    }


}
