package com.mabem.homebook.Fragments.Main.Home;

import android.graphics.Color;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.mabem.homebook.Model.Home;
import com.mabem.homebook.Model.Member;
import com.mabem.homebook.Model.Receipt;
import com.mabem.homebook.R;
import com.mabem.homebook.ViewModels.HomeViewModel;
import com.mabem.homebook.databinding.FragmentStatisticBinding;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.lang.Math;

public class StatisticFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private static final String STATISTICS_FRAGMENT_TAG = "Statistics Fragment";
    private FragmentStatisticBinding fragmentStatisticBinding;
    private HomeViewModel homeViewModel;
    private Member currentMember;
    private static int numMembers = -1;
    private Calendar cal;
    private static String home_id = "";
    private ArrayList<Receipt> receipts;
    private int month = 0;
    private int year = 0;
    private boolean flag = false;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentStatisticBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_statistic, container, false);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        //=============================================================Initialising Spinner

        cal = Calendar.getInstance();
        month = cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);
        int thisMonth = cal.get(Calendar.MONTH);
        thisMonth--;
        thisMonth = ((((thisMonth) % 12) + 12) % 12);

        ArrayList<String> months = new ArrayList<String>();
        months.add(getContext().getResources().getString(R.string.this_month));
        months.add(getContext().getResources().getString(R.string.last_month));
        for(int i = 0; i < 10; i++){
            months.add(thisMonth+"");
            thisMonth--;
            thisMonth = ((((thisMonth) % 12) + 12) % 12);
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, months);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fragmentStatisticBinding.monthSpinner.setAdapter(arrayAdapter);
        fragmentStatisticBinding.monthSpinner.setOnItemSelectedListener(this);


        homeViewModel.updateHomeWithMembers();
        homeViewModel.getCurrentMember().observe(getViewLifecycleOwner(), member -> {
            if (member != null) {
                currentMember = member;
            }
        });

        homeViewModel.getCurrentHome().observe(getViewLifecycleOwner(), h -> {
            if(h != null){
                receipts = h.getReceipts();
                numMembers = h.getMember_role().size();
                Log.d("Stat", "onCreateView: SizeMembers in Orta "+numMembers);
                fragmentStatisticBinding.monthSpinner.setSelection(0);
            }
        });



        return fragmentStatisticBinding.getRoot();
    }

    public static String getHome_id() {
        return home_id;
    }

    public static void setHome_id(String home_id) {
        StatisticFragment.home_id = home_id;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Double totalAmount = 0.0;
        Double totalSelf = 0.0;
        Double shouldEveryOne = 0.0;
        Double shouldGet_Pay = 0.0;

        String selectedMonth = parent.getItemAtPosition(position).toString().trim();
        int selectedMon = 0;
        if(selectedMonth.equals(getContext().getResources().getString(R.string.this_month))){
            selectedMon = month;
        }else if(selectedMonth.equals(getContext().getResources().getString(R.string.last_month))){
            selectedMon = ((((month - 1) % 12) + 12) % 12);
        }else{
            selectedMon = ((((Integer.parseInt(selectedMonth) - 1) % 12) + 12) % 12);
        }
        ArrayList<Receipt> thismonthReceipt = new ArrayList<Receipt>();
        for(Receipt receipt : receipts){
            cal.setTime(receipt.getDate());
            if(selectedMon <= month){
                if( cal.get(Calendar.MONTH) == selectedMon && (cal.get(Calendar.YEAR) == year) ){

                    totalAmount += receipt.getTotal();
                    if(receipt.getMemberId().equals(currentMember.getId())){
                        totalSelf += receipt.getTotal();
                    }
                }
            }else{
                if( cal.get(Calendar.MONTH) == selectedMon && (cal.get(Calendar.YEAR) == year-1) ){
                    totalAmount += receipt.getTotal();
                    if(receipt.getMemberId().equals(currentMember.getId())){
                        totalSelf += receipt.getTotal();
                    }
                }
            }
        }


        Log.d("Stat", "Size of member "+numMembers);
        shouldEveryOne = totalAmount/numMembers;

        DecimalFormat df = new DecimalFormat("#######.#");
        String i = df.format(shouldEveryOne);
        Log.d("Stat", "onItemSelected: i "+i);
        if(shouldEveryOne < Double.MAX_VALUE && shouldEveryOne > Double.MIN_VALUE){
            shouldEveryOne = Double.parseDouble(i);

            Double shouldGetOrPay = shouldEveryOne - totalSelf;
            shouldGet_Pay = Math.abs(shouldGetOrPay);

            if(shouldGetOrPay < 0 ){
                fragmentStatisticBinding.youShouldPayGet.setText(getContext().getResources().getString(R.string.you_should_get));
                fragmentStatisticBinding.youShouldPayGet.setTextColor(Color.GREEN);
            }else{
                fragmentStatisticBinding.youShouldPayGet.setText(getContext().getResources().getString(R.string.you_should_pay));
                fragmentStatisticBinding.youShouldPayGet.setTextColor(Color.RED);
            }
            fragmentStatisticBinding.shouldPayGetAmount.setText(shouldGet_Pay.toString());
        }else{  //============== Size of Members is null
            fragmentStatisticBinding.youShouldPayGet.setText(getContext().getResources().getString(R.string.you_should_pay));
            shouldGet_Pay = Double.parseDouble(shouldGet_Pay.toString());
            fragmentStatisticBinding.shouldPayGetAmount.setText(shouldGet_Pay.toString());
            fragmentStatisticBinding.youShouldPayGet.setTextColor(Color.RED);
        }
        fragmentStatisticBinding.everyoneAmount.setText(i);
        fragmentStatisticBinding.selfAmount.setText(totalSelf.toString());
        fragmentStatisticBinding.totalAmount.setText(totalAmount.toString());

        Log.d("Stat", "TotalAmount "+totalAmount);
        Log.d("Stat", "TotalSelf "+totalSelf);
        Log.d("Stat", "ShouldPayGet "+shouldGet_Pay);
        Log.d("Stat", "ShouldEveryone "+shouldEveryOne);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        parent.setSelection(0);
    }

    public static void setNumMembers(int numMembers) {
        StatisticFragment.numMembers = numMembers;
    }
}