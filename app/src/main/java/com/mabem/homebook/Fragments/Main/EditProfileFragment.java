package com.mabem.homebook.Fragments.Main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mabem.homebook.Model.Member;
import com.mabem.homebook.Model.User;
import com.mabem.homebook.R;
import com.mabem.homebook.ViewModels.HomeViewModel;
import com.mabem.homebook.databinding.FragmentEditProfileBinding;


import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

public class EditProfileFragment extends Fragment {

    private FragmentEditProfileBinding editProfileBinding;
    private HomeViewModel homeViewModel;
    private Member currentMember;
    private Uri imageUri;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        editProfileBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_profile, container, false);

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        homeViewModel.getCurrentMember().observe(getViewLifecycleOwner(), member -> {
            if (member != null) {
                currentMember = member;
                editProfileBinding.profileName.setText(member.getName());
                editProfileBinding.profileEmail.setText(member.getEmailAddress());
                Glide.with(this)
                        .load(member.getImageURI())
                        .into(editProfileBinding.profilePhoto);
            }
        });

        editProfileBinding.saveButton.setOnClickListener(v -> {
            String newName = editProfileBinding.profileName.getText().toString().trim();
            String newEmail = editProfileBinding.profileEmail.getText().toString().trim();
            if(newName.isEmpty()){
                Toast.makeText(requireContext(), R.string.profile_edit_enter_name, Toast.LENGTH_SHORT).show();
            }else if(newEmail.isEmpty()){
                Toast.makeText(requireContext(), R.string.profile_edit_enter_email, Toast.LENGTH_SHORT).show();
            }else{
                editProfileBinding.progressBar.setVisibility(View.VISIBLE);
                Member newMember = new Member(
                        currentMember.getId(),
                        newName,
                        newEmail,
                        currentMember.getImageURI(),
                        currentMember.getHome_role()
                        );

                homeViewModel.updateUser(newMember, imageUri);
            }

            homeViewModel.getResultMessage().observe(getViewLifecycleOwner(), message->{
                if(message != null){
                    Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
                    editProfileBinding.progressBar.setVisibility(View.GONE);
                    Navigation.findNavController(v).navigate(R.id.action_editProfileFragment_to_mainFragment);

                }
            });

        });

        editProfileBinding.changePhotoButton.setOnClickListener(v -> {
            if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},2000);
            } else {
                Intent cameraIntent = new Intent(Intent.ACTION_PICK);
                cameraIntent.setType("image/*");
                startActivityForResult(cameraIntent, 1001);
            }
        });

        return editProfileBinding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1001 && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            editProfileBinding.profilePhoto.setImageURI(imageUri);
            Log.w(TAG, "onActivityResult: "+ imageUri);
        }
    }
}