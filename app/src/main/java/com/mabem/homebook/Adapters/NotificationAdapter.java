package com.mabem.homebook.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mabem.homebook.Model.AdminNotification;
import com.mabem.homebook.Model.UserNotification;
import com.mabem.homebook.R;
import com.mabem.homebook.Utils.NotificationMenuItemListener;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int USER_NOTIFICATION = 0;
    private final int ADMIN_NOTIFICATION = 1;

    Context context;
    List<AdminNotification> adminNotifications;
    List<UserNotification> userNotifications;
    NotificationMenuItemListener notificationMenuItemListener;

    public NotificationAdapter(Context context,
                               List<AdminNotification> adminNotifications,
                               List<UserNotification> userNotifications,
                               NotificationMenuItemListener notificationMenuItemListener) {
        this.context = context;
        this.adminNotifications = adminNotifications;
        this.userNotifications = userNotifications;
        this.notificationMenuItemListener = notificationMenuItemListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_menu_item, parent, false);

        if(viewType == USER_NOTIFICATION){
            return new UserViewHolder(itemView, notificationMenuItemListener);
        }

        if(viewType == ADMIN_NOTIFICATION){
            return new AdminViewHolder(itemView, notificationMenuItemListener);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof UserViewHolder){
            ((UserViewHolder) holder).populate(userNotifications.get(position));
        }

        if(holder instanceof AdminViewHolder){
            ((AdminViewHolder) holder).populate(adminNotifications.get(position - userNotifications.size()));
        }
    }


    @Override
    public int getItemCount() {
        return adminNotifications.size() + userNotifications.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(position < userNotifications.size()){
            return USER_NOTIFICATION;
        }

        if(position - userNotifications.size() < adminNotifications.size()){
            return ADMIN_NOTIFICATION;
        }

        return -1;
    }


    public class UserViewHolder extends RecyclerView.ViewHolder{

        private TextView firstName;
        private TextView secondName;
        private TextView notText;
        private ImageView acceptButton;
        private ImageView declineButton;
        private TextView acceptText;
        private TextView declineText;
        private FrameLayout shape;

        private NotificationMenuItemListener clickListener;


        public UserViewHolder(@NonNull View itemView, NotificationMenuItemListener clickListener) {
            super(itemView);
            firstName = itemView.findViewById(R.id.first_name);
            secondName = itemView.findViewById(R.id.second_name);
            notText = itemView.findViewById(R.id.not_text);
            acceptButton = itemView.findViewById(R.id.accept_button);
            declineButton = itemView.findViewById(R.id.decline_button);
            acceptText = itemView.findViewById(R.id.accept_text);
            declineText = itemView.findViewById(R.id.decline_text);
            shape = itemView.findViewById(R.id.notification_shape);
            this.clickListener = clickListener;

        }

        public void populate(UserNotification userNotification){
            if(userNotification.getType().equals(UserNotification.TYPE_ACCEPT)){
                firstName.setText(userNotification.getHomeName());
                secondName.setVisibility(View.INVISIBLE);
                notText.setText(R.string.home_accepted_you_text);

                secondName.setVisibility(View.INVISIBLE);
                declineText.setVisibility(View.INVISIBLE);
                declineButton.setVisibility(View.INVISIBLE);

                acceptText.setText(R.string.ok);

                acceptButton.setOnClickListener(v -> {
                    clickListener.onOKClicked(userNotification, getAdapterPosition());
                });
                acceptText.setOnClickListener(v -> {
                    clickListener.onOKClicked(userNotification, getAdapterPosition());
                });

            }else if(userNotification.getType().equals(UserNotification.TYPE_DECLINE)) {
                firstName.setText(userNotification.getHomeName());
                secondName.setVisibility(View.INVISIBLE);
                declineText.setVisibility(View.INVISIBLE);
                declineButton.setVisibility(View.INVISIBLE);
                notText.setText(R.string.home_declined_you_text);

                acceptText.setText(R.string.ok);

                acceptButton.setOnClickListener(v -> {
                    clickListener.onOKClicked(userNotification, getAdapterPosition());
                });
                acceptText.setOnClickListener(v -> {
                    clickListener.onOKClicked(userNotification, getAdapterPosition());
                });
            }
        }

    }

    public class AdminViewHolder extends RecyclerView.ViewHolder{

        private TextView firstName;
        private TextView secondName;
        private TextView notText;
        private ImageView acceptButton;
        private ImageView declineButton;
        private TextView acceptText;
        private TextView declineText;
        private FrameLayout shape;

        private NotificationMenuItemListener clickListener;

        public AdminViewHolder(@NonNull View itemView, NotificationMenuItemListener clickListener) {
            super(itemView);

            firstName = itemView.findViewById(R.id.first_name);
            secondName = itemView.findViewById(R.id.second_name);
            notText = itemView.findViewById(R.id.not_text);
            acceptButton = itemView.findViewById(R.id.accept_button);
            declineButton = itemView.findViewById(R.id.decline_button);
            acceptText = itemView.findViewById(R.id.accept_text);
            declineText = itemView.findViewById(R.id.decline_text);
            shape = itemView.findViewById(R.id.notification_shape);

            this.clickListener = clickListener;
        }

        public void populate(AdminNotification adminNotification){
            firstName.setText(adminNotification.getUserName());
            secondName.setText(adminNotification.getHomeName());
            notText.setText(R.string.join_request_text);

            acceptButton.setOnClickListener(v -> {
                clickListener.onAcceptJoinRequest(adminNotification, getAdapterPosition());
            });
            acceptText.setOnClickListener(v -> {
                clickListener.onAcceptJoinRequest(adminNotification, getAdapterPosition());
            });
            declineButton.setOnClickListener(v ->{
                clickListener.onDeclineJoinRequest(adminNotification, getAdapterPosition());
            });
            declineText.setOnClickListener(v ->{
                clickListener.onDeclineJoinRequest(adminNotification, getAdapterPosition());
            });
        }
    }
}
