package centralsoft.uco.edu.centralchat;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends Fragment {

    private boolean phoneDevice = true; // used to force portrait mode

    private Button btnSend;
    private TextView deletionText;
    private EditText inputMsg;
    private AlertDialog alert;
    private MessageListAdapter adapter;
    private List<Message> messageList, temp;
    private ListView listViewMessages;
    private String isMyMsg = "1";
    private Button cancel, ok;
    private int i;


    SharedPreferencesProcessing sharedPreferencesProcessing = new SharedPreferencesProcessing();
    Utils utils = new Utils();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_chat, container, false);
        setHasOptionsMenu(true);

        btnSend = (Button) view.findViewById(R.id.send);
        inputMsg = (EditText) view.findViewById(R.id.message);
        listViewMessages = (ListView) view.findViewById(R.id.messages_list);

        messageList = new ArrayList<Message>();
        temp = messageList = new ArrayList<Message>();


        adapter = new MessageListAdapter(getActivity(), messageList);
        listViewMessages.setAdapter(adapter);


        listViewMessages.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {
                i = arg2;
                deleteIndividual();
                return false;
            }
        });


        //Determine screen size
        int screenSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;

        //If device is a tablet, set phoneDevice to false
        if (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE || screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE)
            phoneDevice = false;

        if (phoneDevice)
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toast.makeText(getActivity(), "Size " + messageList.size(), Toast.LENGTH_SHORT);




        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String myName = sharedPreferencesProcessing.retrieveNickname(getActivity());
                String msg = inputMsg.getText().toString();
                Message m = new Message(myName, msg, isMyMsg);
                messageList.add(m);
                adapter.notifyDataSetChanged();
                inputMsg.setText("");

                Message s = new Message("Auto Response", "No other users in room", "0");
                messageList.add(s);
                adapter.notifyDataSetChanged();
            }
        });
        //listMessages.add(m);
        //adapter.notifyDataSetChanged();
        return view;
    }

    /*@Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }*/

    private void deleteIndividual() {

        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogueLayout = inflater.inflate(R.layout.ask_picture, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogueLayout);


        cancel = (Button) dialogueLayout.findViewById(R.id.camera);
        ok = (Button) dialogueLayout.findViewById(R.id.device);
        deletionText = (TextView) dialogueLayout.findViewById(R.id.txt_dia);

        deletionText.setText(getResources().getString(R.string.deleteOne));

        cancel.setText(getResources().getString(R.string.cancel));
        ok.setText(getResources().getString(R.string.ok));

        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ChatActivity.this.alert.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                messageList.remove(i);
                adapter.notifyDataSetChanged();
                ChatActivity.this.alert.dismiss();
            }
        });
        alert = builder.create();

        alert.show();
    }


    private void deleteChat() {

        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogueLayout = inflater.inflate(R.layout.ask_picture, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogueLayout);


        cancel = (Button) dialogueLayout.findViewById(R.id.camera);
        ok = (Button) dialogueLayout.findViewById(R.id.device);
        deletionText = (TextView) dialogueLayout.findViewById(R.id.txt_dia);

        deletionText.setText(getResources().getString(R.string.deleteChat));

        cancel.setText(getResources().getString(R.string.cancel));
        ok.setText(getResources().getString(R.string.ok));

        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ChatActivity.this.alert.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                messageList.clear();
                adapter.notifyDataSetChanged();
                ChatActivity.this.alert.dismiss();
            }
        });
        alert = builder.create();

        alert.show();
    }


    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first

        if (messageList != null) {
            sharedPreferencesProcessing.storeChat((ArrayList<Message>) messageList, getActivity());
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (sharedPreferencesProcessing.getChat(getActivity()) != null) {
            if (messageList.size() <= 0 && sharedPreferencesProcessing.getChat(getActivity()).size() > 0) {
                temp = sharedPreferencesProcessing.getChat(getActivity());
                for (int i = 0; i < temp.size(); i++) {
                    messageList.add(temp.get(i));
                }
                adapter.notifyDataSetChanged();

            }
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_chat, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent editProfileIntent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(editProfileIntent);
            return super.onOptionsItemSelected(item);
        } else if (id == R.id.chat_rooms) {
            Intent chatRoomsIntent = new Intent(getActivity(), ChatRoomsActivity.class);
            startActivity(chatRoomsIntent);
            return super.onOptionsItemSelected(item);
        } else if (id == R.id.clear_chat) {
            deleteChat();
            return super.onOptionsItemSelected(item);

        } else return true;
    }
/*

    public boolean onCreateOptionsMenu(Menu menu) {

        //get the default Display object representing the screen
        Display display = ((WindowManager) getActivity().getSystemService(getActivity().WINDOW_SERVICE)).getDefaultDisplay();
        Point screenSize = new Point(); // used to store screen size
        display.getRealSize(screenSize); // store size in screenSize

        //Display the app's menu only in portrait orientation
        if (screenSize.x < screenSize.y) // x is width, y is hight
        {
            // Inflate the menu; this adds items to the action bar if it is present.
            getActivity().getMenuInflater().inflate(R.menu.menu_chat, menu);
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
            Intent editProfileIntent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(editProfileIntent);
            return super.onOptionsItemSelected(item);
        } else if (id == R.id.chat_rooms) {
            Intent chatRoomsIntent = new Intent(getActivity(), ChatRoomsActivity.class);
            startActivity(chatRoomsIntent);
            return super.onOptionsItemSelected(item);
        } else if (id == R.id.clear_chat) {
            deleteChat();
            return super.onOptionsItemSelected(item);

        } else return true;
    }
 */
}
