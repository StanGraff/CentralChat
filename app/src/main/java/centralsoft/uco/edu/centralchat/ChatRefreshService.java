package centralsoft.uco.edu.centralchat;

/**
 * Created by Stanislav on 11/9/2015.
 */

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
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
import java.util.ArrayList;
import java.util.List;

public class ChatRefreshService extends Service {

    private static String LOG_TAG = "BoundService";
    private IBinder mBinder = new MyBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(LOG_TAG, "in onCreate");
        Toast.makeText(this, "Listening for new incoming messages!", Toast.LENGTH_LONG).show();
        // Run check on new messages
        // When new messages for the user detected
        // notify the main activity
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.v(LOG_TAG, "in onBind");
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.v(LOG_TAG, "in onRebind");
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(LOG_TAG, "in onUnbind");
        return true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.



            //new checkForMessagesTask().execute("gettingIfNewMessages");

            return START_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Stopped listening for new incoming messages!", Toast.LENGTH_LONG).show();
    }


    public class MyBinder extends Binder {
        ChatRefreshService getService() {
            return ChatRefreshService.this;
        }
    }



    private class checkForMessagesTask extends AsyncTask<String, Void, ArrayList<List<String>>> {


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


            return resultArray;
        }

        @Override
        protected void onPostExecute(ArrayList<List<String>> result) {

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
