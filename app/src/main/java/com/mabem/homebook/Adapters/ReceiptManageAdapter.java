package com.mabem.homebook.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mabem.homebook.Model.Item;
import com.mabem.homebook.R;
import com.mabem.homebook.Utils.ReceiptManagerItemListener;

import java.util.ArrayList;

public class ReceiptManageAdapter extends RecyclerView.Adapter<ReceiptManageAdapter.ViewHolder> {

    private final Context context;
    ReceiptManagerItemListener receiptManagerItemListener;
    ArrayList<Item> items;


    public ReceiptManageAdapter(Context context, ArrayList<Item> items, ReceiptManagerItemListener receiptManagerItemListener) {
        this.context = context;
        this.items = items;
        this.receiptManagerItemListener = receiptManagerItemListener;
    }

    @NonNull
    @Override
    public ReceiptManageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.receipt_manage_menu_item, parent, false);
        return new ReceiptManageAdapter.ViewHolder(v, receiptManagerItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ReceiptManageAdapter.ViewHolder holder, int position) {
        holder.populate(items.get(position));
    }


    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView itemName;
        private TextView itemPrice;
        private ImageView delete;
        private ReceiptManagerItemListener deleteListener;

        public ViewHolder(@NonNull View itemView, ReceiptManagerItemListener deleteListener) {
            super(itemView);
            itemName = itemView.findViewById(R.id.receipt_manage_itemlist_name);
            itemPrice = itemView.findViewById(R.id.receipt_manage_itemlist_price);
            delete = itemView.findViewById(R.id.receipt_manage_itemlist_delete);
            this.deleteListener = deleteListener;

        }


        public void populate(Item item) {
            itemName.setText(item.getName());
            itemPrice.setText(item.getPrice().toString());

            delete.setOnClickListener(v -> {
                deleteListener.onDeleteClicked(item, getAdapterPosition());
            });
        }
    }

}
