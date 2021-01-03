package com.mabem.homebook.Fragments.SignIn;

import android.app.AlertDialog;
import android.content.Context;
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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.mabem.homebook.R;
import com.mabem.homebook.Utils.NavigationDrawer;
import com.mabem.homebook.ViewModels.AuthViewModel;

public class SplashFragment extends Fragment {

    public static final int SPLASH_TIME = 1000; // 1 second

    private AuthViewModel authViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        ((NavigationDrawer) getActivity()).disableNavDrawer();
        new Handler().postDelayed(() -> {
            ConnectivityManager connMgr = (ConnectivityManager) getActivity()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
                authViewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
                    if (user != null) {
                        NavController navController = Navigation.findNavController(requireActivity(), R.id.splash_homebook_text_view);
                        navController.navigate(R.id.action_splashFragment_to_mainFragment);
                    } else {
                        NavController navController = Navigation.findNavController(requireActivity(), R.id.splash_homebook_text_view);
                        navController.navigate(R.id.action_splashFragment_to_loginFragment);
                    }
                });
            } else {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder.setTitle(R.string.no_internet_message);
                alertDialogBuilder
                        .setMessage(R.string.please_connect_to_the_internet_message)
                        .setCancelable(false)
                        .setPositiveButton(R.string.ok_button, (dialog, id) -> {
                            getActivity().finish();
                        });
                alertDialogBuilder.create().show();
            }
        }, SPLASH_TIME);
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }
}