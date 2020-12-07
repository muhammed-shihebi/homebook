package com.mabem.homebook.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.mabem.homebook.R;
import com.mabem.homebook.ViewModels.AuthViewModel;

public class SplashFragment extends Fragment {

    public static final int SPLASH_TIME = 1000; // 1 second

    private AuthViewModel authViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        new Handler().postDelayed(() -> {

            // Getting the network information to see if there is Internet connection.
            ConnectivityManager connMgr = (ConnectivityManager) getActivity()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {

                // If there is Internet, check if there is a logged in user.
                authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
                authViewModel.getCurrentUser().observe(requireActivity(), user -> {

                    // If there is a user, navigate to Welcome Screen
                    if (user != null) {
                        Navigation.findNavController(
                                requireActivity(),
                                R.id.splash_homebook_text_view
                        ).navigate(R.id.action_splashFragment_to_mainFragment);
                    }

                    // If there is no user, navigate to Login screen.
                    else {
                        Navigation.findNavController(
                                requireActivity(),
                                R.id.splash_homebook_text_view
                        ).navigate(R.id.action_splashFragment_to_loginFragment);
                    }
                });
            } else {

                // If there is no Internet, a popup window will show up and after that the program will end.
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                // set title
                alertDialogBuilder.setTitle(R.string.no_internet_message);
                // set dialog message
                alertDialogBuilder
                        .setMessage(R.string.please_connect_to_the_internet_message)
                        .setCancelable(false)
                        .setPositiveButton(R.string.ok_button, (dialog, id) -> {
                            // if this button is clicked, close the activity.
                            getActivity().finish();
                        });
                // create alert dialog.
                alertDialogBuilder.create().show();
            }
        }, SPLASH_TIME);

        return inflater.inflate(R.layout.fragment_splash, container, false);
    }
}