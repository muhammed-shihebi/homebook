package com.mabem.homebook.Fragments.SignIn;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.mabem.homebook.Fragments.Main.EditProfileFragment;
import com.mabem.homebook.R;
import com.mabem.homebook.Utils.NavigationDrawer;
import com.mabem.homebook.Utils.Util;
import com.mabem.homebook.ViewModels.AuthViewModel;
import com.mabem.homebook.databinding.LoginFragmentBinding;

public class LoginFragment extends Fragment {

    //========================================= Attributes

    private static final String LOGIN_FRAGMENT_TAG = "Login Fragment";
    private static final int RC_SIGN_IN = 385;

    private AuthViewModel authViewModel;
    private LoginFragmentBinding loginBinding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //========================================= Init

        // Hide action bar.
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        ((NavigationDrawer) getActivity()).disableNavDrawer();

        // Block landscape orientation.
        // getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Inflate the layout (link this fragment with login_fragment layout).
        loginBinding = DataBindingUtil.inflate(inflater, R.layout.login_fragment, container, false);

        // To link the the view with the viewModel
        loginBinding.setLifecycleOwner(this);

        // Set the remember me preference to false as default.
        Util.saveRememberMePreference(requireActivity(), false);

        //========================================= Listeners

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        authViewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            EditProfileFragment.setAutomaticallyLoggedIn(false);
            loginBinding.progressBar.setVisibility(View.GONE);
            if (user != null) {
                Navigation.findNavController(
                        requireActivity(),
                        R.id.log_in_log_in_button
                ).navigate(R.id.action_loginFragment_to_mainFragment);
            }
        });

        authViewModel.getResultMessage().observe(getViewLifecycleOwner(), message->{
            if(message != null){
                Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
                loginBinding.progressBar.setVisibility(View.GONE);
            }
        });

        // CurrentUser will change according to the state of the currentUser in FirebaseAuth.
        // When a log in or sign up this this state will change and the following code will be executed.
        loginBinding.logInLogInButton.setOnClickListener(v -> {
            loginWithEmail();
        });

        loginBinding.logInWithGoogleButton.setOnClickListener(v -> {
            // If the user logged in with google
            Util.saveRememberMePreference(requireActivity(), true);
            loginWithGoogle();
        });

        // Setup SharedPreferences for the Remember me checkbox
        loginBinding.rememberCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Util.hideKeyboard(requireActivity());
            Util.saveRememberMePreference(requireActivity(), isChecked);
        });

        loginBinding.forgotPasswordButton.setOnClickListener(v -> {
            forgotPassword();
        });

        loginBinding.logInSignUpButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_signUpFragment);
        });

        return loginBinding.getRoot();
    }

    //========================================= Log in with Google

    private void loginWithGoogle() {

        // This code can't be delegated to the database because it requires an activity instance which must not be sent
        // to the viewModel or to the database

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        // We have to sign out from the last client to make the user chose every time.
        googleSignInClient.signOut();
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*
         * After the user chose his Google account and log in with it, his credentials will be sent back
         * as an activity result.
         * */

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            if (task.isSuccessful()) {
                try {
                    // Log in to Google was successful. Next, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    authViewModel.loginWithGoogle(account.getIdToken());
                } catch (ApiException e) {
                    Toast.makeText(requireActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    //========================================= Helper Functions

    private void forgotPassword() {
        String email = loginBinding.logInEmailEditText.getText().toString().trim();
        if (email.isEmpty()) {
            Toast.makeText(requireActivity(), R.string.please_enter_email_message, Toast.LENGTH_SHORT).show();
        } else {
            authViewModel.forgotPassword(email);
        }
    }

    private void loginWithEmail() {
        String email = loginBinding.logInEmailEditText.getText().toString().trim();
        String password = loginBinding.logInPasswordEditText.getText().toString();
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireActivity(), R.string.enter_email_password_message, Toast.LENGTH_SHORT).show();
        } else {
            loginBinding.progressBar.setVisibility(View.VISIBLE);
            authViewModel.loginWithEmail(email, password);
        }
    }
}
