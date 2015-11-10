package centralsoft.uco.edu.centralchat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Justin on 10/28/15.
 */
public class MessageListAdapter extends BaseAdapter {

    private Context context;
    private List<Message> messageList;
    private List<UserIcon> iconList;
    private PendingIntent mContentIntent;
    private static final int MY_NOTIFICATION_ID = 1;
    private int mNotificationCount = 0;




    public MessageListAdapter(Context context, List<Message> list, List<UserIcon> userIcons) {
        this.context = context;
        this.messageList = list;
        this.iconList = userIcons;
    }

    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message m = messageList.get(position);

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (messageList.get(position).isMyMsg().equals("1")) {
            convertView = mInflater.inflate(R.layout.sent_message_item, null);
            convertView = mInflater.inflate(R.layout.sent_message_item, null);
            ImageView img = (ImageView) convertView.findViewById(R.id.userIcon);
            SharedPreferencesProcessing spp = new SharedPreferencesProcessing();
            Utils utils = new Utils();
            img.setImageBitmap(utils.getRoundedShape(spp.retrieveImage(context)));
        } else {
            convertView = mInflater.inflate(R.layout.incoming_message_item, null);
            ImageView img = (ImageView) convertView.findViewById(R.id.userIcon);
            if(messageList.get(position).getMsgFrom().equals("Auto Response")){
                img.setImageResource(R.drawable.chat_central_logo);
            }else{
                for(int i = 0; i < iconList.size() ; i++){
                    if(iconList.get(i).getUserID().equals(messageList.get(position).getMsgFrom()) && iconList.get(i).getIcon().length() > 1000){
                        String iconString = iconList.get(i).getIcon();
                        SharedPreferencesProcessing spp = new SharedPreferencesProcessing();
                        Bitmap tmpBitmap = spp.getMessageImage(iconString);
                        Utils utils = new Utils();
                        img.setImageBitmap(utils.getRoundedShape(tmpBitmap));
                        break;
                    }
                }
            }
        }

        TextView lblFrom = (TextView) convertView.findViewById(R.id.lblMsgFrom);
        TextView txtMsg = (TextView) convertView.findViewById(R.id.txtMsg);
        TextView received = (TextView) convertView.findViewById(R.id.date);

        txtMsg.setText(m.getMsg());
        lblFrom.setText(m.getMsgFrom());
        received.setText(m.getMsgDate());
        mNotificationCount = 0;


        if(m.isViewed() == null){
            m.setViewed("1");
        }


        if (messageList.get(position).isMyMsg().equals("0") && m.isViewed().equals("0")) {
            Intent intent = new Intent(context, ShowChat.class);
            Uri soundUri  = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            mContentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Notification.Builder notificationBuilder = new Notification.Builder(
                    context)
                    .setSmallIcon(R.drawable.chat_central_logo)
                    .setAutoCancel(true)
                    .setContentTitle(++mNotificationCount + " New Message")
                    .setContentText("Click to view the Message")
                    .setContentIntent(mContentIntent)
                    .setContentIntent(mContentIntent).setSound(soundUri );


            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(MY_NOTIFICATION_ID,
                    notificationBuilder.build());
        }
        m.setViewed("1");

        return convertView;
    }
}
