package com.mabem.homebook.register_fragment;

import android.content.Intent;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.mabem.homebook.R;
import com.mabem.homebook.databinding.RegisterFragmentBinding;

public class RegisterFragment extends Fragment {

    //========================================= Attributes

    private static final String REGISTER_FRAGMENT_TAG = "Register Fragment";
    private RegisterViewModel mViewModel;
    private RegisterFragmentBinding registerBinding;
    private FirebaseAuth firebaseAuth;
    private static final int RC_SIGN_IN = 385;
    GoogleSignInClient googleSignInClient;

    //========================================= Methods

    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        firebaseAuth = FirebaseAuth.getInstance();

        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        registerBinding = DataBindingUtil.inflate(inflater, R.layout.register_fragment, container, false);

        registerBinding.registerLogInButton.setOnClickListener((v) -> {
            Navigation.findNavController(v).navigate(R.id.action_registerFragment_to_loginFragment);
        });

        registerBinding.registerSignUpButton.setOnClickListener(v -> {
            registerWithEmail();
        });

        registerBinding.signUpWithGoogleButton.setOnClickListener(v -> {
            signUpWithGoogle();
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        return registerBinding.getRoot();
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
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
                    Log.d(REGISTER_FRAGMENT_TAG, "onActivityResult: Registering with Google was successful. Next, authenticate with Firebase");
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    firebaseAuthWithGoogle(account.getIdToken());
                } catch (ApiException e) {
                    Log.w(REGISTER_FRAGMENT_TAG, "onActivityResult: Log in to Google failed", e);
                }
            } else {
                Log.w(REGISTER_FRAGMENT_TAG, exception.toString());
            }
        }
    }

    //========================================= Help Functions
    private void registerWithEmail() {
        String email = registerBinding.registerEmailEditText.getText().toString().trim();
        String password = registerBinding.registerPasswordEditText.getText().toString();
        String password2 = registerBinding.passwordAgainEditText.getText().toString();
        String name = registerBinding.nameEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || password2.isEmpty() || name.isEmpty()) {
            Toast.makeText(requireActivity(), "Please fill out all the blanks.", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(password2)) {
            Toast.makeText(requireActivity(), "The given passwords don't match. ", Toast.LENGTH_SHORT).show();
        } else {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity(), task -> {
                        if (task.isSuccessful()) {
                            Log.d(REGISTER_FRAGMENT_TAG, "registerWithEmail: Sign in success. A new user is added");
                            Log.d(REGISTER_FRAGMENT_TAG, "registerWithEmail: userId: " + firebaseAuth.getCurrentUser().getUid());
                            Navigation.findNavController(requireActivity(), R.id.register_sign_up_button).navigate(R.id.action_registerFragment_to_mainFragment);
                        } else {
                            Log.w(REGISTER_FRAGMENT_TAG, "registerWithEmail: failure", task.getException());
                            Toast.makeText(requireActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void signUpWithGoogle() {
        // We have to sign out from the last client to make the user chose every time.
        googleSignInClient.signOut();
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(requireActivity(), task -> {
            if (task.isSuccessful()) {
                Log.d(REGISTER_FRAGMENT_TAG, "firebaseAuthWithGoogle: Authentication with firebase was successful.");
                Navigation.findNavController(requireActivity(), R.id.log_in_with_google_button).navigate(R.id.action_loginFragment_to_mainFragment);
            } else {
                Log.w(REGISTER_FRAGMENT_TAG, "signInWithCredential:failure", task.getException());
                Toast.makeText(requireActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }
}