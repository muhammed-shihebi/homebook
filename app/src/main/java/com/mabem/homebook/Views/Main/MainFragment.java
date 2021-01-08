package com.mabem.homebook.Views.Main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mabem.homebook.Adapters.MyhomesAdapter;
import com.mabem.homebook.Model.Objects.Home;
import com.mabem.homebook.R;
import com.mabem.homebook.Utils.NavigationDrawer;
import com.mabem.homebook.ViewModels.HomeViewModel;
import com.mabem.homebook.databinding.MainFragmentBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class MainFragment extends Fragment {

    //========================================= Attribute

    private static final String MAIN_FRAGMENT_TAG = "Main Fragment";

    private MainFragmentBinding mainBinding;
    private HomeViewModel homesViewModel;
    private ArrayList list = new ArrayList();
    private RecyclerView.Adapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        ((NavigationDrawer) getActivity()).enableNavDrawer();

        mainBinding = DataBindingUtil.inflate(inflater, R.layout.main_fragment, container, false);
        homesViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        mainBinding.myHomes.setHasFixedSize(true);
        mainBinding.myHomes.setLayoutManager(new LinearLayoutManager(getContext()));

        homesViewModel.updateCurrentMember();

        homesViewModel.getCurrentMember().observe(getViewLifecycleOwner(), member -> {
            if (member != null) {

                ((NavigationDrawer) getActivity()).setCurrentMember(member);

                list.clear();
                HashMap<Home, Boolean> memberHomes = member.getHome_role();
                for (Home home : memberHomes.keySet()) {
                    list.add(home);
                }
                Collections.sort(list, new Comparator<Home>() {

                    @Override
                    public int compare(Home o1, Home o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                });
                adapter = new MyhomesAdapter(getContext(), list);
                mainBinding.myHomes.setAdapter(adapter);
            }
        });
        return mainBinding.getRoot();
    }
}