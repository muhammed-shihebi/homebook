package com.mabem.homebook.Fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.mabem.homebook.R;
import com.mabem.homebook.databinding.FragmentRemiderSetBinding;
import com.mabem.homebook.Model.Reminder;

import java.util.ArrayList;
import java.util.Calendar;

public class ReminderSetFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String REMINDER_SET_FRAGMENT_TAG = "Reminder Set Fragment";

    private FragmentRemiderSetBinding reminderSetBinding;
    private int hour = 0;
    private int minutes = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        // Inflate the layout (link this fragment with login_fragment layout).
        reminderSetBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_remider_set, container, false);

        // To link the the view with the viewModel
        reminderSetBinding.setLifecycleOwner(this);

        //========================================= Listeners for Date- and TimePickers

        reminderSetBinding.reminderDatePickerButton.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    reminderSetBinding.reminderDatePickerButton.setText(dayOfMonth+"/"+month+"/"+year);
                }
            }, year, month, day);

            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.show();
        });


        reminderSetBinding.reminderTimePickerButton.setOnClickListener(v -> {

            TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            hour = hourOfDay;
                            minutes = minute;
                            Calendar cal = Calendar.getInstance();
                            cal.set(0,0,0,hour,minutes);
                            reminderSetBinding.reminderTimePickerButton.setText(DateFormat.format("hh:mm",cal));

                        }
                    },12,0,false);
            timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#857C23")));
            timePickerDialog.updateTime(hour,minutes);
            timePickerDialog.show();
        });

        //========================================= Initialising of Spinner (Drop down List)
        ArrayList<String> frequencies = new ArrayList<String>();
        frequencies.add(Reminder.NEVER);
        frequencies.add(Reminder.DAILY);
        frequencies.add(Reminder.WEEKLY);
        frequencies.add(Reminder.MONTHLY);
        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, frequencies);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reminderSetBinding.reminderFrequencySpinner.setAdapter(arrayAdapter);




        return reminderSetBinding.getRoot();
    }


    //========================================= Listeners of Spinner (Drop down List)
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId() == R.id.reminder_frequency_spinner){
            String value = parent.getItemAtPosition(position).toString();
            reminderSetBinding.reminderFrequencyTextview.setText(value);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        String value = parent.getItemAtPosition(0).toString();
        reminderSetBinding.reminderFrequencyTextview.setText(value);
    }
}