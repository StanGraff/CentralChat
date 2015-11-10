package centralsoft.uco.edu.centralchat;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class ChatRoomsActivity extends Fragment {

    private ListView chatRoomList;

    String[] listOfRooms = {
            "Room 1",
            "Room 2",
            "Room 3",
            "Room 4",
            "Room 5",
            "Room 6",
            "Room 7",
            "Room 8",
            "Room 9",
            "Room 10",
            "Room 11",
            "Room 12",
            "Room 13",
            "Room 14",
            "Room 15",
            "Room 16"
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_chat_rooms, container, false);


        ChatRoomListAdapter adapter = new ChatRoomListAdapter(getActivity(), listOfRooms);
        chatRoomList = (ListView) view.findViewById(R.id.roomList);
        chatRoomList.setAdapter(adapter);

        chatRoomList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String Selecteditem = listOfRooms[+position];
                Toast.makeText(getActivity(), Selecteditem + " Selected", Toast.LENGTH_SHORT).show();

            }
        });

            return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
         return true;
    }
}
