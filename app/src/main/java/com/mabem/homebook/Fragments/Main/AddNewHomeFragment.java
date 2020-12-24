package com.mabem.homebook.Fragments.Main;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mabem.homebook.R;
import com.mabem.homebook.ViewModels.HomeViewModel;
import com.mabem.homebook.databinding.FragmentCreateHomeBinding;

public class AddNewHomeFragment extends Fragment {

    private FragmentCreateHomeBinding createHomeBinding;
    private HomeViewModel homesViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        createHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_home, container, false);

        homesViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        createHomeBinding.saveHomeButton.setOnClickListener(v -> {
            Boolean isPrivate = createHomeBinding.privateCheckbox.isChecked();
            String homeName = createHomeBinding.homeName.getText().toString().trim();
            if(homeName.isEmpty()){
                Toast.makeText(requireContext(), R.string.please_enter_name_for_home_message, Toast.LENGTH_SHORT).show();
            }else{
                homesViewModel.addNewHome(homeName, isPrivate);
                homesViewModel.setShouldShowResultMessage(true);
            }
        });

        homesViewModel.getResultMessage().observe(getViewLifecycleOwner(), s -> {
            if(homesViewModel.getShouldShowResultMessage()){
                Toast.makeText(requireContext(), homesViewModel.getResultMessage().getValue(), Toast.LENGTH_SHORT).show();
            }
            homesViewModel.setShouldShowResultMessage(false);
        });

        return createHomeBinding.getRoot();
    }


}