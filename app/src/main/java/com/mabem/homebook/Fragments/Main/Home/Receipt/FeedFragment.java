package com.mabem.homebook.Fragments.Main.Home.Receipt;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mabem.homebook.Adapters.FeedAdapter;
import com.mabem.homebook.Model.Home;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentFeedBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_feed, container, false);

        fragmentFeedBinding.myReceipts.setHasFixedSize(true);
        fragmentFeedBinding.myReceipts.setLayoutManager(new LinearLayoutManager(getContext()));

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        homeViewModel.updateCurrentMember();

        homeViewModel.getCurrentMember().observe(getViewLifecycleOwner(), member -> {
            if(member != null){
                HashMap<Home, Boolean> memberHomes = member.getHome_role();
                for(Home home : memberHomes.keySet()){
                    if( home.getName().trim().equals(home_name) ){
                        homeViewModel.updateCurrentHome(home.getId());
                        homeViewModel.getCurrentHome().observe(getViewLifecycleOwner(), h -> {
                            list.clear();
                            ArrayList<Receipt> receipts = h.getReceipts();
                            for(Receipt receipt : receipts){
                                list.add(receipt);
                            }
                            Collections.sort(list);
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


        return fragmentFeedBinding.getRoot();
    }

    public static String getHome_name() {
        return home_name;
    }

    public static void setHome_name(String home_name2) {
        home_name = home_name2;
    }
}