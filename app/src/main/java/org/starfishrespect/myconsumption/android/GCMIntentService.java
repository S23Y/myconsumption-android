package org.starfishrespect.myconsumption.android;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import org.starfishrespect.myconsumption.android.notifications.GCMBroadcastReceiver;
import org.starfishrespect.myconsumption.android.ui.BaseActivity;
import org.starfishrespect.myconsumption.android.ui.ChartActivity;
import org.starfishrespect.myconsumption.android.ui.LoginActivity;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import static org.starfishrespect.myconsumption.android.util.LogUtils.LOGI;
import static org.starfishrespect.myconsumption.android.util.LogUtils.makeLogTag;

/**
 * Service that manages push notifications
 */
public class GCMIntentService extends IntentService {
    private static final String TAG = makeLogTag(GCMIntentService.class);

    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GCMIntentService() {
        super("GCMIntentService");
        LOGI(TAG,  "GCMIntentService started");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!(extras == null || extras.isEmpty() || messageType == null || messageType.isEmpty())) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            switch (messageType) {
                case GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR:
                    LOGI(TAG, "Send error: " + extras.toString());
                    break;
                case GoogleCloudMessaging.MESSAGE_TYPE_DELETED:
                    LOGI(TAG, "Deleted messages on server: " + extras.toString());

                    break;
                case GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE: // If it's a regular GCM message
                    // Post notification of received message.
                    sendNotification(extras.get("message").toString());
                    LOGI(TAG, "Received: " + extras.toString());
                    break;
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, LoginActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.icon_notification)
                        .setContentTitle(getResources().getString(R.string.app_name))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}