package com.mabem.homebook.Fragments.Main.Home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
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
import com.mabem.homebook.Utils.Util;
import com.mabem.homebook.ViewModels.HomeViewModel;
import com.mabem.homebook.databinding.FragmentStatisticBinding;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.lang.Math;

public class StatisticFragment extends Fragment implements AdapterView.OnItemSelectedListener {


    private FragmentStatisticBinding fragmentStatisticBinding;
    private HomeViewModel homeViewModel;
    private Member currentMember;
    private ArrayList<Receipt> receipts = new ArrayList<>();


    private int month = 0;
    private int year = 0;
    private Calendar cal;
    private int numMembers = 1;
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

        //========================================= Get Home and Member and number of Members
        homeViewModel.getCurrentMember().observe(getViewLifecycleOwner(), member -> {
            if (member != null) {
                currentMember = member;
            }
        });

        homeViewModel.updateHomeWithMembers();
        homeViewModel.getCurrentHome().observe(getViewLifecycleOwner(), h -> {
            if(h != null && h.getMember_role().size() != 0){
                receipts = h.getReceipts();
                numMembers = h.getMember_role().size();
                calculateStatistics(getContext().getResources().getString(R.string.this_month));
            }
        });
        return fragmentStatisticBinding.getRoot();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedMonth = parent.getItemAtPosition(position).toString().trim();
        calculateStatistics(selectedMonth);
    }

    @SuppressLint("SetTextI18n")
    private void calculateStatistics(String selectedMonth) {
        if(!receipts.isEmpty()){
            Double totalHomeExpense = 0.0;
            Double totalSelf = 0.0;
            double onEachMember = 0.0;
            double absoluteShouldGetOrPay =  0.0;

            int selectedMon;
            if(selectedMonth.equals(getContext().getResources().getString(R.string.this_month))){
                selectedMon = month;
            }else if(selectedMonth.equals(getContext().getResources().getString(R.string.last_month))){
                selectedMon = ((((month - 1) % 12) + 12) % 12);
            }else{
                selectedMon = ((((Integer.parseInt(selectedMonth) - 1) % 12) + 12) % 12);
            }

            for(Receipt receipt : receipts){
                cal.setTime(receipt.getDate());
                if(selectedMon <= month){
                    if( cal.get(Calendar.MONTH) == selectedMon && (cal.get(Calendar.YEAR) == year) ){
                        totalHomeExpense += receipt.getTotal();
                        if(receipt.getMemberId().equals(currentMember.getId())){
                            totalSelf += receipt.getTotal();
                        }
                    }
                }else{
                    if( cal.get(Calendar.MONTH) == selectedMon && (cal.get(Calendar.YEAR) == year-1) ){
                        totalHomeExpense += receipt.getTotal();
                        if(receipt.getMemberId().equals(currentMember.getId())){
                            totalSelf += receipt.getTotal();
                        }
                    }
                }
            }

            onEachMember = totalHomeExpense/numMembers;

            if((onEachMember < Double.MAX_VALUE && onEachMember > Double.MIN_VALUE) || onEachMember == 0.0){

                double shouldGetOrPay = totalSelf - onEachMember;
                absoluteShouldGetOrPay = Math.abs(totalSelf - onEachMember);

                if(shouldGetOrPay > 0){
                    fragmentStatisticBinding.youShouldPayGet.setText(getContext().getResources().getString(R.string.you_should_get));
                    fragmentStatisticBinding.youShouldPayGet.setTextColor(Color.GREEN);
                }else{
                    fragmentStatisticBinding.youShouldPayGet.setText(getContext().getResources().getString(R.string.you_should_pay));
                    fragmentStatisticBinding.youShouldPayGet.setTextColor(Color.RED);
                }
                fragmentStatisticBinding.shouldPayGetAmount.setText(Util.round(absoluteShouldGetOrPay, 2).toString());
            }
            fragmentStatisticBinding.everyoneAmount.setText(Util.round(onEachMember, 2).toString());
            fragmentStatisticBinding.selfAmount.setText(Util.round(totalSelf, 2).toString());
            fragmentStatisticBinding.totalAmount.setText(Util.round(totalHomeExpense, 2).toString());
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        parent.setSelection(0);
    }
}