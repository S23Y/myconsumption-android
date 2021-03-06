package org.starfishrespect.myconsumption.android.notifications;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import org.starfishrespect.myconsumption.android.GCMIntentService;
import org.starfishrespect.myconsumption.android.util.PrefUtils;

/**
 * Receiver for push notifications
 * S23Y (2015). Licensed under the Apache License, Version 2.0.
 * Author: Thibaud Ledent
 */
public class GCMBroadcastReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            // If the user don't want to receive notifications
            if (!(PrefUtils.getSyncNotification(context)))
                return;

            // Explicitly specify that GcmIntentService will handle the intent.
            ComponentName comp = new ComponentName(context.getPackageName(),
                    GCMIntentService.class.getName());
            // Start the service, keeping the device awake while it is launching.
            startWakefulService(context, (intent.setComponent(comp)));
            setResultCode(Activity.RESULT_OK);
        } catch (Exception e) {
            Log.e("GCMBroadcastReceiver: ", e.toString());
        }
    }
}
