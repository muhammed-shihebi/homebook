package com.mabem.homebook.main_fragment;

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

import com.google.firebase.auth.FirebaseAuth;
import com.mabem.homebook.R;
import com.mabem.homebook.databinding.MainFragmentBinding;

public class MainFragment extends Fragment {

    //========================================= Attribute

    private static final String MAIN_FRAGMENT_TAG = "Main Fragment";
    private MainViewModel mViewModel;
    private MainFragmentBinding mainBinding;
    private FirebaseAuth firebaseAuth;

    //========================================= Functions

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mainBinding = DataBindingUtil.inflate(inflater, R.layout.main_fragment, container, false);
        firebaseAuth = FirebaseAuth.getInstance();

        ((AppCompatActivity) getActivity()).getSupportActionBar().show();


        if (firebaseAuth.getCurrentUser().getDisplayName() == null) {
            Toast.makeText(requireActivity(), "DisplayName is null", Toast.LENGTH_SHORT).show();
            MainFragmentArgs args = MainFragmentArgs.fromBundle(getArguments());
            mainBinding.userName.setText(args.getUserName());
        } else {
            mainBinding.userName.setText(firebaseAuth.getCurrentUser().getDisplayName());
        }


        mainBinding.signOutButton.setOnClickListener(v -> {
            Log.i(MAIN_FRAGMENT_TAG, "onCreateView: Signing out the user... ");
            firebaseAuth.signOut();
            Navigation.findNavController(v).navigate(R.id.action_mainFragment_to_loginFragment);
        });
        return mainBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        // TODO: Use the ViewModel
    }

}