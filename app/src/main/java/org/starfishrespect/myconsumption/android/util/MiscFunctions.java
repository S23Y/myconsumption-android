package org.starfishrespect.myconsumption.android.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import org.starfishrespect.myconsumption.android.R;

/**
 * Class that contains static functions that may be useful anywhere on the app
 * S23Y (2015). Licensed under the Apache License, Version 2.0.
 */
public class MiscFunctions {

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }

    public static Dialog makeOfflineDialog(Context context) {
        return new AlertDialog.Builder(context)
                .setTitle(R.string.dialog_title_error)
                .setMessage("An active internet connection is needed to reload the data")
                .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
    }
}
