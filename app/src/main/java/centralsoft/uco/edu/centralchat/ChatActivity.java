package centralsoft.uco.edu.centralchat;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
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
                new HttpPostTask().execute(randomNum, myName, "Recepient", msg);

                if(chatID.equals("")){
                    Message s = new Message("Auto Response", "No user or room selected", "0", utils.getDate(), "0");
                    messageList.add(s);
                }
                adapter.notifyDataSetChanged();
            }
        });
        
        btnSend.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {

                //Random rand = new Random();
                //String randomNum = Integer.toString(rand.nextInt((100 - 1) + 1) + 1);
                //new HttpPostTask().execute(randomNum, myName, "Recepient", msg);

                //Toast.makeText(getActivity(), "Long press", Toast.LENGTH_LONG).show();

                new HttpGetTask().execute("gettingMessages");



                return true;
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
        inflater.inflate(R.menu.chat_activity, menu);
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
    
   


    private class HttpGetTask extends AsyncTask<String, Void, ArrayList<List<String>>> {


        private static final String TAG = "HttpGetTask";

        // Construct the URL for the OpenWeatherMap query
        // Possible parameters are avaiable at OWM's forecast API page, at
        // http://openweathermap.org/API#forecast
        final String CHAT_BASE_URL =
                "http://104.236.65.167:8080/CentralChat_No_Auth/message/view";

        @Override
        protected ArrayList<List<String>> doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            InputStream in = null;
            HttpURLConnection httpUrlConnection = null;
            ArrayList<List<String>> resultArray = null;
            try {
                Uri builtUri = Uri.parse(CHAT_BASE_URL);//.buildUpon()
                //.appendQueryParameter("q", params[0]) // city
                //.appendQueryParameter("mode", "json") // json format as result
                //.appendQueryParameter("units", "metric") // metric unit
                //.appendQueryParameter("cnt", "1")      // 1 day forecast
                //.appendQueryParameter("type", "accurate")
                //.build();

                URL url = new URL(builtUri.toString());
                httpUrlConnection = (HttpURLConnection) url.openConnection();
                in = new BufferedInputStream(
                        httpUrlConnection.getInputStream());
                String data = readStream(in);
                resultArray = JSONMessageData.getData(data);

            } catch (MalformedURLException exception) {
                Log.e(TAG, "MalformedURLException");
            } catch (IOException exception) {
                Log.e(TAG, "IOException");
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage(), e);
                e.printStackTrace();
            } finally {
                if (null != httpUrlConnection) {
                    httpUrlConnection.disconnect();
                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (final IOException e) {
                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }

            //setMessageList(resultArray);

            return resultArray;
        }

        @Override
        protected void onPostExecute(ArrayList<List<String>> result) {


            if (result == null) {
                Toast.makeText(getActivity(),
                        "Something went wrong in the server-client communication!",
                        Toast.LENGTH_SHORT).show();
                return;
            }


            Toast.makeText(getActivity(),
                    "Data Received! Process it now! \n" + result.toString(),
                    Toast.LENGTH_SHORT).show();
        }
    }




    private class HttpGetUserMessagesTask extends AsyncTask<String, Void, ArrayList<List<String>>> {


        private static final String TAG = "HttpGetTask";

        // Construct the URL for the OpenWeatherMap query
        // Possible parameters are avaiable at OWM's forecast API page, at
        // http://openweathermap.org/API#forecast
        final String CHAT_BASE_URL =
                "http://104.236.65.167:8080/CentralChat_No_Auth/message/view/to/";

        @Override
        protected ArrayList<List<String>> doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            InputStream in = null;
            HttpURLConnection httpUrlConnection = null;
            ArrayList<List<String>> resultArray = null;
            try {
                Uri builtUri = Uri.parse(CHAT_BASE_URL);//.buildUpon()
                //.appendQueryParameter("q", params[0]) // city
                //.appendQueryParameter("mode", "json") // json format as result
                //.appendQueryParameter("units", "metric") // metric unit
                //.appendQueryParameter("cnt", "1")      // 1 day forecast
                //.appendQueryParameter("type", "accurate")
                //.build();

                URL url = new URL(builtUri.toString());
                httpUrlConnection = (HttpURLConnection) url.openConnection();
                in = new BufferedInputStream(
                        httpUrlConnection.getInputStream());
                String data = readStream(in);
                resultArray = JSONMessageData.getData(data);

            } catch (MalformedURLException exception) {
                Log.e(TAG, "MalformedURLException");
            } catch (IOException exception) {
                Log.e(TAG, "IOException");
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage(), e);
                e.printStackTrace();
            } finally {
                if (null != httpUrlConnection) {
                    httpUrlConnection.disconnect();
                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (final IOException e) {
                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }


            Toast.makeText(getActivity(),
                    "Data Received! Process it now! \n" + resultArray.toString(),
                    Toast.LENGTH_SHORT).show();

            //setMessageList(resultArray);

            return resultArray;
        }

        @Override
        protected void onPostExecute(ArrayList<List<String>> result) {


            if (result == null) {
                Toast.makeText(getActivity(),
                        "Something went wrong in the server-client communication!",
                        Toast.LENGTH_SHORT).show();
                return;
            }


            Toast.makeText(getActivity(),
                    "Data Received! Process it now! \n" + result.toString(),
                    Toast.LENGTH_SHORT).show();
        }
    }





        private class HttpPostTask extends AsyncTask<String, Void, ArrayList<List<String>>> {


            private static final String TAG = "HttpGetTask";

            final String CHAT_BASE_URL =
                    "http://104.236.65.167:8080/CentralChat_No_Auth/message/send/";

            @Override
            protected ArrayList<List<String>> doInBackground(String... params) {
                // These two need to be declared outside the try/catch
                // so that they can be closed in the finally block.
                InputStream in = null;
                HttpURLConnection conn = null;
                ArrayList<List<String>> resultArray = null;
                try {

                    String builtUri = CHAT_BASE_URL + URLEncoder.encode(params[0].trim()) + "/" + URLEncoder.encode(params[1].trim())
                            + "/" + URLEncoder.encode(params[2].trim()) + "/" + URLEncoder.encode(params[3].trim());


                    //URL url = new URL(builtUri.toString());
                    //httpUrlConnection = (HttpURLConnection) url.openConnection();
                    //in = new BufferedInputStream(
                    //        httpUrlConnection.getInputStream());
                    //String data = readStream(in);

                    URL url = new URL(builtUri);

                    //URL url = new URL(builtUri.toString());
                    conn = (HttpURLConnection) url.openConnection();

                    //httpUrlConnection.connect();


                    //conn.setRequestProperty("Content-type", "application/json");
                    conn.setRequestProperty("User-agent", "Fiddler");
                    conn.setRequestProperty("Host", "104.236.65.167:8080");
                    //conn.setRequestProperty("Authorization", "4321");
                    //conn.setRequestProperty("charset", "utf-8");
                    conn.setRequestMethod("GET");
                    conn.setDoInput(false);
                    conn.setDoOutput(false);
                    conn.connect();



                    int responseCode = conn.getResponseCode();
                    String message=conn.getResponseMessage();
                    //
                    System.out.println(message);




                    //resultArray = JSONMessageData.getData(data);

                } catch (MalformedURLException exception) {
                    Log.e(TAG, "MalformedURLException");
                } catch (IOException exception) {
                    Log.e(TAG, "IOException");
                }
                  catch(Exception ex)
                  {
                      Log.e(TAG, "Plain exception");
                  }
                finally {
                    if (null != conn) {
                        conn.disconnect();
                    }
                    if (in != null) {
                        try {
                            in.close();
                        } catch (final IOException e) {
                            Log.e(TAG, "Error closing stream", e);
                        }
                    }
                }

                return null;
            }


            @Override
            protected void onPostExecute(ArrayList<List<String>> result) {


                Toast.makeText(getActivity(),
                        "Data sent!" +
                                "",
                        Toast.LENGTH_SHORT).show();

                return;
            }
        }


        private String readStream(InputStream in) {
            BufferedReader reader = null;
            StringBuffer data = new StringBuffer("");
            try {
                reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    data.append(line);
                }
            } catch (IOException e) {
                Log.e("HttpGetTask", "IOException");
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return data.toString();
        }
    
}
