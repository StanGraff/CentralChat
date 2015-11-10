package centralsoft.uco.edu.centralchat;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Ivan on 11/1/2015.
 */
public class ShowUsersListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] roomList;
    //  private final Integer[] imgid;
    private List<UserIcon> iconList;
    // Can take a image for a custom image for each room if we want. Else, we delete the commented out lines

    public ShowUsersListAdapter(Activity context, String[] roomList) {
        super(context, R.layout.chatroom_listview, roomList);

        this.context = context;
        this.roomList = roomList;
        this.iconList = new SharedPreferencesProcessing().getIcons(context);
        //  this.imgid=imgid;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View showRow = inflater.inflate(R.layout.chatroom_listview, null, true);

        TextView txtTitle = (TextView) showRow.findViewById(R.id.item);
        ImageView imageView = (ImageView) showRow.findViewById(R.id.icon);
        TextView roomDescription = (TextView) showRow.findViewById(R.id.description);


        txtTitle.setText(roomList[position]);
        for(int i = 0; i < iconList.size(); i++){
            if(roomList[position] == iconList.get(i).getUserID()){
                imageView.setImageBitmap(new SharedPreferencesProcessing().getMessageImage(iconList.get(i).getIcon()));
                break;
            }
        }
//        imageView.setImageResource(imgid[position]);
        roomDescription.setText("Description of " + roomList[position]);
        return showRow;

    }
}