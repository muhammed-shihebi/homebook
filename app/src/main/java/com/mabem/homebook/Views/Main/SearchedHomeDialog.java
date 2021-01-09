package com.mabem.homebook.Views.Main;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import com.mabem.homebook.Model.Home;
import com.mabem.homebook.R;
import com.mabem.homebook.Utils.SearchResultListener;

import java.util.ArrayList;

public class SearchedHomeDialog extends DialogFragment {

    ArrayList<Home> searchResult;
    CharSequence[] homeNames;
    SearchResultListener searchResultListener;
    Context context;
    int checkedItem = 0;


    public SearchedHomeDialog(ArrayList<Home> searchResult, SearchResultListener searchResultListener, Context context) {
        this.searchResultListener = searchResultListener;
        this.searchResult = searchResult;
        homeNames = new CharSequence[searchResult.size()];
        this.context = context;
        for (int i = 0; i < searchResult.size(); i++) {
            homeNames[i] = searchResult.get(i).getName();
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.search_result)
                .setNegativeButton(R.string.ok, (dialog, id) -> {
                    searchResultListener.onOkPressed();
                });


        if (!searchResult.isEmpty()) {
            builder.setSingleChoiceItems(homeNames, 0, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    checkedItem = which;
                }
            }).setPositiveButton(R.string.send_join_request, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    searchResultListener.onHomeSelected(searchResult.get(checkedItem).getId());
                }
            }).setNegativeButton(R.string.cancel, (dialog, id) -> {
                searchResultListener.onOkPressed();
            });
        } else {
            builder.setMessage("No Home found!");
        }


        return builder.create();
    }
}