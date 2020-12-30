package com.mabem.homebook.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.mabem.homebook.Fragments.Main.Home.EditHomeFragment;
import com.mabem.homebook.Model.Item;
import com.mabem.homebook.Model.Member;
import com.mabem.homebook.R;
import com.mabem.homebook.Utils.EditHomeMemberListener;
import com.mabem.homebook.Utils.ReceiptManagerItemListener;

import java.util.ArrayList;

public class EditHomeAdapter extends RecyclerView.Adapter<EditHomeAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<Member> allMembers;
    private int numAdmins;
    EditHomeMemberListener editHomeMemberListener;
    private Member currentMember;

    public EditHomeAdapter(Context context, ArrayList<Member> allMembers, int numAdmins, EditHomeMemberListener editHomeMemberListener) {
        this.context = context;
        this.allMembers = allMembers;
        this.numAdmins = numAdmins;
        this.editHomeMemberListener = editHomeMemberListener;
    }

    @NonNull
    @Override
    public EditHomeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_home_member_menu_item, parent, false);
        return new EditHomeAdapter.ViewHolder(v,editHomeMemberListener);
    }

    @Override
    public void onBindViewHolder(@NonNull EditHomeAdapter.ViewHolder holder, int position) {
        holder.populate(position);
    }

    @Override
    public int getItemCount() {
        return this.allMembers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView memberName;
        private ImageView delete;
        private SwitchCompat admin;
        private EditHomeMemberListener listener;

        public ViewHolder(@NonNull View itemView, EditHomeMemberListener listener) {
            super(itemView);
            memberName = itemView.findViewById(R.id.home_edit_member_name);
            delete = itemView.findViewById(R.id.home_edit_delete_member);
            admin = itemView.findViewById(R.id.admin_switch);
            this.listener = listener;
        }


        public void populate(int position) {
            memberName.setText(allMembers.get(position).getName());
            if(position < numAdmins){
                admin.setChecked(true);
            }else{
                admin.setChecked(false);
            }

            delete.setOnClickListener(v -> {
                currentMember = allMembers.get(getAdapterPosition());
                if(admin.isChecked() && numAdmins == 1){
                    listener.onDeletingLastAdmin();
                }else{
                    listener.onDeleteClicked(currentMember, getAdapterPosition());
                }
            });

            admin.setOnClickListener(v -> {
                if(admin.isChecked()){
                    currentMember = allMembers.get(getAdapterPosition());
                    listener.onAdminSwitchClicked(currentMember, false);
                    numAdmins++;
                }else{
                    if((numAdmins-1) == 0){
                        admin.setChecked(true);
                        currentMember = allMembers.get(getAdapterPosition());
                        listener.onAdminSwitchClicked(currentMember, true);
                    }else{
                        currentMember = allMembers.get(getAdapterPosition());
                        listener.onAdminSwitchClicked(currentMember, false);
                        numAdmins--;
                    }
                }
            });
        }
    }
}
