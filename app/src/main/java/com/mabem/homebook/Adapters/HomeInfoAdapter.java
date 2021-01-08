package com.mabem.homebook.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mabem.homebook.Model.Objects.Member;
import com.mabem.homebook.R;

import java.util.ArrayList;

public class HomeInfoAdapter extends RecyclerView.Adapter<HomeInfoAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Member> admins;
    private final ArrayList<Member> normalMembers;
    private final int numAllMembers;

    public HomeInfoAdapter(Context context, ArrayList<Member> admins, ArrayList<Member> normalMembers, int numMembers) {
        this.context = context;
        this.admins = admins;
        this.normalMembers = normalMembers;
        this.numAllMembers = numMembers;
    }

    @NonNull
    @Override
    public HomeInfoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_member_menu_item, parent, false);
        return new HomeInfoAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeInfoAdapter.ViewHolder holder, int position) {
        int numAdmins = admins.size();
        Log.d("Position", position + "");

        if (position < admins.size()) {
            holder.memberName.setText(admins.get(position).getName());
            holder.memberRole.setText(R.string.admin);
        } else {
            holder.memberName.setText(normalMembers.get(position - numAdmins).getName());
            holder.memberRole.setText(" ");
        }
        Log.d("membername", holder.memberName.getText().toString());
        Log.d("memberrole", holder.memberRole.getText().toString());

    }

    @Override
    public int getItemCount() {
        return this.numAllMembers;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView memberName;
        private TextView memberRole;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            memberName = itemView.findViewById(R.id.homemember_name);
            memberRole = itemView.findViewById(R.id.homemember_role);
        }
    }

}
