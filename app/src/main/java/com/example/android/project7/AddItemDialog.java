package com.example.android.project7;

import android.app.Dialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.android.project7.data.ItemsContract;

import java.util.ArrayList;
import java.util.List;

public class AddItemDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        b.setView(inflater.inflate(R.layout.edit_item_dialog, null))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // SHOULD NOW WORK
                        //						String category = categoryBox.getText().toString();
                        //Uri itemUri = getActivity().getContentResolver().insert(ItemsContract.ItemsEntry.CONTENT_URI, cv);
                        //mItemsGridAdapter.notifyDataSetChanged();
                    }
                });
        b.setNegativeButton("CANCEL", null);
        b.create().show();
        // Create the AlertDialog object and return it
        return b.create();
    }
}