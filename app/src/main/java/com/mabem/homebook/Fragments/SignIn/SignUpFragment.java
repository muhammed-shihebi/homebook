package com.mabem.homebook.Fragments.SignIn;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.mabem.homebook.R;
import com.mabem.homebook.Utils.NavigationDrawer;
import com.mabem.homebook.Utils.Util;
import com.mabem.homebook.ViewModels.AuthViewModel;
import com.mabem.homebook.databinding.SignUpFragmentBinding;

public class SignUpFragment extends Fragment {

    //========================================= Attributes

    private static final String REGISTER_FRAGMENT_TAG = "Register Fragment";
    private static final int RC_SIGN_IN = 385;
    private SignUpFragmentBinding signUpBinding;
    private AuthViewModel authViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        ((NavigationDrawer) getActivity()).disableNavDrawer();

        signUpBinding = DataBindingUtil.inflate(inflater, R.layout.sign_up_fragment, container, false);

        Util.saveRememberMePreference(requireActivity(), false);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        authViewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            Log.i("SignUp", "onCreateView: ");
            signUpBinding.progressBar2.setVisibility(View.GONE);
            if (user != null) {
                Navigation.findNavController(
                        requireActivity(),
                        R.id.sign_up_logo_image_view
                ).navigate(R.id.action_signUpFragment_to_mainFragment);
            }
        });

        authViewModel.getResultMessage().observe(getViewLifecycleOwner(), message->{
            if(message != null){
                Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
            }
        });

        signUpBinding.signUpSignUpButton.setOnClickListener(v -> {
            signUpWithEmail();
        });

        signUpBinding.signUpWithGoogleButton.setOnClickListener(v -> {
            Util.saveRememberMePreference(requireActivity(), true);
            signUpWithGoogle();
        });

        signUpBinding.signUpRememberCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Util.hideKeyboard(requireActivity());
            Util.saveRememberMePreference(requireActivity(), isChecked);
        });

        signUpBinding.signUpLogInButton.setOnClickListener((v) -> {
            Navigation.findNavController(v).navigate(R.id.action_signUpFragment_to_loginFragment);
        });

        return signUpBinding.getRoot();
    }

    //========================================= Log in with Google

    private void signUpWithGoogle() {

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

    private void signUpWithEmail() {
        String email = signUpBinding.signUpEmailEditText.getText().toString().trim();
        String password = signUpBinding.signUpPasswordEditText.getText().toString();
        String password2 = signUpBinding.passwordAgainEditText.getText().toString();
        String name = signUpBinding.nameEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || password2.isEmpty() || name.isEmpty()) {
            Toast.makeText(requireActivity(), R.string.please_fill_blanks_message, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(password2)) {
            Toast.makeText(requireActivity(), R.string.passwords_not_match_message, Toast.LENGTH_SHORT).show();
            return;
        }
        signUpBinding.progressBar2.setVisibility(View.VISIBLE);
        authViewModel.signUpWithEmail(email, password, name);
    }
}