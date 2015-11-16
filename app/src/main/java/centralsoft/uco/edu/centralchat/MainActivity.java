package centralsoft.uco.edu.centralchat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ImageView image;
    private TextView nickname;
    private Button submitBtn, camera, device;
    private AlertDialog alert;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int SELECT_PICTURE_ACTIVITY_REQUEST_CODE = 0;

    GoogleCloudMessaging gcmObj;
    String regId = "";
    ProgressDialog prgDialog;
    RequestParams params = new RequestParams();
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    AsyncTask<Void, Void, String> createRegIdTask;

    public static final String REG_ID = "regId";
    public static final String MAC_ID = "MacId";

    SharedPreferencesProcessing sharedPreferencesProcessing = new SharedPreferencesProcessing();
    Utils utils = new Utils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        image = (ImageView) findViewById(R.id.userIcon);
        nickname = (TextView) findViewById(R.id.displayName);
        submitBtn = (Button) findViewById(R.id.submit);


        //if (sharedPreferencesProcessing.retrieveNickname(MainActivity.this) != null)
        //{
        //    nickname.setText(sharedPreferencesProcessing.retrieveNickname(MainActivity.this));

        //    Intent intent = new Intent(MainActivity.this, ChatActivity.class);
        //    startActivity(intent);

        //}

        prgDialog = new ProgressDialog(this);
        // Set Progress Dialog Text
        prgDialog.setMessage("Please wait...");
        // Set Cancelable as False
        prgDialog.setCancelable(false);

        if (sharedPreferencesProcessing.retrieveNickname(MainActivity.this) != null) {
            nickname.setText(sharedPreferencesProcessing.retrieveNickname(MainActivity.this));

        }


        if (sharedPreferencesProcessing.retrieveImage(this) != null) {
            //image.setImageBitmap(sharedPreferencesProcessing.retrieveImage(this));
            image.setImageBitmap(utils.getRoundedShape(sharedPreferencesProcessing.retrieveImage(this)));
        } else {
            image.setImageResource(R.drawable.user_icon1);
        }

        image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                askPicture();
            }
        });

        submitBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (nickname.getText().toString().equals("")) {
                    Toast.makeText(MainActivity.this, "Please Input the Display Name", Toast.LENGTH_SHORT).show();
                } else if (sharedPreferencesProcessing.retrieveImage(MainActivity.this) == null) {
                    Toast.makeText(MainActivity.this, "Please Select an Image", Toast.LENGTH_SHORT).show();
                } else {
                    sharedPreferencesProcessing.storeNickname(MainActivity.this, nickname.getText().toString());

                    if (!TextUtils.isEmpty(getSharedPreferences("UserDetails",
                            Context.MODE_PRIVATE).getString(REG_ID, ""))) {
                        Intent intent = new Intent(MainActivity.this, ShowChat.class);

                        startActivity(intent);
                        finish();
                    }
                    else {
                        //Authenticate with the server
                        if (checkPlayServices()) {
                            // Register Device in GCM Server
                            registerInBackground(utils.getDeviceMacAddress(MainActivity.this));
                        }
                    }

                    //Toast.makeText(MainActivity.this, "Chat Activity Ready, " + sharedPreferencesProcessing.retrieveNickname(MainActivity.this), Toast.LENGTH_SHORT).show();
                    //Toast.makeText(MainActivity.this, "Device MAC: " + utils.getDeviceMacAddress(MainActivity.this), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        if (sharedPreferencesProcessing.retrieveImage(this) != null) {
            //image.setImageBitmap(sharedPreferencesProcessing.retrieveImage(this));
            image.setImageBitmap(utils.getRoundedShape(sharedPreferencesProcessing.retrieveImage(this)));
        } else {
            image.setImageResource(R.drawable.user_icon1);
        }
        if (sharedPreferencesProcessing.retrieveNickname(MainActivity.this) != null) {
            nickname.setText(sharedPreferencesProcessing.retrieveNickname(MainActivity.this));

        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_PICTURE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            Bitmap pictureObject = BitmapFactory.decodeFile(picturePath);

            image.setImageBitmap(utils.getRoundedShape(pictureObject));

            sharedPreferencesProcessing.storeImage(this, pictureObject);
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            image.setImageBitmap(utils.getRoundedShape(imageBitmap));

            sharedPreferencesProcessing.storeImage(this, imageBitmap);
        }

        /** this is temp code for simulating message*/
        if(requestCode == 3 && resultCode == RESULT_OK && null != data){
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            Bitmap pictureObject = BitmapFactory.decodeFile(picturePath);

            ArrayList<UserIcon> iconList = new ArrayList<UserIcon>();
            if(sharedPreferencesProcessing.getIcons(this) != null && sharedPreferencesProcessing.getIcons(this).size() > 0){
                iconList = sharedPreferencesProcessing.getIcons(this);
                for(int i = 0; i < iconList.size(); i++){
                    if(iconList.get(i).getUserID() == "Simulated"){
                        iconList.get(i).setIcon(sharedPreferencesProcessing.storeMessageImage(pictureObject));
                    }
                }
                iconList.add(new UserIcon("Simulated", sharedPreferencesProcessing.storeMessageImage(pictureObject)));
            }else{
                iconList.add(new UserIcon("Simulated", sharedPreferencesProcessing.storeMessageImage(pictureObject)));
            }
            sharedPreferencesProcessing.storeIcons(iconList, this);

            //sharedPreferencesProcessing.storeSimulatedImage(this, pictureObject, "Simulated");
        }

        if(requestCode == 4 && resultCode == RESULT_OK && null != data){
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            Bitmap pictureObject = BitmapFactory.decodeFile(picturePath);

            ArrayList<UserIcon> iconList = new ArrayList<UserIcon>();
            if(sharedPreferencesProcessing.getIcons(this) != null && sharedPreferencesProcessing.getIcons(this).size() > 0){
                iconList = sharedPreferencesProcessing.getIcons(this);
                for(int i = 0; i < iconList.size(); i++){
                    if(iconList.get(i).getUserID() == "Simulated2"){
                        iconList.get(i).setIcon(sharedPreferencesProcessing.storeMessageImage(pictureObject));
                    }
                }
                iconList.add(new UserIcon("Simulated2", sharedPreferencesProcessing.storeMessageImage(pictureObject)));
            }else{
                iconList.add(new UserIcon("Simulated2", sharedPreferencesProcessing.storeMessageImage(pictureObject)));
            }
            sharedPreferencesProcessing.storeIcons(iconList, this);
            //sharedPreferencesProcessing.storeSimulatedImage(this, pictureObject, "Simulated2");
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void askPicture() {

        final LayoutInflater inflater = getLayoutInflater();
        final View dialogueLayout = inflater.inflate(R.layout.ask_picture, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogueLayout);


        camera = (Button) dialogueLayout.findViewById(R.id.camera);
        device = (Button) dialogueLayout.findViewById(R.id.device);

        camera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispatchTakePictureIntent();
                if (MainActivity.this.alert != null) {
                    MainActivity.this.alert.dismiss();

                }

            }
        });

        device.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getPictureFromMemoryIntent();
                if (MainActivity.this.alert != null) {
                    MainActivity.this.alert.dismiss();
                }

            }
        });
        alert = builder.create();

        alert.show();
    }

    private void getPictureFromMemoryIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_PICTURE_ACTIVITY_REQUEST_CODE);
    }

    /** following can be removed once simulate message is not needed */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add_image) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 3);
            return super.onOptionsItemSelected(item);
        } else if(id == R.id.add_image2) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 4);
            return super.onOptionsItemSelected(item);
        } else return true;
    }

    //GCM related routines
    // AsyncTask to register Device in GCM Server
    private void registerInBackground(final String macID) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcmObj == null) {
                        gcmObj = GoogleCloudMessaging
                                .getInstance(MainActivity.this);
                    }
                    regId = gcmObj
                            .register(GcmConstants.GOOGLE_PROJ_ID);
                    msg = "Registration ID :" + regId;

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                if (!TextUtils.isEmpty(regId)) {
                    // Store RegId created by GCM Server in SharedPref
                    storeRegIdInSharedPref(MainActivity.this, regId, macID);
                    //Toast.makeText(
                    //        MainActivity.this,
                    //        "Registered with GCM Server successfully.\n\n"
                    //                + msg, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(
                            MainActivity.this,
                            "Reg ID Creation Failed.\n\nEither you haven't enabled Internet or GCM server is busy right now. Make sure you enabled Internet and try registering again after some time."
                                    + msg, Toast.LENGTH_LONG).show();
                }
            }
        }.execute(null, null, null);
    }

    // Store  RegId and Email entered by User in SharedPref
    private void storeRegIdInSharedPref(Context context, String regId,
                                        String macID) {
        SharedPreferences prefs = getSharedPreferences("UserDetails",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(REG_ID, regId);
        editor.putString(MAC_ID, macID);
        editor.commit();
        storeRegIdInServer();

    }

    // Share RegID with GCM Server
    private void storeRegIdInServer() {
        prgDialog.show();
        params.put("regId", regId);
        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(GcmConstants.APP_SERVER_URL, params,
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
                        //Toast.makeText(MainActivity.this,
                        //        "Reg Id shared successfully with Web App ",
                        //        Toast.LENGTH_LONG).show();
                        //Intent i = new Intent(applicationContext,
                        Intent i = new Intent(MainActivity.this,
                                ShowChat.class);
                        i.putExtra("regId", regId);
                        startActivity(i);
                        finish();
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
                            Toast.makeText(MainActivity.this,
                                    "Requested resource not found",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code is '500'
                        else if (statusCode == 500) {
                            Toast.makeText(MainActivity.this,
                                    "Something went wrong at server end",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code other than 404, 500
                        else {
                            Toast.makeText(
                                    MainActivity.this,
                                    "Unexpected Error occcured! [Most common Error: Device might "
                                            + "not be connected to Internet or remote server is not up and running], check for other errors as well",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    // Check if Google PlayServices is installed in Device or not
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        // When Play services not found in device
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                // Show Error dialog to install Play services
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(
                        MainActivity.this,
                        "This device doesn't support Play services, App will not work normally",
                        Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        } else {
            Toast.makeText(
                    MainActivity.this,
                    "This device supports Play services, App will work normally",
                    Toast.LENGTH_LONG).show();
        }
        return true;
    }

}
