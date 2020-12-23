package com.mabem.homebook.Fragments.Main;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mabem.homebook.R;
import com.mabem.homebook.ViewModels.MyHomesViewModel;
import com.mabem.homebook.databinding.FragmentCreateHomeBinding;

public class AddNewHomeFragment extends Fragment {

    private FragmentCreateHomeBinding createHomeBinding;
    private MyHomesViewModel myHomesViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        createHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_home, container, false);

        myHomesViewModel = new ViewModelProvider(this).get(MyHomesViewModel.class);

        Log.i("asdf", "onCreateView: " + myHomesViewModel.getCurrentMember().getValue().getName());

        createHomeBinding.saveHomeButton.setOnClickListener(v -> {
            Boolean isPrivate = createHomeBinding.privateCheckbox.isChecked();
            String homeName = createHomeBinding.homeName.getText().toString().trim();
            if(homeName.isEmpty()){
                Toast.makeText(requireContext(), R.string.please_enter_name_for_home_message, Toast.LENGTH_SHORT).show();
            }else{
                myHomesViewModel.addNewHome(homeName, isPrivate);
                myHomesViewModel.setShouldShowResultMessage(true);
            }
        });

        myHomesViewModel.getResultMessage().observe(getViewLifecycleOwner(), s -> {
            if(myHomesViewModel.getShouldShowResultMessage()){
                Toast.makeText(requireContext(), myHomesViewModel.getResultMessage().getValue(), Toast.LENGTH_SHORT).show();
            }
            myHomesViewModel.setShouldShowResultMessage(false);
        });

        return createHomeBinding.getRoot();
    }


}