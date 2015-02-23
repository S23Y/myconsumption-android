/*
package org.starfishrespect.myconsumption.android.notifications;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import org.starfishrespect.myconsumption.android.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;

*/
/**
 * Service that manages push notifications
 *//*

public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    public static final String TAG = "GCM Demo";

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                sendNotification(extras.getString("title"), extras.getString("message"));
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String title, String msg) {
        // disable notifications if not wanted

        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);


        */
/*
        Intent pendingClass = new Intent(this, AlertActivity.class);
        pendingClass.putExtra("title", title);
        pendingClass.putExtra("message", msg);
        *//*


        */
/*
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                pendingClass, Intent.FLAG_ACTIVITY_NEW_TASK);
        *//*

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_action_refresh)
                        .setContentTitle(getResources().getString(R.string.app_name))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(title))
                        .setContentText(title)
                        .setAutoCancel(true);

        //mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify((int) (System.currentTimeMillis() / 1000), mBuilder.build());


    }
}
*/
