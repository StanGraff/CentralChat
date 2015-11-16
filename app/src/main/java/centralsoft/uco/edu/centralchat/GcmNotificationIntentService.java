package centralsoft.uco.edu.centralchat;

/**
 * Created by Stanislav on 11/15/2015.
 */

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmNotificationIntentService extends IntentService {
    // Sets an ID for the notification, so it can be updated
    public static final int notifyID = 9001;
    private PendingIntent mContentIntent;
    NotificationCompat.Builder builder;
    private static final int MY_NOTIFICATION_ID = 1;
    private int mNotificationCount = 0;

    public GcmNotificationIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
                    .equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
                    .equals(messageType)) {
                sendNotification("Deleted messages on server: "
                        + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
                    .equals(messageType)) {
                String to = extras.get(GcmConstants.MSG_TO).toString();
                String from = extras.get(GcmConstants.MSG_FROM).toString();
                String msg = extras.get(GcmConstants.MSG).toString();//.get(GcmConstants.MSG_KEY).toString();

                sendNotification(//"Message Received from Google GCM Server:\n\n"
                        ""
                                + "Message: " + msg + " From:" + from + " To:" + to);
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String msg) {

        Context context = this;

        Intent intent = new Intent(context, ShowChat.class);


        Uri soundUri  = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        mContentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder notificationBuilder = new Notification.Builder(
                context)
                .setSmallIcon(R.drawable.chat_central_logo)
                .setAutoCancel(true)
                .setContentTitle(++mNotificationCount + " New Message")
                //.setContentText("Click to view the Message")
                .setContentText(msg)
                .setContentIntent(mContentIntent)
                .setContentIntent(mContentIntent).setSound(soundUri);


        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(MY_NOTIFICATION_ID,
                notificationBuilder.build());

    }
}
