package centralsoft.uco.edu.centralchat;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stanislav on 11/2/2015.
 */
public class JSONMessageData {

    private static final String TAG = "JSONMessageData";

    public static ArrayList<List<String>> getData(String chatMessageJsonStr) throws JSONException
    {
        try
        {

            JSONObject chatJson = new JSONObject(chatMessageJsonStr);

            JSONObject msgObj = chatJson.getJSONObject("message");
            JSONArray messageArray = msgObj.getJSONArray("messages");

            ArrayList<List<String>> result = new ArrayList<>();
            //ArrayList<String> singleList = new ArrayList<>();

            for (int i=0; i < messageArray.length(); i++)
            {
                ArrayList<String> singleList = new ArrayList<>();

                JSONObject singleMessage = messageArray.getJSONObject(i);

                //JSONObject messageObject =
                //        singleMessage.getJSONArray("messages").getJSONObject(0);
                //int id = messageObject.getInt("id"); // id
                //String message = messageObject.getString("message");
                //String to = messageObject.getString("to");
                //String from = messageObject.getString("from");

                int id = singleMessage.getInt("id"); // id
                String message = singleMessage.getString("message");
                String to = singleMessage.getString("to");
                String from = singleMessage.getString("from");


                singleList.add(id + "");
                singleList.add(message);
                singleList.add(to);
                singleList.add(from);
                result.add(singleList);
            }

            return result;
        }catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

}
