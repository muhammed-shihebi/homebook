package com.mabem.homebook.Fragments.Main.Home.Receipt;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.mabem.homebook.Adapters.FeedAdapter;
import com.mabem.homebook.Fragments.Main.Home.Reminder.RemindersFragment;
import com.mabem.homebook.Model.Home;
import com.mabem.homebook.Model.Member;
import com.mabem.homebook.Model.Receipt;
import com.mabem.homebook.R;
import com.mabem.homebook.ViewModels.HomeViewModel;
import com.mabem.homebook.databinding.FragmentFeedBinding;
import com.mabem.homebook.databinding.FragmentManageReceiptBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class FeedFragment extends Fragment {

    private static final String FEED_FRAGMENT_TAG = "Feed Fragment";

    private FragmentFeedBinding fragmentFeedBinding;
    private HomeViewModel homeViewModel;
    private ArrayList list = new ArrayList();
    private RecyclerView.Adapter adapter;
    private static String home_id = "";
    private NavController navController;
    private Home currentHome;
    private Member currentMember;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentFeedBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_feed, container, false);

        fragmentFeedBinding.myReceipts.setHasFixedSize(true);
        fragmentFeedBinding.myReceipts.setLayoutManager(new LinearLayoutManager(getContext()));

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        homeViewModel.getCurrentMember().observe(getViewLifecycleOwner(), member -> {
            if(member != null){
                currentMember = member;
                HashMap<Home, Boolean> memberHomes = member.getHome_role();
                for(Home h1 : memberHomes.keySet()){
                    if( h1.getId().equals(home_id) ){
                        boolean isAdmin = memberHomes.get(h1);

                        homeViewModel.updateCurrentHome(h1.getId());

                        homeViewModel.getCurrentHome().observe(getViewLifecycleOwner(), h -> {
                            if(h != null){
                                currentHome = h;

                                list.clear();
                                ArrayList<Receipt> receipts = h.getReceipts();
                                for(Receipt receipt : receipts){
                                    list.add(receipt);
                                }
                                Collections.sort(list, new Comparator<Receipt>() {
                                    @Override
                                    public int compare(Receipt o1, Receipt o2) {
                                        return o2.getDate().compareTo(o1.getDate());
                                    }
                                });

                                adapter = new FeedAdapter(getContext(), list, isAdmin, member.getId());
                                fragmentFeedBinding.myReceipts.setAdapter(adapter);
                            }
                         });
                    }
                }


            }
        });


        fragmentFeedBinding.addButton.setOnClickListener(v -> {
            ReceiptManageFragment.setToEditFlag(false);
            Navigation.findNavController(v).navigate(R.id.action_feedFragment_to_addReceiptFragment);
        });

        setHasOptionsMenu(true);
        return fragmentFeedBinding.getRoot();
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.overflow_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.homeInfoFragment){
            if(currentMember.isThisMemberAdmin(currentHome)){
                navController.navigate(R.id.editHomeFragment);
                return true;
            }
        }
        if(item.getItemId() == R.id.remindersFragment){
            RemindersFragment.setHome_id(home_id);
        }
        return NavigationUI.onNavDestinationSelected(item, navController) || super.onOptionsItemSelected(item);
    }

    public static String getHome_id() {
        return home_id;
    }

    public static void setHome_id(String home_id2) {
        home_id = home_id2;
    }
}