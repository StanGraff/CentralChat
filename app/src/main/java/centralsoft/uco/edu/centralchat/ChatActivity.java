package centralsoft.uco.edu.centralchat;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private boolean phoneDevice = true; // used to force portrait mode

    private Button btnSend;
    private EditText inputMsg;

    private MessageListAdapter adapter;
    private List<Message> messageList;
    private ListView listViewMessages;
    private boolean isMyMsg = true;

    SharedPreferencesProcessing sharedPreferencesProcessing = new SharedPreferencesProcessing();
    Utils utils = new Utils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setTitle("Chat");

        btnSend = (Button) findViewById(R.id.send);
        inputMsg = (EditText) findViewById(R.id.message);
        listViewMessages = (ListView) findViewById(R.id.messages_list);

        messageList = new ArrayList<Message>();

        adapter = new MessageListAdapter(this, messageList);
        listViewMessages.setAdapter(adapter);

        //Determine screen size
        int screenSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;

        //If device is a tablet, set phoneDevice to false
        if (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE || screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE)
            phoneDevice = false;

        if (phoneDevice)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String myName = sharedPreferencesProcessing.retrieveNickname(ChatActivity.this);
                Bitmap img = utils.getRoundedShape(sharedPreferencesProcessing.retrieveImage(ChatActivity.this));
                String msg = inputMsg.getText().toString();
                Message m = new Message(myName, msg, isMyMsg, img);
                messageList.add(m);
                adapter.notifyDataSetChanged();
                inputMsg.setText("");

                Message s = new Message("Auto Response", "No other users in room", false, null);
                messageList.add(s);
                adapter.notifyDataSetChanged();
            }
        });
        //listMessages.add(m);
        //adapter.notifyDataSetChanged();
    }

    /*@Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //get the default Display object representing the screen
        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        Point screenSize = new Point(); // used to store screen size
        display.getRealSize(screenSize); // store size in screenSize

        //Display the app's menu only in portrait orientation
        if (screenSize.x < screenSize.y) // x is width, y is hight
        {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_chat, menu);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent editProfileIntent = new Intent(this, EditProfileActivity.class);
            startActivity(editProfileIntent);
            return super.onOptionsItemSelected(item);
        } else if (id == R.id.chat_rooms) {
            Intent chatRoomsIntent = new Intent(this, ChatRoomsActivity.class);
            startActivity(chatRoomsIntent);
            return super.onOptionsItemSelected(item);
        } else return true;
    }
}
