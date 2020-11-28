package com.mabem.homebook.main_fragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.mabem.homebook.R;
import com.mabem.homebook.databinding.MainFragmentBinding;

public class MainFragment extends Fragment {

    //========================================= Attribute

    private MainViewModel mViewModel;
    private MainFragmentBinding mainBinding;
    private FirebaseAuth firebaseAuth;
    private String MAIN_FRAGMENT_TAG = "Main Fragment";
    //========================================= Functions

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        mainBinding = DataBindingUtil.inflate(inflater, R.layout.main_fragment, container, false);
        firebaseAuth = FirebaseAuth.getInstance();


        mainBinding.signOutButton.setOnClickListener(v -> {
            Log.i(MAIN_FRAGMENT_TAG, "onCreateView: Signing out the user... ");
            firebaseAuth.signOut();
            Navigation.findNavController(v).navigate(R.id.action_mainFragment_to_loginFragment);
        });
        return mainBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        // TODO: Use the ViewModel
    }

}