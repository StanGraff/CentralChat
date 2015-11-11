package centralsoft.uco.edu.centralchat;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stanislav on 10/20/2015.
 */
public class SharedPreferencesProcessing {

    public void storeImage(Context ct, Bitmap img) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        img = resizeImageForImageView(img);
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
        img = resizeImageForImageView(img);
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


    public void storeChat(ArrayList<Message> chat, String chatID, Context ct) {
        SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(ct);
        SharedPreferences.Editor edit = shre.edit();
        Gson gson = new Gson();
        String json = gson.toJson(chat);
        edit.putString(chatID, json);
        edit.commit();
    }

    public void storeIcons(ArrayList<UserIcon> icons, Context ct){
        SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(ct);
        SharedPreferences.Editor edit = shre.edit();
        Gson gson = new Gson();
        String json = gson.toJson(icons);
        edit.putString("userIcons", json);
        edit.commit();
    }

    public ArrayList getChat(String chatID, Context ct) {
        SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(ct);
        Gson gson = new Gson();
        String json = shre.getString(chatID, "");
        if(json.equals("")){
            return null;
        }
        Type type = new TypeToken<List<Message>>() {
        }.getType();
        ArrayList<Message> messages;
        try{
            messages = gson.fromJson(json, type);
            return messages;
        }catch(JsonSyntaxException e){
            return null;
        }
    }

    public ArrayList getIcons(Context ct){
        SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(ct);
        Gson gson = new Gson();
        String json = shre.getString("userIcons", "");
        if(json.equals("")){
            return null;
        }
        Type type = new TypeToken<List<UserIcon>>(){
        }.getType();
        ArrayList<UserIcon> icons = gson.fromJson(json, type);
        return icons;
    }

    public void setChatID(String chatID, Context ct){
        SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(ct);
        SharedPreferences.Editor edit = shre.edit();
        edit.putString("ChatID", chatID);
        edit.commit();
    }

    public String getChatID(Context ct){
        SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(ct);
        String chatID = shre.getString("ChatID", "");
        return chatID;
    }

    public void storeSessionIdNumber(Context ct) {

    }

    public Bitmap resizeImageForImageView(Bitmap bitmap) {
        Bitmap resizedBitmap = null;
        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int newWidth = -1;
        int newHeight = -1;
        float multFactor = -1.0F;
        if(originalHeight > originalWidth) {
            newHeight = 80;
            multFactor = (float) originalWidth/(float) originalHeight;
            newWidth = (int) (newHeight*multFactor);
        } else if(originalWidth > originalHeight) {
            newWidth = 80;
            multFactor = (float) originalHeight/ (float)originalWidth;
            newHeight = (int) (newWidth*multFactor);
        } else if(originalHeight == originalWidth) {
            newHeight = 80;
            newWidth = 80;
        }
        resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false);
        return resizedBitmap;
    }

//    public void storeSimulatedImage(Context ct, Bitmap img, String id) {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        img = resizeImageForImageView(img);
//        img.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//        byte[] b = baos.toByteArray();
//
//        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
//        //textEncode.setText(encodedImage);
//
//        SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(ct);
//        SharedPreferences.Editor edit = shre.edit();
//        edit.putString(id , encodedImage);
//        edit.commit();
//    }

//    public String getSimulatedIcon(String id, Context ct){
//        SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(ct);
//        String i = shre.getString(id, "");
//        return i;
//        // return shre.getString(id, "");
//    }
}
