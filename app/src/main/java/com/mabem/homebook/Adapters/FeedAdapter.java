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
import com.mabem.homebook.Model.Receipt;
import com.mabem.homebook.R;

import java.text.DateFormat;
import java.util.ArrayList;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Receipt> list;
    private Receipt r;
    private boolean isAdmin;

    public FeedAdapter(Context context, ArrayList<Receipt> list, boolean isAdmin) {
        this.context = context;
        this.list = list;
        this.isAdmin = isAdmin;
    }

    @NonNull
    @Override
    public FeedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_item, parent, false);
        return new FeedAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedAdapter.ViewHolder holder, int position) {
        r = list.get(position);
        holder.receiptName.setText(r.getName().trim());

        DateFormat sdf = DateFormat.getDateInstance();

        holder.receiptDate.setText(sdf.format(r.getDate().getTime()));
        holder.receiptTotal.setText(r.getTotal().toString());
        holder.memberName.setText(r.getMemberName());



        //holder.memberPhoto.setImageURI();

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
        private ImageView memberPhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            shape = itemView.findViewById(R.id.feed_shape);
            receiptDate = itemView.findViewById(R.id.feed_receipt_date);
            receiptName = itemView.findViewById(R.id.feed_receipt_name);
            receiptTotal = itemView.findViewById(R.id.feed_receipt_total);
            memberName = itemView.findViewById(R.id.feed_member_name);
            memberPhoto = itemView.findViewById(R.id.feed_member_photo);

            shape.setOnClickListener(v -> {
                if(isAdmin){
                    Navigation.findNavController(v).navigate(R.id.action_feedFragment_to_manageReceiptFragment);
                    ReceiptManageFragment.setToEditReceipt(r);
                    ReceiptManageFragment.setToEditFlag(true);
                }else{
                    Navigation.findNavController(v).navigate(R.id.action_feedFragment_to_receiptInfoFragment);
                    ReceiptInfoFragment.setReceipt(r);
                }

            });
        }


    }
}