package centralsoft.uco.edu.centralchat;

import android.graphics.Bitmap;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Justin on 10/28/15.
 */
public class Message {
    private String msgFrom;
    private String msg;
    private boolean isMyMsg;
    private Bitmap img;

    public Message() {

    }

    public Message(String msgFrom, String msg, boolean isMyMsg, Bitmap img) {
        this.msgFrom = msgFrom;
        this.msg = msg;
        this.isMyMsg = isMyMsg;
        this.img = img;
    }

    public void setMsgFrom(String msgFrom) {
        this.msgFrom = msgFrom;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setIsMyMsg(boolean isMyMsg) {
        this.isMyMsg = isMyMsg;
    }

    public String getMsgFrom() {
        return msgFrom;
    }

    public String getMsg() {
        return msg;
    }

    public boolean isMyMsg() {
        return isMyMsg;
    }

    public void setImg(Bitmap img) {
        this.img = img;
    }

    public Bitmap getImg() {
        return img;
    }


    public String getDate() {
        //This gives the current date and time as numeric values formatted as shown below .
        Calendar calendar = Calendar.getInstance();
        String correct = "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");
        String currentDate = sdf.format(calendar.getTime());
        //This gives the current day as a string. Ex: Monday, Tuesday, etc.
        SimpleDateFormat sdf_ = new SimpleDateFormat("EEEE");
        Date date = new Date();
        String dayName = sdf_.format(date);
        correct = ("" + dayName + " " + currentDate + "");
        return correct;
    }
}
