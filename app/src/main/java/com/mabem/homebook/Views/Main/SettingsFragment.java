package com.mabem.homebook.Views.Main;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mabem.homebook.R;
import com.mabem.homebook.Utils.NavigationDrawer;
import com.mabem.homebook.Utils.Util;
import com.mabem.homebook.ViewModels.AuthViewModel;
import com.mabem.homebook.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding settingsBinding;
    private AuthViewModel authViewModel;

    private boolean langSelected = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ((NavigationDrawer) getActivity()).disableNavDrawer();

        //========================================= Set up DataBinding
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        settingsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);

        //========================================= Set up on click listeners

        settingsBinding.profileButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.editProfileFragment);
        });

        settingsBinding.changePasswordButton.setOnClickListener(v -> {
            authViewModel.changePassword();

            authViewModel.getResultMessage().observe(getViewLifecycleOwner(), message->{
                if(message != null){
                    Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
                }
            });
        });

        settingsBinding.languageButton.setOnClickListener(v ->{
            Util.showChangeLanguageDialog(getActivity());
        });

        settingsBinding.aboutButton.setOnClickListener(v ->{
            Navigation.findNavController(v).navigate(R.id.aboutFragment);
        });

        return settingsBinding.getRoot();
    }
}