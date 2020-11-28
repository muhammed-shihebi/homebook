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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mabem.homebook.Model.User;
import com.mabem.homebook.R;
import com.mabem.homebook.databinding.LoginFragmentBinding;

import java.util.zip.Inflater;

public class LoginFragment extends Fragment {

    //========================================= Attributes

    private LoginViewModel mViewModel;
    private LoginFragmentBinding loginBinding;
    private FirebaseAuth firebaseAuth;
    private String LOGIN_FRAGMENT_TAG = "Login Fragment";

    //========================================= Methods

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Not that RegisterFragment works in a very similar way

        // Initialize Firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Hide action bar.
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        // Block landscape orientation.
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Inflate the layout (link this fragment with login_fragment layout).
        loginBinding = DataBindingUtil.inflate(inflater, R.layout.login_fragment, container, false);

        // This call allows LiveData to update the layout automatically when needed.
        loginBinding.setLifecycleOwner(this);

        // Setting up an on click listener for sign up button.
        loginBinding.logInSignUpButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_registerFragment);
        });

        loginBinding.logInLogInButton.setOnClickListener(v -> {
            loginWithEmail(v);
        });

        // The root must be returned to be displayed on the screen.
        // Make sure this statement stays at the bottom of onCreate function.
        return loginBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){
            Log.i(LOGIN_FRAGMENT_TAG, "onViewCreated: There is a user logged in; " + currentUser.getEmail());
            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_mainFragment);
        }else{
            Log.i(LOGIN_FRAGMENT_TAG, "onViewCreated: There is no user logged in. Start normal. ");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        // TODO: Use the ViewModel
    }

    public void loginWithEmail(View view){
        String email = loginBinding.logInEmailEditText.getText().toString().trim();
        String password = loginBinding.logInPasswordEditText.getText().toString().trim();
        if(email.isEmpty() && password.isEmpty()){
            Log.i(LOGIN_FRAGMENT_TAG, "loginWithEmail: Email or Password were empty.");
        }else{
            final boolean[] returnValue = {false};
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(getActivity(), task -> {
                        if(task.isSuccessful()){
                            Log.i(LOGIN_FRAGMENT_TAG, "loginWithEmail: The user was logged in successfully.");
                            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_mainFragment);
                        }else{
                            Log.i(LOGIN_FRAGMENT_TAG, "loginWithEmail: The user is not logged in (Password or Email are false or no Internet)");
                        }
                    });
        }
    }

}