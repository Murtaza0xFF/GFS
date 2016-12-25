package com.theeaglehaslanded.goalazo.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.theeaglehaslanded.goalazo.R;

/**
 * Created on 5/12/15.
 */
public class LicenseDialog extends android.support.v4.app.DialogFragment {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View disclaimerView = LayoutInflater.from(getActivity()).inflate(R.layout.license_fragment, null);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setView(disclaimerView)
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        LicenseDialog.this.getDialog().cancel();
                    }
                });

        return dialogBuilder.create();
    }
}
