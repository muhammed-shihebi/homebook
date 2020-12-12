package com.mabem.homebook.Fragments;

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
    private MainViewModel mainViewModel;
    private MainFragmentBinding mainBinding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();


        mainBinding = DataBindingUtil.inflate(inflater, R.layout.main_fragment, container, false);

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.updateCurrentUser();
        mainViewModel.getCurrentUser().observe(requireActivity(), user -> {
            if (user == null) {
                Navigation.findNavController(
                        requireActivity(),
                        R.id.welcome_view_text
                ).navigate(R.id.action_mainFragment_to_loginFragment);
            } else {
                mainBinding.userName.setText(user.getName());
            }
        });


        mainBinding.signOutButton.setOnClickListener(v -> {
            mainViewModel.signOut();
        });

        return mainBinding.getRoot();
    }
}