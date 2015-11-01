package centralsoft.uco.edu.centralchat;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class ShowAvailableUsers extends Fragment {

    private ListView userList;

    String[] listOfUsers = {
            "User 1",
            "User 2",
            "User 3",
            "User 4",
            "User 5",
            "User 6",
            "User 7",
            "User 8",
            "User 9",
            "User 10",
            "User 11",
            "User 12",
            "User 13",
            "User 14",
            "User 15"
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_show_available_users, container, false);

        ChatRoomListAdapter adapter = new ChatRoomListAdapter(getActivity(), listOfUsers);
        userList = (ListView) view.findViewById(R.id.userList);
        userList.setAdapter(adapter);

        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String Selecteditem = listOfUsers[+position];
                Toast.makeText(getActivity(), Selecteditem + " Selected", Toast.LENGTH_SHORT).show();

            }
        });
        return view;
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_chat_rooms, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            Intent editProfilesIntent = new Intent(this, EditProfileActivity.class);
            startActivity(editProfilesIntent);
            return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);

        return true;
    }
*/
}