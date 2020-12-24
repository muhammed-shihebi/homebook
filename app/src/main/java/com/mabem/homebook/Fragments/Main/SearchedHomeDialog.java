package com.mabem.homebook.Fragments.Main;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.mabem.homebook.R;

public class SearchedHomeDialog extends DialogFragment {

    String homeName;

    public SearchedHomeDialog(String homeName) {
        this.homeName = homeName;
    }

    public SearchedHomeDialog() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.search_result)
                .setMessage(getString(R.string.searched_home_dialog_message_1) + " \""+ homeName + "\" " + getString(R.string.searched_home_dialog_message_2))
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getContext(), "Yes", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getContext(), "No", Toast.LENGTH_SHORT).show();
                    }
                });

        return builder.create();
    }
}
