package com.mabem.homebook.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.mabem.homebook.Fragments.Main.Home.Receipt.ReceiptInfoFragment;
import com.mabem.homebook.Fragments.Main.Home.Receipt.ReceiptManageFragment;
import com.mabem.homebook.Model.Item;
import com.mabem.homebook.Model.Receipt;
import com.mabem.homebook.R;

import java.text.DateFormat;
import java.util.ArrayList;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Receipt> list;
    private final boolean isAdmin;
    private final String member_id;

    public FeedAdapter(Context context, ArrayList<Receipt> list, boolean isAdmin, String member_id) {
        this.context = context;
        this.list = list;
        this.isAdmin = isAdmin;
        this.member_id = member_id;
    }

    @NonNull
    @Override
    public FeedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_item, parent, false);
        return new FeedAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedAdapter.ViewHolder holder, int position) {
        holder.populate(list.get(position));
    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private FrameLayout shape;
        private TextView receiptName;
        private TextView receiptDate;
        private TextView receiptTotal;
        private TextView memberName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            shape = itemView.findViewById(R.id.feed_shape);
            receiptDate = itemView.findViewById(R.id.feed_receipt_date);
            receiptName = itemView.findViewById(R.id.feed_receipt_name);
            receiptTotal = itemView.findViewById(R.id.feed_receipt_total);
            memberName = itemView.findViewById(R.id.feed_member_name);
        }

        public void populate(Receipt receipt) {
            receiptName.setText(receipt.getName().trim());
            DateFormat sdf = DateFormat.getDateInstance();
            receiptDate.setText(sdf.format(receipt.getDate().getTime()));
            receiptTotal.setText(receipt.getTotal().toString());
            memberName.setText(receipt.getMemberName());

            shape.setOnClickListener(v -> {
                if(isAdmin || member_id.equals(receipt.getMemberId())){
                    Navigation.findNavController(v).navigate(R.id.action_feedFragment_to_manageReceiptFragment);
                    ReceiptManageFragment.setToEditReceipt(receipt);
                    ReceiptManageFragment.setToEditFlag(true);
                }else{
                    Navigation.findNavController(v).navigate(R.id.action_feedFragment_to_receiptInfoFragment);
                    ReceiptInfoFragment.setReceipt(receipt);
                    ReceiptManageFragment.setToEditFlag(false);
                }
            });
        }
    }
}