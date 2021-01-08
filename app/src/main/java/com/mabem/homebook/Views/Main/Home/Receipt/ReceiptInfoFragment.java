package com.mabem.homebook.Views.Main.Home.Receipt;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mabem.homebook.Adapters.ReceiptinfoAdapter;
import com.mabem.homebook.Model.Objects.Item;
import com.mabem.homebook.Model.Objects.Receipt;
import com.mabem.homebook.R;
import com.mabem.homebook.Utils.NavigationDrawer;
import com.mabem.homebook.ViewModels.HomeViewModel;
import com.mabem.homebook.databinding.FragmentReceiptInfoBinding;

import java.text.DateFormat;
import java.util.ArrayList;


public class ReceiptInfoFragment extends Fragment {

    private static final String RECEIPT_INFO_FRAGMENT_TAG = "Receipt Info Fragment";
    private static Receipt currentReceipt;
    private FragmentReceiptInfoBinding fragmentReceiptInfoBinding;
    private HomeViewModel homeViewModel;
    private ArrayList items = new ArrayList();
    private RecyclerView.Adapter adapter;

    public static Receipt getReceipt() {
        return currentReceipt;
    }

    public static void setReceipt(Receipt receipt) {
        ReceiptInfoFragment.currentReceipt = receipt;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ((NavigationDrawer) getActivity()).disableNavDrawer();

        fragmentReceiptInfoBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_receipt_info, container, false);

        fragmentReceiptInfoBinding.receiptinfoItemsList.setHasFixedSize(true);
        fragmentReceiptInfoBinding.receiptinfoItemsList.setLayoutManager(new LinearLayoutManager(getContext()));

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        DateFormat sdf = DateFormat.getDateInstance();

        fragmentReceiptInfoBinding.receiptinfoMembername.setText(currentReceipt.getMemberName());
        fragmentReceiptInfoBinding.receiptinfoTotal.setText(currentReceipt.getTotal().toString());
        fragmentReceiptInfoBinding.receiptinfoDate.setText(sdf.format(currentReceipt.getDate().getTime()));

        homeViewModel.getCurrentHome().observe(getViewLifecycleOwner(), h -> {
            if (h != null) {
                ArrayList<Receipt> receipts = h.getReceipts();
                for (Receipt receipt : receipts) {
                    if (receipt.equals(currentReceipt)) {
                        homeViewModel.updateCurrentReceipt(receipt.getId());
                        homeViewModel.getCurrentReceipt().observe(getViewLifecycleOwner(), r -> {
                            if (r != null) {
                                items.clear();
                                ArrayList<Item> i = r.getItems();
                                for (Item item : i) {
                                    items.add(item);
                                }
                                adapter = new ReceiptinfoAdapter(getContext(), items);
                                fragmentReceiptInfoBinding.receiptinfoItemsList.setAdapter(adapter);
                            }
                        });
                    }
                }
            }
        });

        return fragmentReceiptInfoBinding.getRoot();

    }
}