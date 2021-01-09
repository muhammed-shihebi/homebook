package com.mabem.homebook.Views.Main.Home;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mabem.homebook.Adapters.EditHomeAdapter;
import com.mabem.homebook.Model.Home;
import com.mabem.homebook.Model.Member;
import com.mabem.homebook.R;
import com.mabem.homebook.Utils.EditHomeMemberListener;
import com.mabem.homebook.Utils.NavigationDrawer;
import com.mabem.homebook.ViewModels.HomeViewModel;
import com.mabem.homebook.databinding.FragmentEditHomeBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class EditHomeFragment extends Fragment implements EditHomeMemberListener {

    private static final String EDIT_HOME_FRAGMENT_TAG = "Edit Home Fragment";

    private FragmentEditHomeBinding fragmentEditHomeBinding;
    private HomeViewModel homeViewModel;
    private RecyclerView.Adapter adapter;
    private HashMap<Member, Boolean> members_role = new HashMap<Member, Boolean>();
    private ArrayList<Member> allMembers;
    private boolean isPrivate = false;
    private Home currentHome;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ((NavigationDrawer) getActivity()).disableNavDrawer();

        fragmentEditHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_home, container, false);

        fragmentEditHomeBinding.homeEditMembersList.setHasFixedSize(true);
        fragmentEditHomeBinding.homeEditMembersList.setLayoutManager(new LinearLayoutManager(getContext()));

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        homeViewModel.updateHomeWithMembers();
        homeViewModel.getCurrentHome().observe(getViewLifecycleOwner(), h -> {
            if (h != null) {
                currentHome = h;
                isPrivate = !h.getVisibility();

                fragmentEditHomeBinding.privateCheckbox.setChecked(isPrivate);
                fragmentEditHomeBinding.homeEditName.setText(h.getName());
                fragmentEditHomeBinding.homeEditCode.setText(h.getCode());

                members_role.clear();
                members_role = h.getMember_role();

                ArrayList<Member> admins = new ArrayList<Member>();
                ArrayList<Member> normalmembers = new ArrayList<Member>();
                allMembers = new ArrayList<>();
                for (Member m : members_role.keySet()) {
                    allMembers.add(m);
                }

                for (int i = 0; i < allMembers.size(); i++) {
                    if (members_role.get(allMembers.get(i)).equals(true)) {
                        admins.add(allMembers.get(i));
                    } else if (members_role.get(allMembers.get(i)).equals(false)) {
                        normalmembers.add(allMembers.get(i));
                    }
                }

                Collections.sort(admins, new Comparator<Member>() {
                    @Override
                    public int compare(Member o1, Member o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                });
                Collections.sort(normalmembers, new Comparator<Member>() {
                    @Override
                    public int compare(Member o1, Member o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                });

                allMembers.clear();
                allMembers.addAll(admins);
                allMembers.addAll(normalmembers);

                adapter = new EditHomeAdapter(getContext(), allMembers, admins.size(), this);
                fragmentEditHomeBinding.homeEditMembersList.setAdapter(adapter);
            }
        });

        fragmentEditHomeBinding.privateCheckbox.setOnClickListener(v -> {
            isPrivate = !isPrivate;
        });

        fragmentEditHomeBinding.homeEditSaveButton.setOnClickListener(v -> {
            String name = fragmentEditHomeBinding.homeEditName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(requireContext(), R.string.please_enter_name_for_home_message, Toast.LENGTH_SHORT).show();
            } else {
                Home h = new Home(currentHome.getId(), name, currentHome.getCode(), !isPrivate, currentHome.getReceipts());
                h.setMember_role(members_role);
                homeViewModel.updateHome(h);
                homeViewModel.getResultMessage().observe(getViewLifecycleOwner(), s -> {
                    if (s != null) {
                        Toast.makeText(requireContext(), s, Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(v).navigate(R.id.action_editHomeFragment_to_mainFragment);
                    }
                });

            }
        });

        fragmentEditHomeBinding.homeEditDeleteButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.delete_home_warning)
                    .setMessage(R.string.delete_home_warning_message)
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            homeViewModel.deleteHome();
                            homeViewModel.getResultMessage().observe(getViewLifecycleOwner(), s -> {
                                if (s != null) {
                                    Toast.makeText(requireContext(), homeViewModel.getResultMessage().getValue(), Toast.LENGTH_SHORT).show();
                                    Navigation.findNavController(v).navigate(R.id.action_editHomeFragment_to_mainFragment);
                                }
                            });
                        }
                    });
            AlertDialog mDialog = builder.create();
            mDialog.show();
        });

        fragmentEditHomeBinding.homeEditLeaveButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.leave_home_warning)
                    .setMessage(R.string.leave_home_warning_message)
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            homeViewModel.leaveHome();
                            homeViewModel.getResultMessage().observe(getViewLifecycleOwner(), s -> {
                                if (s != null) {
                                    Toast.makeText(requireContext(), homeViewModel.getResultMessage().getValue(), Toast.LENGTH_SHORT).show();
                                    Navigation.findNavController(v).navigate(R.id.action_editHomeFragment_to_mainFragment);
                                }
                            });
                        }
                    });
            AlertDialog mDialog = builder.create();
            mDialog.show();
        });

        fragmentEditHomeBinding.homeEditShareButton.setOnClickListener(v -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.code_of) + " " + currentHome.getName() + ": " + currentHome.getCode());
            sendIntent.setType("text/plain");
            try {
                startActivity(sendIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getContext(), R.string.no_text_sharing_app, Toast.LENGTH_SHORT).show();
            }
        });

        return fragmentEditHomeBinding.getRoot();
    }

    @Override
    public void onDeleteClicked(Member member, int position) {
        if (allMembers.size() > 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.remove_member_warning)
                    .setMessage(R.string.remove_member_warning_message)
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            members_role.remove(member);
                            allMembers.remove(member);
                            adapter.notifyItemRemoved(position);
                        }
                    });
            AlertDialog mDialog = builder.create();
            mDialog.show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.warning)
                    .setMessage(R.string.remove_last_member_warning_message)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            AlertDialog mDialog = builder.create();
            mDialog.show();
        }
    }

    @Override
    public void onAdminSwitchClicked(Member member, boolean warning) {
        if (!warning) {
            for (Member m : members_role.keySet()) {
                if (m.equals(member)) {
                    boolean oldRole = members_role.get(m);
                    members_role.put(m, !oldRole);
                }
            }

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.warning)
                    .setMessage(R.string.remove_last_admin_warning_message)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            AlertDialog mDialog = builder.create();
            mDialog.show();
        }
    }

    @Override
    public void onDeletingLastAdmin() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.warning)
                .setMessage(R.string.delete_last_admin_warning_message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        AlertDialog mDialog = builder.create();
        mDialog.show();
    }

}