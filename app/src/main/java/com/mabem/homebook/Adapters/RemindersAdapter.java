package com.mabem.homebook.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.mabem.homebook.Model.Objects.Reminder;
import com.mabem.homebook.R;
import com.mabem.homebook.Views.Main.Home.Reminder.ReminderSetFragment;

import java.util.ArrayList;
import java.util.Calendar;

public class RemindersAdapter extends RecyclerView.Adapter<RemindersAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<Reminder> list;
    private boolean isAdmin;
    private Reminder r;

    public RemindersAdapter(Context context, ArrayList<Reminder> list, boolean isAdmin) {
        this.context = context;
        this.list = list;
        this.isAdmin = isAdmin;
    }

    @NonNull
    @Override
    public RemindersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.reminder_menu_item, parent, false);
        return new RemindersAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RemindersAdapter.ViewHolder holder, int position) {
        r = list.get(position);
        holder.populate(r);
    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout shape;
        private TextView reminderName;
        private TextView reminderDate;
        private TextView reminderTime;
        private TextView reminderFrequency;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            shape = itemView.findViewById(R.id.reminder_shape);
            reminderName = itemView.findViewById(R.id.reminder_name);
            reminderDate = itemView.findViewById(R.id.reminder_date);
            reminderTime = itemView.findViewById(R.id.reminder_time);
            reminderFrequency = itemView.findViewById(R.id.reminder_frequency);

        }

        public void populate(Reminder r) {
            reminderName.setText(r.getName().trim());
            Calendar cal = Calendar.getInstance();
            cal.setTime(r.getDate());
            reminderDate.setText(cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR));
            String hour = "";
            String minute = "";
            if (cal.get(Calendar.MINUTE) < 10) {
                minute = "0" + cal.get(Calendar.MINUTE);
            } else {
                minute = cal.get(Calendar.MINUTE) + "";
            }
            if (cal.get(Calendar.HOUR_OF_DAY) < 10) {
                hour = "0" + cal.get(Calendar.HOUR_OF_DAY);
            } else {
                hour = cal.get(Calendar.HOUR_OF_DAY) + "";
            }
            reminderTime.setText(hour + ":" + minute);
            reminderFrequency.setText(r.getFrequency());

            shape.setOnClickListener(v -> {
                if (isAdmin) {
                    Navigation.findNavController(v).navigate(R.id.action_remindersFragment_to_reminderSetFragment);
                    ReminderSetFragment.setToEditReminder(r);
                    ReminderSetFragment.setToEditFlag(true);
                }
            });
        }
    }


}
