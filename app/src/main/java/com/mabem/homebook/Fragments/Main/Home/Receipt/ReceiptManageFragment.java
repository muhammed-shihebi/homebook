package com.mabem.homebook.Fragments.Main.Home.Receipt;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.DatePicker;
import android.widget.Toast;

import com.mabem.homebook.Adapters.MyhomesAdapter;
import com.mabem.homebook.Adapters.ReceiptManageAdapter;
import com.mabem.homebook.Adapters.ReceiptinfoAdapter;
import com.mabem.homebook.Model.Home;
import com.mabem.homebook.Model.Item;
import com.mabem.homebook.Model.Member;
import com.mabem.homebook.Model.Receipt;
import com.mabem.homebook.R;
import com.mabem.homebook.Utils.NavigationDrawer;
import com.mabem.homebook.Utils.ReceiptManagerItemListener;
import com.mabem.homebook.Utils.Util;
import com.mabem.homebook.ViewModels.HomeViewModel;
import com.mabem.homebook.databinding.FragmentManageReceiptBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

public class ReceiptManageFragment extends Fragment implements ReceiptManagerItemListener {

    private static final String RECEIPT_MANAGE_FRAGMENT_TAG = "Receipt Manage Fragment";


    private FragmentManageReceiptBinding fragmentManageReceiptBinding;
    private HomeViewModel homeViewModel;
    private RecyclerView.Adapter adapter;
    private Member currentMember;

    private Calendar cal;
    private String receiptName = "";
    private Double total = 0.0;
    private ArrayList<Item> itemsOfReceipt = new ArrayList<>();


