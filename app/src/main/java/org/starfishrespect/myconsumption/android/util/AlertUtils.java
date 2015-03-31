package org.starfishrespect.myconsumption.android.util;

import android.app.AlertDialog;
import android.content.DialogInterface;

import org.starfishrespect.myconsumption.android.R;
import org.starfishrespect.myconsumption.android.SingleInstance;

/**
 * Created by thibaud on 27.03.15.
 */
public class AlertUtils {

    public static void buildAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SingleInstance.getChartActivity());
        builder.setTitle(R.string.dialog_title_error)
                .setMessage(SingleInstance.getChartActivity().getString(R.string.dialog_message_error_when_loading_please_reconnect))
                .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        SingleInstance.disconnect(SingleInstance.getChartActivity());
                    }
                });
        builder.show();
    }
}
