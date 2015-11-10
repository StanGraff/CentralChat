package centralsoft.uco.edu.centralchat;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Point;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import centralsoft.uco.edu.centralchat.ChatRefreshService.MyBinder;

public class ShowChat extends AppCompatActivity {

    private FrameLayout chatFragment, roomOrUserFragment;
    private FragmentManager fm;
    private FragmentTransaction fragmentTransaction;
    private boolean showingRooms;
    private Button cancel, ok;
    private AlertDialog alert;
    private TextView alertText;
    private ChatActivity chatActivity;
    private ShowAvailableUsers userActivity;
    private ChatRoomsActivity roomsActivity;
    private Menu menu;

    ChatRefreshService mBoundService;
    boolean chatServiceBound = false;
    boolean serviceOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_show_chat);

        chatFragment = (FrameLayout) findViewById(R.id.chat);
        roomOrUserFragment = (FrameLayout) findViewById(R.id.Names_or_Rooms);


        Display display = ((WindowManager) this.getSystemService(this.WINDOW_SERVICE)).getDefaultDisplay();
        Point screenSize = new Point(); // used to store screen size
        display.getRealSize(screenSize); // store size in screenSize

        fm = getFragmentManager();
        fragmentTransaction = fm.beginTransaction();

        chatActivity = (ChatActivity) getFragmentManager().findFragmentByTag("Showing_Chat");
        userActivity = (ShowAvailableUsers) getFragmentManager().findFragmentByTag("Showing_Users");
        roomsActivity = (ChatRoomsActivity) getFragmentManager().findFragmentByTag("Showing_Rooms");


        if (screenSize.x < screenSize.y){ // x is width, y is height
            //---portrait mode---
            if (chatActivity == null) {

                fragmentTransaction.add(R.id.chat, new ChatActivity(), "Showing_Chat");
            }
        } else {
            //---landscape mode---
            if (chatActivity == null) {
                fragmentTransaction.add(R.id.chat, new ChatActivity(), "Showing_Chat");
            }
            if (userActivity == null) {
                fragmentTransaction.add(R.id.Names_or_Rooms, new ShowAvailableUsers(), "Showing_Users");
            }
        }


        fragmentTransaction.commit();


            if (!chatServiceBound) {
                Intent intent = new Intent(this, ChatRefreshService.class);
                startService(intent);
                bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
                chatServiceBound = true;
            }



        //if (chatServiceBound) {
        //    unbindService(mServiceConnection);
        //    chatServiceBound = false;
        //}
        //Intent intent = new Intent(ShowChat.this,
        //        ChatRefreshService.class);
        //stopService(intent);
    }



    @Override
    protected void onStart() {
        super.onStart();
        if (!serviceOn) {
            Intent intent = new Intent(this, ChatRefreshService.class);
            startService(intent);
            bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
            serviceOn = true;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

            if (!chatServiceBound) {
                unbindService(mServiceConnection);
                //chatServiceBound = false;
            }
        }




    public void choose(){
        final LayoutInflater inflater = this.getLayoutInflater();
        final View dialogueLayout = inflater.inflate(R.layout.ask_picture, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogueLayout);


        cancel = (Button) dialogueLayout.findViewById(R.id.camera);
        ok = (Button) dialogueLayout.findViewById(R.id.device);
        alertText = (TextView) dialogueLayout.findViewById(R.id.txt_dia);

        alertText.setText(getResources().getString(R.string.choose));

        cancel.setText(getResources().getString(R.string.rooms));
        ok.setText(getResources().getString(R.string.users));

        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent chatRoomsIntent = new Intent(ShowChat.this, ShowRooms.class);
                startActivity(chatRoomsIntent);
                ShowChat.this.alert.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent chatRoomsIntent = new Intent(ShowChat.this, ShowUsers.class);
                startActivity(chatRoomsIntent);
                ShowChat.this.alert.dismiss();
            }
        });
        alert = builder.create();

        alert.show();
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        Display display = ((WindowManager) this.getSystemService(this.WINDOW_SERVICE)).getDefaultDisplay();
        Point screenSize = new Point(); // used to store screen size
        display.getRealSize(screenSize); // store size in screenSize
        //Display the app's menu only in portrait orientation
        if (screenSize.x < screenSize.y) // x is width, y is hight
        {
            inflater.inflate(R.menu.menu_chat, menu);

        } else if (showingRooms) {
            inflater.inflate(R.menu.menu_chat_landscape_users, menu);

        } else if (!showingRooms) {
            inflater.inflate(R.menu.menu_chat_landscape, menu);
        }
        return true;
    }

        @Override
        public boolean onOptionsItemSelected (MenuItem item){

            int id = item.getItemId();

           if (id == R.id.chat_rooms) {
               choose();
                return true;
            } else if (id == R.id.chat_rooms_landscape) {
               showingRooms = true;
               invalidateOptionsMenu();
               fragmentTransaction = fm.beginTransaction();
               if (roomsActivity == null) {
                   fragmentTransaction.replace(R.id.Names_or_Rooms, new ChatRoomsActivity(), "Showing_Rooms");
               }else {
                   fragmentTransaction.replace(R.id.Names_or_Rooms, roomsActivity, "Showing_Rooms");
               }
               fragmentTransaction.addToBackStack(null);
               fragmentTransaction.commit();
                return true;

            }else if (id == R.id.chat_users_landscape) {
               showingRooms = false;
               invalidateOptionsMenu();
               fragmentTransaction = fm.beginTransaction();
               if (userActivity == null) {
                   fragmentTransaction.replace(R.id.Names_or_Rooms, new ShowAvailableUsers(), "Showing_Users");
               }else{
                   fragmentTransaction.replace(R.id.Names_or_Rooms, userActivity, "Showing_Users");

               }
               fragmentTransaction.addToBackStack(null);
               fragmentTransaction.commit();
               return true;

           } else return super.onOptionsItemSelected(item);
        }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("Rooms", showingRooms);
        savedInstanceState.putBoolean("ServiceState", chatServiceBound);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        showingRooms = savedInstanceState.getBoolean("Rooms");
        chatServiceBound = savedInstanceState.getBoolean("ServiceState");

    }


    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //chatServiceBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyBinder myBinder = (MyBinder) service;
            mBoundService = myBinder.getService();
            //chatServiceBound = true;
        }
    };

}

