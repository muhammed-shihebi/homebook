package com.mabem.homebook.Fragments.Main.Home.Receipt;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mabem.homebook.Adapters.FeedAdapter;
import com.mabem.homebook.Adapters.ReceiptinfoAdapter;
import com.mabem.homebook.Model.Item;
import com.mabem.homebook.Model.Receipt;
import com.mabem.homebook.R;
import com.mabem.homebook.ViewModels.HomeViewModel;
import com.mabem.homebook.databinding.FragmentFeedBinding;
import com.mabem.homebook.databinding.FragmentReceiptInfoBinding;

import java.util.ArrayList;
import java.util.Collections;


public class ReceiptInfoFragment extends Fragment {

    private static final String RECEIPT_INFO_FRAGMENT_TAG = "Receipt Info Fragment";

    private FragmentReceiptInfoBinding fragmentReceiptInfoBinding;
    private HomeViewModel homeViewModel;
    private ArrayList items = new ArrayList();
    private RecyclerView.Adapter adapter;
    private static Receipt currentReceipt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentReceiptInfoBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_receipt_info, container, false);

        fragmentReceiptInfoBinding.receiptinfoItemsList.setHasFixedSize(true);
        fragmentReceiptInfoBinding.receiptinfoItemsList.setLayoutManager(new LinearLayoutManager(getContext()));

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        fragmentReceiptInfoBinding.receiptinfoMembername.setText(currentReceipt.getMemberName());
        fragmentReceiptInfoBinding.receiptinfoTotal.setText(currentReceipt.getTotal());
        //fragmentReceiptInfoBinding.receiptinfoDate.setText(currentReceipt.getDate().toString());
        //fragmentReceiptInfoBinding.receiptinfoMemberphoto.setImageURI();

        homeViewModel.getCurrentHome().observe(getViewLifecycleOwner(), h -> {
            ArrayList<Receipt> receipts = h.getReceipts();
            for(Receipt receipt : receipts){
                if(receipt.equals(currentReceipt)){

                    homeViewModel.updateCurrentReceipt(receipt.getId());
                    homeViewModel.getCurrentReceipt().observe(getViewLifecycleOwner(), r -> {
                        items.clear();
                        ArrayList<Item> i = r.getItems();
                        for(Item item : i){
                            items.add(item);
                        }
                        adapter = new ReceiptinfoAdapter(getContext(), items);
                        fragmentReceiptInfoBinding.receiptinfoItemsList.setAdapter(adapter);


                    });
                }
            }
        });

        return fragmentReceiptInfoBinding.getRoot();

    }

    public static Receipt getReceipt() {
        return currentReceipt;
    }

    public static void setReceipt(Receipt receipt) {
        ReceiptInfoFragment.currentReceipt = receipt;
    }
}