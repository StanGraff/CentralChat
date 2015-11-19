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

import java.util.ArrayList;
import java.util.List;

public class GcmNotificationIntentService extends IntentService {
    // Sets an ID for the notification, so it can be updated
    public static final int notifyID = 9001;
    private PendingIntent mContentIntent;
    NotificationCompat.Builder builder;
    private static final int MY_NOTIFICATION_ID = 1;
    private int mNotificationCount = 0;
    private Message m;
    Utils utils = new Utils();
    private List<Message> messageRecieved = new ArrayList<Message>();

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
                SharedPreferencesProcessing spp = new SharedPreferencesProcessing();


//                if (spp.getRecievedChat(from, getApplicationContext()) != null && spp.getRecievedChat(from, getApplicationContext()).size() <= 0){
//                    messageRecieved.clear();
//                }else if(spp.getRecievedChat(from, getApplicationContext()) != null){
//                    messageRecieved = spp.getRecievedChat(from, getApplicationContext());
//                }
//
//                m = new Message(from, msg, "0", utils.getDate(), "0");
//                messageRecieved.add(m);
//
//
//                if(messageRecieved.size() > 0){
//                    spp.storeRecieved((ArrayList<Message>) messageRecieved, from, getApplicationContext());
//                }
//
//                if(!spp.getChatID(getApplicationContext()).equals(from) && !spp.retrieveNickname(getApplicationContext()).equals(to)){
//                    sendNotification(//"Message Received from Google GCM Server:\n\n"
//                            ""
//                                    + "Message: " + msg + " From:" + from + " To:" + to);
//                }

                if (spp.retrieveNickname(getApplicationContext()).equals(to) && !spp.retrieveNickname(getApplicationContext()).equals(from)) {

                    if (spp.getRecievedChat(from, getApplicationContext()) != null && spp.getRecievedChat(from, getApplicationContext()).size() <= 0) {
                        messageRecieved.clear();
                    } else if (spp.getRecievedChat(from, getApplicationContext()) != null) {
                        messageRecieved = spp.getRecievedChat(from, getApplicationContext());
                    }

                    m = new Message(from, msg, "0", utils.getDate(), "0");
                    messageRecieved.add(m);


                    if (messageRecieved.size() > 0) {
                        spp.storeRecieved((ArrayList<Message>) messageRecieved, from, getApplicationContext());
                    }

                    if (!spp.getChatID(getApplicationContext()).equals(from)) {
                        sendNotification(//"Message Received from Google GCM Server:\n\n"
                                ""
                                        + "Message: " + msg + " From:" + from + " To:" + to);
                    }

//                    Utils u = new Utils();
//                    Message m = new Message(from, msg, "0", u.getDate(), "0");
//
//                    ArrayList<Message> mList;
//                    if(spp.getChat(from, getApplicationContext()) != null){
//                        mList = spp.getChat(from, getApplicationContext());
//                    }else{
//                        mList = new ArrayList<Message>();
//                    }
//
//                    mList.add(m);
//                    spp.storeChat(mList, from, getApplicationContext());
//                    ArrayList<String> users;
//                    if(spp.getAvailableUsers(getApplicationContext()) != null){
//                        users = spp.getAvailableUsers(getApplicationContext());
//                    }else{
//                        users = new ArrayList<String>();
//                    }
//                    if(!users.contains(from)){
//                        users.add(from);
//                    }
//                    spp.storeAvailableUsers(users, getApplicationContext());
//
//                    sendNotification(//"Message Received from Google GCM Server:\n\n"
//                            ""
//                                    + "Message: " + msg + " From:" + from + " To:" + to);
                } else {
                    ArrayList<String> users;
                    if (spp.getAvailableUsers(getApplicationContext()) != null) {
                        users = spp.getAvailableUsers(getApplicationContext());
                    } else {
                        users = new ArrayList<String>();
                    }
                    if (!users.contains(from) && !spp.retrieveNickname(getApplicationContext()).equals(from)) {
                        users.add(from);
                    }
                    spp.storeAvailableUsers(users, getApplicationContext());
                }
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
