package centralsoft.uco.edu.centralchat;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by Stanislav on 10/20/2015.
 */
public class SharedPreferencesProcessing {

    public void storeImage(Context ct, Bitmap img){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();

        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        //textEncode.setText(encodedImage);

        SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(ct);
        SharedPreferences.Editor edit = shre.edit();
        edit.putString("image_data", encodedImage);
        edit.commit();

    }

    public Bitmap retrieveImage(Context ct)
    {
        SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(ct);
        String previouslyEncodedImage = shre.getString("image_data", "");

        if( !previouslyEncodedImage.equalsIgnoreCase("") ){
            byte[] b = Base64.decode(previouslyEncodedImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            //imageConvertResult.setImageBitmap(bitmap);
            return bitmap;
        }
        return null;
    }

    public void storeNickname(Context ct, String nickname){
        SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(ct);
        SharedPreferences.Editor edit = shre.edit();
        edit.putString("user_nickname", nickname);
        edit.commit();

    }

    public String retrieveNickname(Context ct)
    {
        SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(ct);
        try
        {
            String nickname = shre.getString("user_nickname", "");
            return nickname;
        }catch(Exception ex)
        {
            return null;
        }
    }

    public void storeSessionIdNumber(Context ct)
    {

    }


}
