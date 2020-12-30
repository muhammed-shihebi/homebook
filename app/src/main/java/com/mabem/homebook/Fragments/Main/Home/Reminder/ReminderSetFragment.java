package com.mabem.homebook.Fragments.Main.Home.Reminder;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.mabem.homebook.Model.Member;
import com.mabem.homebook.R;
import com.mabem.homebook.ViewModels.HomeViewModel;
import com.mabem.homebook.databinding.FragmentRemiderSetBinding;
import com.mabem.homebook.Model.Reminder;

import java.util.ArrayList;
import java.util.Calendar;

public class ReminderSetFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String REMINDER_SET_FRAGMENT_TAG = "Reminder Set Fragment";

    private FragmentRemiderSetBinding reminderSetBinding;
    private HomeViewModel homeViewModel;
    private Member currentMember;

    private Calendar cal;
    private String hour = "";
    private String minutes = "";
    private String frequency = "";

    private static boolean toEditFlag = false; //Admin accesses this reminder
    private static Reminder toEditReminder;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        reminderSetBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_remider_set, container, false);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        homeViewModel.getCurrentMember().observe(getViewLifecycleOwner(), member -> {
            if (member != null) {
                currentMember = member;
            }
        });


//========================================= Listeners for Date- and TimePickers

        reminderSetBinding.reminderDatePickerButton.setOnClickListener(v -> {
            cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    cal.set(year,month,dayOfMonth,hourOfDay,minute);
                                    if(cal.get(Calendar.MINUTE) < 10){
                                        minutes = "0"+cal.get(Calendar.MINUTE);
                                    }else{
                                        minutes = cal.get(Calendar.MINUTE)+"";
                                    }
                                    if(cal.get(Calendar.HOUR_OF_DAY) < 10){
                                        hour = "0"+cal.get(Calendar.HOUR_OF_DAY);
                                    }else{
                                        hour = cal.get(Calendar.HOUR_OF_DAY)+"";
                                    }
                                    reminderSetBinding.reminderDatePickerButton.setText(dayOfMonth+"/"+(month+1)+"/"+year+" - "+hour+":"+minutes);

                                }
                            },12,0,true);
                    timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2b2f38")));
                    timePickerDialog.updateTime(cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE));
                    timePickerDialog.show();
                }
            }, year, month, day);

            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.updateDate(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        //========================================= Initialising of Spinner (Drop down List)
        ArrayList<String> frequencies = new ArrayList<String>();
        frequencies.add(Reminder.ONCE);
        frequencies.add(Reminder.DAILY);
        frequencies.add(Reminder.WEEKLY);
        frequencies.add(Reminder.MONTHLY);
        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, frequencies);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reminderSetBinding.reminderFrequencySpinner.setAdapter(arrayAdapter);
        reminderSetBinding.reminderFrequencySpinner.setOnItemSelectedListener(this);


        if(toEditFlag){
            reminderSetBinding.reminderSetName.setText(toEditReminder.getName());
            cal = Calendar.getInstance();
            cal.setTime(toEditReminder.getDate());
            if(cal.get(Calendar.MINUTE) < 10){
                minutes = "0"+cal.get(Calendar.MINUTE);
            }else{
                minutes = cal.get(Calendar.MINUTE)+"";
            }
            if(cal.get(Calendar.HOUR_OF_DAY) < 10){
                hour = "0"+cal.get(Calendar.HOUR_OF_DAY);
            }else{
                hour = cal.get(Calendar.HOUR_OF_DAY)+"";
            }
            reminderSetBinding.reminderDatePickerButton.setText(cal.get(Calendar.DAY_OF_MONTH)+"/"+(cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.YEAR)+" - "+hour+":"+minutes);
            if(toEditReminder.getFrequency().equals("Once")){
                reminderSetBinding.reminderFrequencySpinner.setSelection(0);
            }else if(toEditReminder.getFrequency().equals("Daily")){
                reminderSetBinding.reminderFrequencySpinner.setSelection(1);
            }else if(toEditReminder.getFrequency().equals("Weekly")){
                reminderSetBinding.reminderFrequencySpinner.setSelection(2);
            }else if(toEditReminder.getFrequency().equals("Monthly")){
                reminderSetBinding.reminderFrequencySpinner.setSelection(3);
            }
            frequency = toEditReminder.getFrequency();
        }else{
            reminderSetBinding.reminderDeleteButton.setText(R.string.cancel);
        }



        reminderSetBinding.reminderSetButton.setOnClickListener(v -> {
            boolean nameEmpty = reminderSetBinding.reminderSetName.getText().toString().trim().isEmpty();

            if(isToEditFlag()){
                if(!nameEmpty){
                    String reminderName = reminderSetBinding.reminderSetName.getText().toString().trim();
                    Reminder updated = new Reminder(toEditReminder.getId(), reminderName, frequency, cal.getTime());
                    homeViewModel.updateReminder(updated);
                    homeViewModel.getResultMessage().observe(getViewLifecycleOwner(), s -> {
                        Toast.makeText(requireContext(), homeViewModel.getResultMessage().getValue(), Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(v).navigate(R.id.action_reminderSetFragment_to_remindersFragment);
                    });
                }else{
                    Toast.makeText(requireContext(), R.string.please_enter_name_for_reminder_message, Toast.LENGTH_SHORT).show();
                }
            }else{
                if(!nameEmpty && (cal != null) ){
                    String reminderName = reminderSetBinding.reminderSetName.getText().toString().trim();
                    Reminder newReminder = new Reminder(reminderName, cal.getTime(), frequency);
                    homeViewModel.setReminder(newReminder);
                    homeViewModel.getResultMessage().observe(getViewLifecycleOwner(), s -> {
                        Toast.makeText(requireContext(), homeViewModel.getResultMessage().getValue(), Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(v).navigate(R.id.action_reminderSetFragment_to_remindersFragment);
                    });
                }else{
                    if(nameEmpty){
                        Toast.makeText(requireContext(), R.string.please_enter_name_for_reminder_message, Toast.LENGTH_SHORT).show();
                    }else if(cal == null){
                        Toast.makeText(requireContext(), R.string.please_enter_date_of_reminder_message, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        reminderSetBinding.reminderDeleteButton.setOnClickListener(v -> {
            if(isToEditFlag()){
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.delete_reminder_warning)
                        .setMessage(R.string.delete_reminder_warning_message)
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {}
                        })
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                homeViewModel.deleteReminder(toEditReminder.getId());
                                homeViewModel.getResultMessage().observe(getViewLifecycleOwner(), s -> {
                                    Toast.makeText(requireContext(), homeViewModel.getResultMessage().getValue(), Toast.LENGTH_SHORT).show();
                                    Navigation.findNavController(v).navigate(R.id.action_reminderSetFragment_to_remindersFragment);
                                });

                            }
                        });
                AlertDialog mDialog = builder.create();
                mDialog.show();
            }else{
                Navigation.findNavController(v).navigate(R.id.action_reminderSetFragment_to_remindersFragment);
            }
        });




        return reminderSetBinding.getRoot();
    }


    public static boolean isToEditFlag() {
        return toEditFlag;
    }

    public static void setToEditFlag(boolean toEditFlag) {
        ReminderSetFragment.toEditFlag = toEditFlag;
    }

    public static Reminder getToEditReminder() {
        return toEditReminder;
    }

    public static void setToEditReminder(Reminder toEditReminder) {
        ReminderSetFragment.toEditReminder = toEditReminder;
    }


    //========================================= Listeners of Spinner (Drop down List)
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        frequency = parent.getItemAtPosition(position).toString().trim();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        frequency = parent.getItemAtPosition(0).toString();
    }
}