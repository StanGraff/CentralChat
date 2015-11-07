package centralsoft.uco.edu.centralchat;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageView image;
    private TextView nickname;
    private Button submitBtn, camera, device;
    private AlertDialog alert;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int SELECT_PICTURE_ACTIVITY_REQUEST_CODE = 0;

    public boolean authenticated = false;

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

                //new HttpAuthenticateTask().execute("getAuthentication");

                if (nickname.getText().toString().equals("")) {
                    Toast.makeText(MainActivity.this, "Please Input the Display Name", Toast.LENGTH_SHORT).show();
                } else if (sharedPreferencesProcessing.retrieveImage(MainActivity.this) == null) {
                    Toast.makeText(MainActivity.this, "Please Select an Image", Toast.LENGTH_SHORT).show();
                //} else if (authenticated)
                //{
                //    Toast.makeText(MainActivity.this, "Chat Server Authentication Failed", Toast.LENGTH_SHORT).show();
                }
                else {
                    sharedPreferencesProcessing.storeNickname(MainActivity.this, nickname.getText().toString());
                    Intent intent = new Intent(MainActivity.this, ShowChat.class);

                    startActivity(intent);
                    finish();
                    //Authenticate with the server
                    //new HttpAuthenticateTask().execute("gettingMessages");

                    Toast.makeText(MainActivity.this, "Chat Activity Ready, " + sharedPreferencesProcessing.retrieveNickname(MainActivity.this), Toast.LENGTH_SHORT).show();
                    Toast.makeText(MainActivity.this, "Device MAC: " + utils.getDeviceMacAddress(MainActivity.this), Toast.LENGTH_LONG).show();
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

    private class HttpAuthenticateTask extends AsyncTask<String, Void, Boolean> {


        private static final String TAG = "HttpGetTask";

        // Construct the URL for the OpenWeatherMap query
        // Possible parameters are avaiable at OWM's forecast API page, at
        // http://openweathermap.org/API#forecast
        final String CHAT_BASE_URL =
                "http://104.236.65.167:8080/ChatCentral/message/authenticate";

        @Override
        protected Boolean doInBackground(String... params) {

            InputStream in = null;
            HttpURLConnection httpUrlConnection = null;
            ArrayList<List<String>> resultArray = null;
            try {

                String builtUri = CHAT_BASE_URL + params[0];

                URL url = new URL(builtUri.toString());

                HttpURLConnection conn = (HttpURLConnection) url
                        .openConnection();
                conn.setConnectTimeout(30000);
                conn.setReadTimeout(30000);
                conn.setInstanceFollowRedirects(true);
                InputStream is = conn.getInputStream();


                //HttpURLConnection con = (HttpURLConnection) url.openConnection();
                //con.connect();

                if (!is.toString().equals("200"))
                {
                    return false;
                }
                else return true;

               //if (con.getResponseCode() != 200) {
               //     return false;
               // }
               // else return true;


            } catch (MalformedURLException exception) {
                Log.e(TAG, "MalformedURLException");
            } catch (IOException exception) {
                Log.e(TAG, "IOException");
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

            return false;
        }


        @Override
        protected void onPostExecute(Boolean result) {

/*
            if (result == false) {
                Toast.makeText(MainActivity.this,
                        "Oooops! Something went wrong in the server-client communication!",
                        Toast.LENGTH_SHORT).show();
                return;
            }


            if (result == true) {
                Toast.makeText(MainActivity.this,
                        "Connection successful!",
                        Toast.LENGTH_SHORT).show();
                return;
            }
*/
        }

    }


}
