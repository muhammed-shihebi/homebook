package com.mabem.homebook.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.mabem.homebook.Model.Objects.Home;
import com.mabem.homebook.R;
import com.mabem.homebook.Views.Main.Home.Receipt.FeedFragment;

import java.util.ArrayList;

public class MyhomesAdapter extends RecyclerView.Adapter<MyhomesAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Home> list;

    public MyhomesAdapter(Context context, ArrayList<Home> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyhomesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_menu_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyhomesAdapter.ViewHolder holder, int position) {
        String name = list.get(position).getName();
        holder.homename.setText(name);

    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private Button homename;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            homename = itemView.findViewById(R.id.home_button);

            homename.setOnClickListener(v -> {
                Navigation.findNavController(v).navigate(R.id.action_mainFragment_to_feedFragment);
                for (Home h : list) {
                    if (homename.getText().equals(h.getName())) {
                        FeedFragment.setHome_id(h.getId());
                    }
                }
            });
        }


    }
}
