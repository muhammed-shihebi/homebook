package com.mabem.homebook.login_fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.mabem.homebook.R;
import com.mabem.homebook.databinding.LoginFragmentBinding;

public class LoginFragment extends Fragment {

    //========================================= Attributes

    private static final String LOGIN_FRAGMENT_TAG = "Login Fragment";
    private static final int RC_SIGN_IN = 385;
    GoogleSignInClient googleSignInClient;
    private LoginViewModel mViewModel;
    private LoginFragmentBinding loginBinding;
    private FirebaseAuth firebaseAuth;

    //========================================= Methods

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    //========================================= Overriding

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

        loginBinding.logInWithGoogleButton.setOnClickListener(v -> {
            loginWithGoogle();
        });

        loginBinding.forgotPasswordButton.setOnClickListener(v -> {
            forgotPassword();
        });


        // Initialize Google sign in client
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        // Setup SharedPreferences for the Remember me checkbox
        loginBinding.rememberCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(getString(R.string.saved_remember_me_preference), isChecked);
            editor.apply();
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
        if (currentUser != null) {
            Log.d(LOGIN_FRAGMENT_TAG, "onViewCreated: There is a user logged in; " + currentUser.getEmail());
            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_mainFragment);
        } else {
            Log.d(LOGIN_FRAGMENT_TAG, "onViewCreated: There is no user logged in. Start normal. ");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            Exception exception = task.getException();
            if (task.isSuccessful()) {
                try {
                    Log.d(LOGIN_FRAGMENT_TAG, "onActivityResult: Log in to Google was successful. Next, authenticate with Firebase");
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    firebaseAuthWithGoogle(account.getIdToken());
                } catch (ApiException e) {
                    Log.w(LOGIN_FRAGMENT_TAG, "onActivityResult: Log in to Google failed", e);
                }
            } else {
                Log.w(LOGIN_FRAGMENT_TAG, exception.toString());
            }
        }
    }

    //========================================= Help Methods

    public void loginWithEmail(View view) {
        loginBinding.logInProgressBar.setVisibility(View.VISIBLE);
        String email = loginBinding.logInEmailEditText.getText().toString().trim();
        String password = loginBinding.logInPasswordEditText.getText().toString();
        if (email.isEmpty() || password.isEmpty()) {
            loginBinding.logInProgressBar.setVisibility(View.GONE);
            Log.i(LOGIN_FRAGMENT_TAG, "loginWithEmail: Email or Password were empty.");
            Toast.makeText(requireActivity(), "Please enter a Password and an Email.", Toast.LENGTH_SHORT).show();
        } else {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity(), task -> {
                        if (task.isSuccessful()) {
                            Log.i(LOGIN_FRAGMENT_TAG, "loginWithEmail: The user was logged in successfully.");
                            Log.d(LOGIN_FRAGMENT_TAG, "registerWithEmail: userId: " + firebaseAuth.getCurrentUser().getUid());
                            loginBinding.logInProgressBar.setVisibility(View.GONE);
                            Navigation.findNavController(view)
                                    .navigate(LoginFragmentDirections
                                            .actionLoginFragmentToMainFragment(firebaseAuth.getCurrentUser().getDisplayName()));
                        } else {
                            // The user is not logged in (Password or Email are false or no Internet).
                            loginBinding.logInProgressBar.setVisibility(View.GONE);
                            Log.w(LOGIN_FRAGMENT_TAG, "loginWithEmail: ");
                            Toast.makeText(requireActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void loginWithGoogle() {
        // We have to sign out from the last client to make the user chose every time.
        googleSignInClient.signOut();
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(requireActivity(), task -> {
            if (task.isSuccessful()) {
                Log.d(LOGIN_FRAGMENT_TAG, "firebaseAuthWithGoogle: Authentication with firebase was successful.");
                Navigation.findNavController(requireActivity(), R.id.log_in_with_google_button).navigate(R.id.action_loginFragment_to_mainFragment);
            } else {
                Log.w(LOGIN_FRAGMENT_TAG, "signInWithCredential: failure", task.getException());
                Toast.makeText(requireActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void forgotPassword() {
        String email = loginBinding.logInEmailEditText.getText().toString().trim();
        System.out.println(email);
        if(email.isEmpty()){
            Toast.makeText(requireActivity(), "Please enter you Email first", Toast.LENGTH_SHORT).show();
        }else{
            firebaseAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(LOGIN_FRAGMENT_TAG, "Email sent.");
                            Toast.makeText(requireActivity(), "An Email was sent to you to reset your password.", Toast.LENGTH_SHORT).show();
                        }else {
                            Log.w(LOGIN_FRAGMENT_TAG, "Unable to send email to reset password.");
                            Toast.makeText(requireActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

}