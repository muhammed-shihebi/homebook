package com.mabem.homebook.Utils;

import com.mabem.homebook.Model.AdminNotification;
import com.mabem.homebook.Model.UserNotification;

public interface NotificationMenuItemListener {

    public void onAcceptJoinRequest(AdminNotification adminNotification, int position);

    public void onDeclineJoinRequest(AdminNotification adminNotification, int position);

    public void onAcceptInvitation(UserNotification userNotification, int position);

    public void onDeclineInvitation(UserNotification userNotification, int position);

    public void onOKClicked(UserNotification userNotification, int position);

}
