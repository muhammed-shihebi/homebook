package com.mabem.homebook.Fragments.Main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
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
import com.mabem.homebook.ViewModels.AuthViewModel;
import com.mabem.homebook.databinding.FragmentSettingsBinding;

import java.util.Locale;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding settingsBinding;
    private AuthViewModel authViewModel;

    private boolean langSelected = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

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
            showChangeLanguageDialog();
        });

        settingsBinding.aboutButton.setOnClickListener(v ->{
            Navigation.findNavController(v).navigate(R.id.aboutFragment);
        });



        return settingsBinding.getRoot();
    }

    private void showChangeLanguageDialog() {
        final String[] lang = {"English", "Deutsch"};

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select a Language")
                .setSingleChoiceItems(lang, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){
                            setLocale("en");
                            getActivity().recreate();
                        }else if(which == 1){
                            setLocale("de");
                            getActivity().recreate();
                        }

                        dialog.dismiss();
                    }
                });
        AlertDialog mDialog = builder.create();
        mDialog.show();
    }

    public void setLocale(String locale1) {
        Locale locale = new Locale(locale1);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getActivity().getBaseContext().getResources().updateConfiguration(config, getActivity().getBaseContext().getResources().getDisplayMetrics());

        SharedPreferences.Editor editor = getActivity().getSharedPreferences("settings",  getActivity().MODE_PRIVATE).edit();
        editor.putString("my_lang",locale1);
        editor.apply();
    }



}