package com.mabem.homebook.Fragments.Main.Notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.mabem.homebook.Adapters.NotificationAdapter;
import com.mabem.homebook.Model.AdminNotification;
import com.mabem.homebook.Model.Item;
import com.mabem.homebook.Model.UserNotification;
import com.mabem.homebook.R;
import com.mabem.homebook.Utils.NavigationDrawer;
import com.mabem.homebook.Utils.NotificationMenuItemListener;
import com.mabem.homebook.ViewModels.NotificationViewModel;
import com.mabem.homebook.databinding.FragmentNotificationsBinding;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment implements NotificationMenuItemListener {

    private FragmentNotificationsBinding notificationsBinding;
    private NotificationViewModel notificationViewModel;
    private NotificationAdapter adapter;

    private ArrayList<AdminNotification> adminNotificationList = new ArrayList<>();
    private ArrayList<UserNotification> userNotificationList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ((NavigationDrawer) getActivity()).disableNavDrawer();

        notificationsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_notifications, container, false);

        notificationsBinding.myNotifications.setHasFixedSize(true);

        notificationViewModel = new ViewModelProvider(this).get(NotificationViewModel.class);

        notificationViewModel.updateCurrentNotification();

        notificationViewModel.getCurrentNotification().observe(getViewLifecycleOwner(), notification -> {

            if (notification != null) {
                List<AdminNotification> adminNotifications = notification.getAdminNotifications();
                List<UserNotification> userNotifications = notification.getUserNotifications();
                adminNotificationList.clear();
                userNotificationList.clear();
                adminNotificationList.addAll(adminNotifications);
                userNotificationList.addAll(userNotifications);
                adapter = new NotificationAdapter(getContext(), adminNotificationList, userNotificationList, this);
                notificationsBinding.myNotifications.setAdapter(adapter);
            }
        });


        return notificationsBinding.getRoot();
    }

    @Override
    public void onAcceptJoinRequest(AdminNotification adminNotification, int position) {
        notificationViewModel.acceptJoinRequest(adminNotification);
        adminNotificationList.remove(position);
        adapter.notifyItemRemoved(position);
    }

    @Override
    public void onDeclineJoinRequest(AdminNotification adminNotification, int position) {
        notificationViewModel.declineJoinRequest(adminNotification);
        adminNotificationList.remove(position);
        adapter.notifyItemRemoved(position);
    }

    @Override
    public void onOKClicked(UserNotification userNotification, int position) {
        notificationViewModel.deleteUserNotification(userNotification);
        userNotificationList.remove(position);
        adapter.notifyItemRemoved(position);
    }
}