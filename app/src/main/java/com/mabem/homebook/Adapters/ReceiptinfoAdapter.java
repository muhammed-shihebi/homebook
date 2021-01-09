package com.mabem.homebook.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mabem.homebook.Model.Item;
import com.mabem.homebook.R;

import java.util.ArrayList;

public class ReceiptinfoAdapter extends RecyclerView.Adapter<ReceiptinfoAdapter.ViewHolder> {


    private final Context context;
    private final ArrayList<Item> items;

    public ReceiptinfoAdapter(Context context, ArrayList<Item> items) {
        this.context = context;
        this.items = items;
    }


    @NonNull
    @Override
    public ReceiptinfoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.receipt_menu_item, parent, false);
        return new ReceiptinfoAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ReceiptinfoAdapter.ViewHolder holder, int position) {
        Item i = items.get(position);
        holder.itemName.setText(i.getName());
        holder.itemPrice.setText(i.getPrice().toString());

    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView itemName;
        private TextView itemPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.receiptinfo_item_name);
            itemPrice = itemView.findViewById(R.id.receiptinfo_item_price);

        }
    }


}
