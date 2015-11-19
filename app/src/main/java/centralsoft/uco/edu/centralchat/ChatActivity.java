package centralsoft.uco.edu.centralchat;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import static com.google.android.gms.internal.zzid.runOnUiThread;

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
    private PlaySound sound;
    
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

    GcmBroadcastReceiver mGBR;

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

        sound = new PlaySound(getActivity().getApplicationContext());
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

        //getActivity().setTitle(chatID);
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
        } else {
            chatID = "Recepient";
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


        btnSend.setBackgroundColor(Color.parseColor("#434343"));

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String myName = sharedPreferencesProcessing.retrieveNickname(getActivity());
                String msg = inputMsg.getText().toString();
                if (!msg.isEmpty()) {
                    Message m = new Message(myName, msg, isMyMsg, utils.getDate(), "0");
                    messageList.add(m);
                    adapter.notifyDataSetChanged();
                    inputMsg.setText("");

                    Random rand = new Random();
                    String randomNum = Integer.toString(rand.nextInt((100 - 1) + 1) + 1);
                    sendMessageToTheServer(msg, myName, chatID);

                    if (chatID.equals("")) {
                        Message s = new Message("Auto Response", "No user or room selected", "0", utils.getDate(), "0");
                        messageList.add(s);
                    }
                    sound.playSent();
                    adapter.notifyDataSetChanged();
                    btnSend.setEnabled(false);
                    btnSend.setBackgroundColor(Color.parseColor("#434343"));
                }
            }
        });

        inputMsg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count >= 0)
                    btnSend.setEnabled(true);
                btnSend.setBackgroundColor(Color.parseColor("#3F51B5"));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        
        //listMessages.add(m);
        //adapter.notifyDataSetChanged();

        Thread myThread = null;

        Runnable runnable = new CountDownRunner();
        myThread = new Thread(runnable);
        myThread.start();

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

//        unregisterGcmReceiver();

        if (messageList != null && !chatID.equals("")) {
            sharedPreferencesProcessing.storeChat((ArrayList<Message>) messageList, chatID, getActivity());
            sharedPreferencesProcessing.storeIcons((ArrayList<UserIcon>) iconList, getActivity());
        }
    }

    @Override
    public void onResume() {
        super.onResume();

//        registerGcmReceiver();

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

//    @Override
//    public void onStart(){
//        super.onStart();
//        registerGcmReceiver();
//    }
//
//    @Override
//    public void onStop(){
//        super.onStop();
//        unregisterGcmReceiver();
//    }
//
//    @Override
//    public void onDestroy(){
//        super.onDestroy();
//        //unregisterReceiver();
//    }


//    private void registerGcmReceiver(){
//        mGBR = new GcmBroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                Bundle extras = intent.getExtras();
//                GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(getActivity());
//
//                String messageType = gcm.getMessageType(intent);
//
//                if(!extras.isEmpty()){
//                    if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
//                            .equals(messageType)) {
//                        ComponentName comp = new ComponentName(context.getPackageName(),
//                                GcmNotificationIntentService.class.getName());
//                        startWakefulService(context, (intent.setComponent(comp)));
//                        setResultCode(Activity.RESULT_OK);
//                    } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
//                            .equals(messageType)) {
//                        ComponentName comp = new ComponentName(context.getPackageName(),
//                                GcmNotificationIntentService.class.getName());
//                        startWakefulService(context, (intent.setComponent(comp)));
//                        setResultCode(Activity.RESULT_OK);
//                    } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
//                            .equals(messageType)) {
//                        String to = extras.get(GcmConstants.MSG_TO).toString();
//                        String from = extras.get(GcmConstants.MSG_FROM).toString();
//                        String msg = extras.get(GcmConstants.MSG).toString();
//                        if(sharedPreferencesProcessing.getChatID(getActivity()).equals(from)){
//                            if(sharedPreferencesProcessing.retrieveNickname(getActivity()).equals(to)){
//                                Message m = new Message(from, msg, "0", utils.getDate(), "1");
//                                messageList.add(m);
//                                adapter.notifyDataSetChanged();
//                                setResultCode(Activity.RESULT_OK);
//                            }else{
//                                setResultCode(Activity.RESULT_OK);
//                            }
//                        }else{
//                            ComponentName comp = new ComponentName(context.getPackageName(),
//                                    GcmNotificationIntentService.class.getName());
//                            startWakefulService(context, (intent.setComponent(comp)));
//                            setResultCode(Activity.RESULT_OK);
//                        }
//                    }
//                }
//            }
//        };
//        try{
//            getActivity().registerReceiver(mGBR , new IntentFilter("CHAT_FRAGMENT"));
//        }catch(Exception e){
//            Toast.makeText(getActivity(), e.getMessage().toString(), Toast.LENGTH_LONG).show();
//        }
//        //getActivity().registerReceiver(mGBR , new IntentFilter("CHAT_FRAGMENT"));
//    }
//
//    private void unregisterGcmReceiver(){
//        try{
//            getActivity().unregisterReceiver(mGBR);
//        }catch(Exception e){
//
//        }
//
//    }

    public void doWork() {
        runOnUiThread(new Runnable() {
            public void run() {
                try {
                    if (sharedPreferencesProcessing.getRecievedChat(chatID, getActivity().getApplicationContext()) != null) {
                        if (sharedPreferencesProcessing.getRecievedChat(chatID, getActivity().getApplicationContext()).size() > 0) {
                            List<Message> temp;
                            temp = sharedPreferencesProcessing.getRecievedChat(chatID, getActivity().getApplicationContext());
                            for (int i = 0; i < temp.size(); i++) {
                                messageList.add(temp.get(i));
//                                Message m = messageList.get(i);
//                                if(sharedPreferencesProcessing.getChatID(getActivity().getApplicationContext()).equals(m.getMsgFrom())){
//                                    messageList.add(temp.get(i));
//                                    sound.playReceived();
//                                    adapter.notifyDataSetChanged();
//                                }else{
//                                    if(!sharedPreferencesProcessing.retrieveNickname(getActivity().getApplicationContext()).equals(m.getMsgFrom())){
//                                        ArrayList<Message> Ml = sharedPreferencesProcessing.getChat(m.getMsgFrom(), getActivity().getApplicationContext());
//                                        Ml.add(m);
//                                        sharedPreferencesProcessing.storeChat(Ml, m.getMsgFrom(), getActivity().getApplicationContext());
//                                    }
//                                }
                            }
                            temp.clear();
                            sharedPreferencesProcessing.storeRecieved((ArrayList<Message>) temp, chatID, getActivity().getApplicationContext());
                            adapter.notifyDataSetChanged();
                            sound.playReceived();
                        }//else{
                        //   Toast.makeText(getActivity(), "Size < 0", Toast.LENGTH_SHORT).show();
                        //  }
                    }//else{
                    // Toast.makeText(getActivity(), "Received Null", Toast.LENGTH_SHORT).show();
                    //}
                } catch (Exception e) {
                }
            }
        });
    }

    class CountDownRunner implements Runnable {
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    doWork();
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                }
            }
        }
    }
}
