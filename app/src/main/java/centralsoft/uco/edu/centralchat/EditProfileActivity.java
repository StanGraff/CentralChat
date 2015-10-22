package centralsoft.uco.edu.centralchat;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView image;
    private TextView nickname;
    private Button saveBtn;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    SharedPreferencesProcessing sharedPreferencesProcessing = new SharedPreferencesProcessing();
    Utils utils = new Utils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        image = (ImageView) findViewById(R.id.userIconEdit);
        nickname = (TextView) findViewById(R.id.displayNameEdit);
        saveBtn = (Button) findViewById(R.id.saveButton);


        //if (sharedPreferencesProcessing.retrieveNickname(MainActivity.this) != null)
        //{
        //    nickname.setText(sharedPreferencesProcessing.retrieveNickname(MainActivity.this));

        //    Intent intent = new Intent(MainActivity.this, ChatActivity.class);
        //    startActivity(intent);

        //}

        if (sharedPreferencesProcessing.retrieveNickname(EditProfileActivity.this) != null)
        {
            nickname.setText(sharedPreferencesProcessing.retrieveNickname(EditProfileActivity.this));

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

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (nickname.getText().toString().equals("")) {
                    Toast.makeText(EditProfileActivity.this, "Please Input the Display Name", Toast.LENGTH_SHORT).show();
                }
                else if (sharedPreferencesProcessing.retrieveImage(EditProfileActivity.this) == null)
                {
                    Toast.makeText(EditProfileActivity.this, "Please Select an Image", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    sharedPreferencesProcessing.storeNickname(EditProfileActivity.this, nickname.getText().toString());
                    //Intent intent = new Intent(EditProfileActivity.this, ChatActivity.class);
                    //startActivity(intent);

                    Toast.makeText(EditProfileActivity.this, "Your settings were successfully updated, " + sharedPreferencesProcessing.retrieveNickname(EditProfileActivity.this), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.chat_rooms) {
            Intent chatRoomsIntent = new Intent(this, ChatRoomsActivity.class);
            startActivity(chatRoomsIntent);
            return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
        */
        return true;
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
