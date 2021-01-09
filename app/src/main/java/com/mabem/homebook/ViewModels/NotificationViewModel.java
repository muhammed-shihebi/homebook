package com.mabem.homebook.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mabem.homebook.Model.Database.Database;
import com.mabem.homebook.Model.AdminNotification;
import com.mabem.homebook.Model.Notification;
import com.mabem.homebook.Model.UserNotification;

/**
 * A ViewModel to link the NotificationFragment to the database
 * and maintain the status of the data in the fragment
 */

public class NotificationViewModel extends AndroidViewModel {

    private final Database database;
    private final MutableLiveData<Notification> currentNotification;


    public NotificationViewModel(@NonNull Application application) {
        super(application);
        this.database = Database.getInstance(application);
        this.currentNotification = database.getCurrentNotification();
    }

    public LiveData<Notification> getCurrentNotification() {
        return currentNotification;
    }


    public void acceptJoinRequest(AdminNotification adminNotification) {
        database.acceptJoinRequest(adminNotification);
    }

    public void declineJoinRequest(AdminNotification adminNotification) {
        database.declineJoinRequest(adminNotification);
    }

    public void deleteUserNotification(UserNotification userNotification) {
        database.deleteUserNotification(userNotification);
    }


    public void updateCurrentNotification() {
        database.updateCurrentNotification();
    }
}
