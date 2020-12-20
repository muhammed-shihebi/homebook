package com.mabem.homebook.Fragments.Main;

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
import androidx.navigation.Navigation;

import com.mabem.homebook.R;
import com.mabem.homebook.ViewModels.MainViewModel;
import com.mabem.homebook.databinding.MainFragmentBinding;

public class MainFragment extends Fragment {

    //========================================= Attribute

    private static final String MAIN_FRAGMENT_TAG = "Main Fragment";

    private MainFragmentBinding mainBinding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();


        mainBinding = DataBindingUtil.inflate(inflater, R.layout.main_fragment, container, false);


        return mainBinding.getRoot();
    }
}