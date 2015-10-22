package centralsoft.uco.edu.centralchat;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private ImageView image;
    private TextView nickname;
    private Button submitBtn;

    static final int REQUEST_IMAGE_CAPTURE = 1;

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

        if (sharedPreferencesProcessing.retrieveNickname(MainActivity.this) != null)
        {
            nickname.setText(sharedPreferencesProcessing.retrieveNickname(MainActivity.this));

        }



        if (sharedPreferencesProcessing.retrieveImage(this) != null)
        {
            //image.setImageBitmap(sharedPreferencesProcessing.retrieveImage(this));
            image.setImageBitmap(utils.getRoundedShape(sharedPreferencesProcessing.retrieveImage(this)));
        }
        else
        {
            image.setImageResource(R.drawable.user_icon1);
        }

        image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        submitBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (nickname.getText().toString().equals("")) {
                    Toast.makeText(MainActivity.this, "Please Input the Display Name", Toast.LENGTH_SHORT).show();
                }
                else if (sharedPreferencesProcessing.retrieveImage(MainActivity.this) == null)
                {
                    Toast.makeText(MainActivity.this, "Please Select an Image", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    sharedPreferencesProcessing.storeNickname(MainActivity.this, nickname.getText().toString());
                    Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                    startActivity(intent);

                    //Authenticate with the server

                    Toast.makeText(MainActivity.this, "Chat Activity Ready, " + sharedPreferencesProcessing.retrieveNickname(MainActivity.this), Toast.LENGTH_SHORT).show();
                    Toast.makeText(MainActivity.this, "Device MAC: " + utils.getDeviceMacAddress(MainActivity.this), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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



}
