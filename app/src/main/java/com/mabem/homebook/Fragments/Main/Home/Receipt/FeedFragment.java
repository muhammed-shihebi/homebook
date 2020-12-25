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
import com.mabem.homebook.Model.Home;
import com.mabem.homebook.Model.Member;
import com.mabem.homebook.Model.Receipt;
import com.mabem.homebook.R;
import com.mabem.homebook.ViewModels.HomeViewModel;
import com.mabem.homebook.databinding.FragmentFeedBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class FeedFragment extends Fragment {

    private static final String FEED_FRAGMENT_TAG = "Feed Fragment";

    private FragmentFeedBinding fragmentFeedBinding;
    private HomeViewModel homeViewModel;
    private ArrayList list = new ArrayList();
    private RecyclerView.Adapter adapter;
    private static String home_name = "";
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
                for(Home home : memberHomes.keySet()){
                    if( home.getName().trim().equals(home_name) ){

                        homeViewModel.updateCurrentHome(home.getId());

                        homeViewModel.getCurrentHome().observe(getViewLifecycleOwner(), h -> {

                            currentHome = h;

                            list.clear();
                            ArrayList<Receipt> receipts = h.getReceipts();
                            for(Receipt receipt : receipts){
                                list.add(receipt);
                            }
//                            Collections.sort(list);
                            adapter = new FeedAdapter(getContext(), list);
                            fragmentFeedBinding.myReceipts.setAdapter(adapter);
                         });
                    }
                }


            }
        });


        fragmentFeedBinding.addButton.setOnClickListener(v -> {
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
        return NavigationUI.onNavDestinationSelected(item, navController) || super.onOptionsItemSelected(item);
    }

    public static String getHome_name() {
        return home_name;
    }

    public static void setHome_name(String home_name2) {
        home_name = home_name2;
    }
}