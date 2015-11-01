package centralsoft.uco.edu.centralchat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class ChatRoomsActivity extends AppCompatActivity {

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_rooms);

        ChatRoomListAdapter adapter = new ChatRoomListAdapter(this, listOfRooms);
        chatRoomList = (ListView) findViewById(R.id.roomList);
        chatRoomList.setAdapter(adapter);

        chatRoomList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String Selecteditem = listOfRooms[+position];
                Toast.makeText(getApplicationContext(), Selecteditem + " Selected", Toast.LENGTH_SHORT).show();

            }
        });

    }

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
        */
        return true;
    }
}