    private static boolean toEditFlag = false; //Admin or the member, who created this receipt, accesses a receipt
    private static Receipt toEditReceipt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentManageReceiptBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_manage_receipt, container, false);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        fragmentManageReceiptBinding.receiptManageItemList.setHasFixedSize(true);
        fragmentManageReceiptBinding.receiptManageItemList.setLayoutManager(new LinearLayoutManager(getContext()));

        homeViewModel.getCurrentMember().observe(getViewLifecycleOwner(), member -> {
            if (member != null) {
                currentMember = member;
            }
        });

        if(toEditFlag){
            fragmentManageReceiptBinding.receiptManageReceiptName.setText(toEditReceipt.getName());
            Calendar cal = Calendar.getInstance();
            cal.setTime(toEditReceipt.getDate());
            Log.d("Demo", " At beginning"+cal.getTime().toString());
            fragmentManageReceiptBinding.receiptManageDateButton.setText(cal.get(Calendar.DAY_OF_MONTH)+"/"+(cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.YEAR));
            total = toEditReceipt.getTotal();
            fragmentManageReceiptBinding.receiptManageTotal.setText(toEditReceipt.getTotal().toString());

            homeViewModel.updateCurrentReceipt(toEditReceipt.getId());
            homeViewModel.getCurrentReceipt().observe(getViewLifecycleOwner(), r -> {
                itemsOfReceipt.clear();
                ArrayList<Item> i = r.getItems();
                for(Item item : i){
                    itemsOfReceipt.add(item);
                }
                adapter = new ReceiptManageAdapter(getContext(), itemsOfReceipt, this);
                fragmentManageReceiptBinding.receiptManageItemList.setAdapter(adapter);
            });

        }else{
            itemsOfReceipt.clear();
            adapter = new ReceiptManageAdapter(getContext(), itemsOfReceipt, this);
            fragmentManageReceiptBinding.receiptManageItemList.setAdapter(adapter);
        }

        fragmentManageReceiptBinding.receiptManageDateButton.setOnClickListener(v -> {
            cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    fragmentManageReceiptBinding.receiptManageDateButton.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                    cal.set(year, month, dayOfMonth);

                    Log.d("Demo", cal.getTime().toString());
                }
            }, year, month, day);

            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.show();
        });

        fragmentManageReceiptBinding.receiptManageAddItemButton.setOnClickListener(v -> {
            String name = fragmentManageReceiptBinding.receiptManageItemName.getText().toString().trim();
            String price = fragmentManageReceiptBinding.receiptManageItemPrice.getText().toString().trim();
            if(name != null && price != null) {
                Double d = Double.parseDouble(price);
                Item i = new Item(name, d);
                onAddingItem(i);
            }

        });

        fragmentManageReceiptBinding.receiptManageSaveButton.setOnClickListener(v -> {
            if(isToEditFlag()){
                if(!fragmentManageReceiptBinding.receiptManageReceiptName.getText().toString().trim().isEmpty() && !itemsOfReceipt.isEmpty()){
                    receiptName = fragmentManageReceiptBinding.receiptManageReceiptName.getText().toString().trim();
                    Receipt updated = new Receipt(toEditReceipt.getId(), receiptName, cal.getTime(), total, toEditReceipt.getMemberName(), toEditReceipt.getMemberId());
                    updated.setItems(itemsOfReceipt);
                    homeViewModel.updateReceipt(updated);
                    homeViewModel.getResultMessage().observe(getViewLifecycleOwner(), s -> {
                        Toast.makeText(requireContext(), homeViewModel.getResultMessage().getValue(), Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(v).navigate(R.id.action_manageReceiptFragment_to_feedFragment);
                    });

                }else{
                    if(fragmentManageReceiptBinding.receiptManageReceiptName.getText().toString().trim().isEmpty()){
                        Toast.makeText(requireContext(), R.string.please_enter_name_for_receipt_message, Toast.LENGTH_SHORT).show();
                    }else if(itemsOfReceipt.isEmpty()){
                        Toast.makeText(requireContext(), R.string.please_enter_item_message, Toast.LENGTH_SHORT).show();
                    }
                }
            }else{
                if(!fragmentManageReceiptBinding.receiptManageReceiptName.getText().toString().trim().isEmpty() && !itemsOfReceipt.isEmpty() && (cal != null) ){
                    receiptName = fragmentManageReceiptBinding.receiptManageReceiptName.getText().toString().trim();
                    Receipt r = new Receipt("", receiptName, cal.getTime(), total, currentMember.getName(), currentMember.getId());
                    r.setItems(itemsOfReceipt);
                    homeViewModel.addReceipt(r);
                    homeViewModel.getResultMessage().observe(getViewLifecycleOwner(), s -> {
                        Toast.makeText(requireContext(), homeViewModel.getResultMessage().getValue(), Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(v).navigate(R.id.action_manageReceiptFragment_to_feedFragment);
                    });

                }else{
                    if(fragmentManageReceiptBinding.receiptManageReceiptName.getText().toString().trim().isEmpty()){
                        Toast.makeText(requireContext(), R.string.please_enter_name_for_receipt_message, Toast.LENGTH_SHORT).show();
                    }else if(itemsOfReceipt.isEmpty()){
                        Toast.makeText(requireContext(), R.string.please_enter_item_message, Toast.LENGTH_SHORT).show();
                    }else if(cal == null){
                        Toast.makeText(requireContext(), R.string.please_enter_date_of_receipt_message, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        fragmentManageReceiptBinding.receiptManageDeleteButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.delete_receipt_warning)
                    .setMessage(R.string.delete_receipt_warning_message)
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}
                    })
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            homeViewModel.deleteReceipt(toEditReceipt.getId());
                            homeViewModel.getResultMessage().observe(getViewLifecycleOwner(), s -> {
                                Toast.makeText(requireContext(), homeViewModel.getResultMessage().getValue(), Toast.LENGTH_SHORT).show();
                                Navigation.findNavController(v).navigate(R.id.action_manageReceiptFragment_to_feedFragment);
                            });
                        }
                    });
            AlertDialog mDialog = builder.create();
            mDialog.show();
        });




        return fragmentManageReceiptBinding.getRoot();
    }

    private void onAddingItem(Item i) {
        total += i.getPrice();
        int position = itemsOfReceipt.size();
        fragmentManageReceiptBinding.receiptManageTotal.setText(total.toString());
        itemsOfReceipt.add(position, i);
        adapter.notifyItemInserted(position);
        fragmentManageReceiptBinding.receiptManageItemName.setText("");
        fragmentManageReceiptBinding.receiptManageItemPrice.setText("");
        Util.hideKeyboard(getActivity());
    }


    public static Receipt getToEditReceipt() {
        return toEditReceipt;
    }

    public static void setToEditReceipt(Receipt toEditReceipt) {
        ReceiptManageFragment.toEditReceipt = toEditReceipt;
    }

    public static boolean isToEditFlag() {
        return toEditFlag;
    }

    public static void setToEditFlag(boolean toEditFlag) {
        ReceiptManageFragment.toEditFlag = toEditFlag;
    }


    @Override
    public void onDeleteClicked(Item item, int position) {
        total = total - itemsOfReceipt.get(position).getPrice();
        fragmentManageReceiptBinding.receiptManageTotal.setText(total.toString());
        itemsOfReceipt.remove(position);
        adapter.notifyItemRemoved(position);
    }
}