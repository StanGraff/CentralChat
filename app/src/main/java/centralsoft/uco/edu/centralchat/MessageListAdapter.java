package centralsoft.uco.edu.centralchat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Justin on 10/28/15.
 */
public class MessageListAdapter extends BaseAdapter {

    private Context context;
    private List<Message> messageList;

    public MessageListAdapter(Context context, List<Message> list) {
        this.context = context;
        this.messageList = list;
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
//            ImageView img = (ImageView) convertView.findViewById(R.id.userIcon);
//            img.setImageResource(R.drawable.chat_central_logo);
        } else {
            convertView = mInflater.inflate(R.layout.incoming_message_item, null);
        }

        TextView lblFrom = (TextView) convertView.findViewById(R.id.lblMsgFrom);
        TextView txtMsg = (TextView) convertView.findViewById(R.id.txtMsg);
        TextView received = (TextView) convertView.findViewById(R.id.date);

        txtMsg.setText(m.getMsg());
        lblFrom.setText(m.getMsgFrom());
        received.setText(m.getDate());

        return convertView;
    }
}
