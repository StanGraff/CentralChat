package centralsoft.uco.edu.centralchat;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
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

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ChatActivity extends Fragment {

    private boolean phoneDevice = true; // used to force portrait mode

    private Button btnSend;
    private TextView deletionText;
    private EditText inputMsg;
    private AlertDialog alert;
    private MessageListAdapter adapter;
    private List<Message> messageList;
    private List<UserIcon> iconList;
    private ListView listViewMessages;
    private String isMyMsg = "1";
    private Button cancel, ok;
    private int i;
    private String chatID = "";
    
    private ArrayList<List<String>> msgList;

    public void setMessageList(ArrayList<List<String>> messageL) {
        this.msgList = messageL;
    }

    public ArrayList<List<String>> getMessageList() {
        return this.msgList;
    }

    SharedPreferencesProcessing sharedPreferencesProcessing = new SharedPreferencesProcessing();
    Utils utils = new Utils();

    ProgressDialog prgDialog;
    RequestParams params = new RequestParams();


    public static ChatActivity newInstance(String chatID, Context ct){
        ChatActivity fragment = new ChatActivity();
        new SharedPreferencesProcessing().setChatID(chatID, ct);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_chat, container, false);
        setHasOptionsMenu(true);

        prgDialog = new ProgressDialog(getActivity());
        // Set Progress Dialog Text
        prgDialog.setMessage("Please wait...");
        // Set Cancelable as False
        prgDialog.setCancelable(false);

        btnSend = (Button) view.findViewById(R.id.send);
        inputMsg = (EditText) view.findViewById(R.id.message);
        listViewMessages = (ListView) view.findViewById(R.id.messages_list);

        messageList = new ArrayList<Message>();
        iconList = new ArrayList<UserIcon>();

        if(sharedPreferencesProcessing.getIcons(getActivity()) != null){
            List<UserIcon> tempIcon;
            // tempIcon = new ArrayList<UserIcon>();
            tempIcon = sharedPreferencesProcessing.getIcons(getActivity());
            for(int i = 0; i < tempIcon.size(); i++){
                iconList.add(tempIcon.get(i));
            }
        }

        adapter = new MessageListAdapter(getActivity(), messageList, iconList);
        listViewMessages.setAdapter(adapter);

        if(sharedPreferencesProcessing.getChatID(getActivity()) != null && !sharedPreferencesProcessing.getChatID(getActivity()).equals("")){
            chatID = sharedPreferencesProcessing.getChatID(getActivity());
            //get all messages with this chat id from SharedPreferencesProcessing
            if (sharedPreferencesProcessing.getChat(chatID, getActivity()) != null) {
                if (messageList.size() <= 0 && sharedPreferencesProcessing.getChat(chatID, getActivity()).size() > 0) {
                    List<Message> temp;
                    temp = sharedPreferencesProcessing.getChat(chatID, getActivity());
                    for (int i = 0; i < temp.size(); i++) {
                        messageList.add(temp.get(i));
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        }

        //temp = messageList = new ArrayList<Message>();


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


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String myName = sharedPreferencesProcessing.retrieveNickname(getActivity());
                String msg = inputMsg.getText().toString();
                Message m = new Message(myName, msg, isMyMsg, utils.getDate(), "0");
                messageList.add(m);
                adapter.notifyDataSetChanged();
                inputMsg.setText("");
                
                Random rand = new Random();
                String randomNum = Integer.toString(rand.nextInt((100 - 1) + 1) + 1);
                sendMessageToTheServer(msg, myName, "Recepient");

                if(chatID.equals("")){
                    Message s = new Message("Auto Response", "No user or room selected", "0", utils.getDate(), "0");
                    messageList.add(s);
                }
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
        super.onPause();

        if (messageList != null && !chatID.equals("")) {
            sharedPreferencesProcessing.storeChat((ArrayList<Message>) messageList, chatID, getActivity());
            sharedPreferencesProcessing.storeIcons((ArrayList<UserIcon>) iconList, getActivity());
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (sharedPreferencesProcessing.getChat(chatID, getActivity()) != null) {
            if (messageList.size() <= 0 && sharedPreferencesProcessing.getChat(chatID, getActivity()).size() > 0) {
                List<Message> temp;
                temp = sharedPreferencesProcessing.getChat(chatID, getActivity());
                for (int i = 0; i < temp.size(); i++) {
                    messageList.add(temp.get(i));
                }
                if(iconList.size() <= 0 && sharedPreferencesProcessing.getIcons(getActivity()).size() > 0){
                    List<UserIcon> tempIcon;
                    // tempIcon = new ArrayList<UserIcon>();
                    tempIcon = sharedPreferencesProcessing.getIcons(getActivity());
                    for(int i = 0; i < tempIcon.size(); i++){
                        iconList.add(tempIcon.get(i));
                    }
                }
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(menu.size() <= 1) {
            inflater.inflate(R.menu.chat_activity, menu);
        }

        super.onCreateOptionsMenu(menu, inflater);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent editProfileIntent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(editProfileIntent);
            return true;
        }
        if (id == R.id.clear_chat) {
            deleteChat();
            return true;
        } else if(id == R.id.simulate_message){
            if(chatID == ""){
                return super.onOptionsItemSelected(item);
            }
            Message m = new Message(chatID, "Hey, whats up?", "0", utils.getDate(), "0");
            messageList.add(m);
//            boolean hasIcon = false;
//            for(int i = 0; i < iconList.size() ; i++){
//                if(iconList.get(i).getUserID().equals(chatID)){
//                    hasIcon = true;
//                    iconList.get(i).setIcon(sharedPreferencesProcessing.getSimulatedIcon(chatID, getActivity()));
//                }
//            }
//            if(!hasIcon){
//                iconList.add(new UserIcon(chatID, sharedPreferencesProcessing.getSimulatedIcon(chatID, getActivity())));
//            }
            adapter.notifyDataSetChanged();
            return super.onOptionsItemSelected(item);
        }else{
            return super.onOptionsItemSelected(item);
        }
    }

    //Send message to the GCM server
    private void sendMessageToTheServer(String message, String fromUser, String toUser) {
        prgDialog.show();
        params.put("message", message);
        params.put("fromUser", fromUser);
        params.put("toUser", toUser);
        // Add additional parameters such as image, timestamp etc below.

        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(GcmConstants.APP_SEND_MESSAGE_URL, params,
                new AsyncHttpResponseHandler() {
                    // When the response returned by REST has Http
                    // response code '200'
                    @Override
                    public void onSuccess(String response) {
                        // Hide Progress Dialog
                        prgDialog.hide();
                        if (prgDialog != null) {
                            prgDialog.dismiss();
                        }
                        Toast.makeText(getActivity(),
                                "Message sent",
                                Toast.LENGTH_LONG).show();
                    }

                    // When the response returned by REST has Http
                    // response code other than '200' such as '404',
                    // '500' or '403' etc
                    @Override
                    public void onFailure(int statusCode, Throwable error,
                                          String content) {
                        // Hide Progress Dialog
                        prgDialog.hide();
                        if (prgDialog != null) {
                            prgDialog.dismiss();
                        }
                        // When Http response code is '404'
                        if (statusCode == 404) {
                            Toast.makeText(getActivity(),
                                    "Requested resource not found",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code is '500'
                        else if (statusCode == 500) {
                            Toast.makeText(getActivity(),
                                    "Something went wrong at server end",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code other than 404, 500
                        else {
                            Toast.makeText(
                                    getActivity(),
                                    "Unexpected Error occcured! [Most common Error: Device might "
                                            + "not be connected to Internet or remote server is not up and running], check for other errors as well",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


}
