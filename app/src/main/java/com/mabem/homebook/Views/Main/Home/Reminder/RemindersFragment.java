package com.mabem.homebook.Views.Main.Home.Reminder;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mabem.homebook.Adapters.RemindersAdapter;
import com.mabem.homebook.Model.Objects.Home;
import com.mabem.homebook.Model.Objects.Member;
import com.mabem.homebook.Model.Objects.Reminder;
import com.mabem.homebook.R;
import com.mabem.homebook.Utils.NavigationDrawer;
import com.mabem.homebook.ViewModels.HomeViewModel;
import com.mabem.homebook.databinding.FragmentRemindersBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class RemindersFragment extends Fragment {
    private static final String REMINDERS_FRAGMENT_TAG = "Reminders Fragment";
    private static String home_id = "";
    private FragmentRemindersBinding fragmentRemindersBinding;
    private HomeViewModel homeViewModel;
    private RecyclerView.Adapter adapter;
    private Home currentHome;
    private Member currentMember;
    private ArrayList<Reminder> reminders = new ArrayList();
    private boolean isAdmin = false;

    public static String getHome_id() {
        return home_id;
    }

    public static void setHome_id(String home_id) {
        RemindersFragment.home_id = home_id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ((NavigationDrawer) getActivity()).disableNavDrawer();

        fragmentRemindersBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_reminders, container, false);

        fragmentRemindersBinding.myReminders.setHasFixedSize(true);
        fragmentRemindersBinding.myReminders.setLayoutManager(new LinearLayoutManager(getContext()));

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        homeViewModel.getCurrentMember().observe(getViewLifecycleOwner(), member -> {
            if (member != null) {
                currentMember = member;
                HashMap<Home, Boolean> memberHomes = member.getHome_role();
                for (Home h1 : memberHomes.keySet()) {
                    if (h1.getId().equals(home_id)) {
                        isAdmin = memberHomes.get(h1);
                        if (isAdmin) {
                            fragmentRemindersBinding.reminderAddButton.setVisibility(View.VISIBLE);
                        } else {
                            fragmentRemindersBinding.reminderAddButton.setVisibility(View.GONE);
                        }
                        homeViewModel.updateHomeWithReminders();

                        homeViewModel.getCurrentHome().observe(getViewLifecycleOwner(), h -> {

                            currentHome = h;

                            reminders.clear();

                            ArrayList<Reminder> r = h.getReminders();
                            for (Reminder reminder : r) {
                                reminders.add(reminder);
                            }
                            Collections.sort(reminders, new Comparator<Reminder>() {
                                @Override
                                public int compare(Reminder o1, Reminder o2) {
                                    return o1.getDate().compareTo(o2.getDate());
                                }
                            });

                            adapter = new RemindersAdapter(getContext(), reminders, isAdmin);
                            fragmentRemindersBinding.myReminders.setAdapter(adapter);
                        });
                    }
                }
            }
        });

        fragmentRemindersBinding.reminderAddButton.setOnClickListener(v -> {
            ReminderSetFragment.setToEditFlag(false);
            Navigation.findNavController(v).navigate(R.id.action_remindersFragment_to_reminderSetFragment);
        });


        return fragmentRemindersBinding.getRoot();
    }
}