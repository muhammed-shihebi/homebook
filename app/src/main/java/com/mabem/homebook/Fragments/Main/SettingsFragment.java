package com.mabem.homebook.Fragments.Main;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mabem.homebook.R;
import com.mabem.homebook.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding settingsBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //========================================= Set up DataBinding

        settingsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);

        //========================================= Set up on click listeners

        settingsBinding.profileButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.editProfileFragment);
        });

        settingsBinding.languageButton.setOnClickListener(v ->{
            Navigation.findNavController(v).navigate(R.id.changeLanguageFragment);
        });

        settingsBinding.aboutButton.setOnClickListener(v ->{
            Navigation.findNavController(v).navigate(R.id.aboutFragment);
        });

        return settingsBinding.getRoot();
    }
}