package com.mabem.homebook.Fragments.Main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.mabem.homebook.Model.Home;
import com.mabem.homebook.Model.Member;
import com.mabem.homebook.R;
import com.mabem.homebook.ViewModels.MyHomesViewModel;
import com.mabem.homebook.databinding.MainFragmentBinding;

import java.util.HashMap;

public class MainFragment extends Fragment {

    //========================================= Attribute

    private static final String MAIN_FRAGMENT_TAG = "Main Fragment";

    private MainFragmentBinding mainBinding;
    private MyHomesViewModel myHomesViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();


        mainBinding = DataBindingUtil.inflate(inflater, R.layout.main_fragment, container, false);
        myHomesViewModel = new ViewModelProvider(this).get(MyHomesViewModel.class);

        myHomesViewModel.updateCurrentMember();

        myHomesViewModel.getCurrentMember().observe(getViewLifecycleOwner(), member -> {
            if(member != null){
                HashMap<Home, Boolean> memberHomes = member.getHome_role();
                for(Home home : memberHomes.keySet()){
                    mainBinding.myHomes.setText(home.getName());
                }
            }
        });



        return mainBinding.getRoot();
    }
}