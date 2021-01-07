package com.mabem.homebook.Utils;

import com.mabem.homebook.Model.Objects.AdminNotification;
import com.mabem.homebook.Model.Objects.UserNotification;

public interface NotificationMenuItemListener {

    public void onAcceptJoinRequest(AdminNotification adminNotification, int position);

    public void onDeclineJoinRequest(AdminNotification adminNotification, int position);

    public void onOKClicked(UserNotification userNotification, int position);

}
