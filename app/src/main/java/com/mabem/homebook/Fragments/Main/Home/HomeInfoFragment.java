package com.mabem.homebook.Fragments.Main.Home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mabem.homebook.Adapters.HomeInfoAdapter;
import com.mabem.homebook.Model.Home;
import com.mabem.homebook.Model.Member;
import com.mabem.homebook.R;
import com.mabem.homebook.ViewModels.HomeViewModel;
import com.mabem.homebook.databinding.FragmentHomeInfoBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class HomeInfoFragment extends Fragment {
    private static final String HOME_INFO_FRAGMENT_TAG = "Home Info Fragment";

    private FragmentHomeInfoBinding fragmentHomeInfoBinding;
    private HomeViewModel homeViewModel;
    private HashMap<Member,Boolean> members_role = new HashMap<Member,Boolean>();
    private RecyclerView.Adapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentHomeInfoBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home_info, container, false);

        fragmentHomeInfoBinding.homeinfoMemberlist.setHasFixedSize(true);
        fragmentHomeInfoBinding.homeinfoMemberlist.setLayoutManager(new LinearLayoutManager(getContext()));

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        homeViewModel.updateHomeWithMembers();
        homeViewModel.getCurrentHome().observe(getViewLifecycleOwner(), h -> {

            fragmentHomeInfoBinding.homeinfoName.setText(h.getName());
            fragmentHomeInfoBinding.homeinfoCode.setText(h.getCode());

            members_role.clear();
            members_role = h.getMember_role();

            Log.d("demo1", members_role.isEmpty()+"");

            ArrayList<Member> admins = new ArrayList<Member>();
            ArrayList<Member> normalmembers = new ArrayList<Member>();
            ArrayList<Member> allMembers = new ArrayList<>();
            for(Member m : members_role.keySet()){
                allMembers.add(m);
            }

            for(int i = 0; i < allMembers.size(); i++){
                if(members_role.get(allMembers.get(i)).equals(true)){
                    admins.add(allMembers.get(i));
                }else if(members_role.get(allMembers.get(i)).equals(false)){
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

            adapter = new HomeInfoAdapter(getContext(), admins, normalmembers, members_role.size());
            fragmentHomeInfoBinding.homeinfoMemberlist.setAdapter(adapter);
        });


        fragmentHomeInfoBinding.homeinfoLeaveButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.leave_home_warning)
                    .setMessage(R.string.leave_home_warning_message)
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}
                    })
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            homeViewModel.leaveHome();
                            homeViewModel.getResultMessage().observe(getViewLifecycleOwner(), s -> {
                                Toast.makeText(requireContext(), homeViewModel.getResultMessage().getValue(), Toast.LENGTH_SHORT).show();
                                Navigation.findNavController(v).navigate(R.id.action_homeInfoFragment_to_mainFragment);
                            });
                        }
                    });
            AlertDialog mDialog = builder.create();
            mDialog.show();
        });

        return fragmentHomeInfoBinding.getRoot();
    }


}