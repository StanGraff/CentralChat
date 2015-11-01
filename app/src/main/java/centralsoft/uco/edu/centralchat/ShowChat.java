package centralsoft.uco.edu.centralchat;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class ShowChat extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_show_chat);


        FragmentManager manager = getFragmentManager();

        FragmentTransaction transaction = manager.beginTransaction();

        transaction.commit();

    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //get the default Display object representing the screen
        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        Point screenSize = new Point(); // used to store screen size
        display.getRealSize(screenSize); // store size in screenSize

        //Display the app's menu only in portrait orientation
       // if (screenSize.x < screenSize.y) // x is width, y is hight
      //  {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_chat, menu);
            return true;
      //  } else {
      //      return false;
      //  }
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
        } else if (id == R.id.clear_chat) {

       //     deleteChat();
            return super.onOptionsItemSelected(item);

        } else return true;
    }
    */
}
