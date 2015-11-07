package centralsoft.uco.edu.centralchat;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stanislav on 10/20/2015.
 */
public class SharedPreferencesProcessing {

    private static final String FLAG_MESSAGE = "message";

    public void storeImage(Context ct, Bitmap img) {
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

    public Bitmap retrieveImage(Context ct) {
        SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(ct);
        String previouslyEncodedImage = shre.getString("image_data", "");

        if (!previouslyEncodedImage.equalsIgnoreCase("")) {
            byte[] b = Base64.decode(previouslyEncodedImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            //imageConvertResult.setImageBitmap(bitmap);
            return bitmap;
        }
        return null;
    }

    public void storeNickname(Context ct, String nickname) {
        SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(ct);
        SharedPreferences.Editor edit = shre.edit();
        edit.putString("user_nickname", nickname);
        edit.commit();

    }

    public String retrieveNickname(Context ct) {
        SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(ct);
        try {
            String nickname = shre.getString("user_nickname", "");
            return nickname;
        } catch (Exception ex) {
            return null;
        }
    }

    public String storeMessageImage(Bitmap img) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encodedImage;
    }

    public Bitmap getMessageImage(String encodedImage) {
        if (!encodedImage.equalsIgnoreCase("")) {
            byte[] b = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            //imageConvertResult.setImageBitmap(bitmap);
            return bitmap;
        }
        return null;
    }


    public void storeChat(ArrayList<Message> chat, Context ct) {
        SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(ct);
        SharedPreferences.Editor edit = shre.edit();
        Gson gson = new Gson();
        String json = gson.toJson(chat);
        edit.putString("chatMessages", json);
        edit.commit();
    }

    public ArrayList getChat(Context ct) {
        SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(ct);
        Gson gson = new Gson();
        String json = shre.getString("chatMessages", "");
        Type type = new TypeToken<List<Message>>() {
        }.getType();
        ArrayList<Message> messages = gson.fromJson(json, type);
        return messages;
    }

    public void storeSessionId(Context ct, String sessionId) {
        SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(ct);
        SharedPreferences.Editor edit = shre.edit();
        edit.putString("sessionId", sessionId);
        edit.commit();

    }

    public String getSessionId(Context ct) {

        SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(ct);
        try {
            String sessionId = shre.getString("sessionId", "");
            return sessionId;
        } catch (Exception ex) {
            return null;
        }

    }

    public String getSendMessageJSON(Context ct, String from, String to, String message) {
        String json = null;

        try {
            JSONObject jObj = new JSONObject();
            //jObj.put("flag", FLAG_MESSAGE);
            jObj.put("sessionId", getSessionId(ct));
            jObj.put("from", from);
            jObj.put("to", to);
            jObj.put("message", message);

            json = jObj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }


}
