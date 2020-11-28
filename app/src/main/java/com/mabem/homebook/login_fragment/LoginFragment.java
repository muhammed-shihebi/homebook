package com.mabem.homebook.login_fragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mabem.homebook.R;
import com.mabem.homebook.databinding.LoginFragmentBinding;

import java.util.zip.Inflater;

public class LoginFragment extends Fragment {

    private LoginViewModel mViewModel;
    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    private LoginFragmentBinding loginBinding;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Not that RegisterFragment works in a very similar way

        // Hide action bar.
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        // Block landscape orientation.
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Inflate the layout (link this fragment with login_fragment layout).
        loginBinding = DataBindingUtil.inflate(inflater, R.layout.login_fragment, container, false);

        // Setting up an on click listener for sign up button.
        loginBinding.logInSignUpButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_registerFragment);
        });

        loginBinding.logInLogInButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_mainFragment);
        });

        // The root must be returned to be displayed on the screen.
        // Make sure this statement stays at the bottom of onCreate function.
        return loginBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        // TODO: Use the ViewModel
    }

}