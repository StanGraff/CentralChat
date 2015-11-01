package centralsoft.uco.edu.centralchat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Justin on 10/28/15.
 */
public class Message {
    private String msgFrom;
    private String msg;
    private String isMyMsg;

    public Message(String msgFrom, String msg, String isMyMsg) {
        this.msgFrom = msgFrom;
        this.msg = msg;
        this.isMyMsg = isMyMsg;
    }

    public void setMsgFrom(String msgFrom) {
        this.msgFrom = msgFrom;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setIsMyMsg(String isMyMsg) {
        this.isMyMsg = isMyMsg;
    }

    public String getMsgFrom() {
        return msgFrom;
    }

    public String getMsg() {
        return msg;
    }

    public String isMyMsg() {
        return isMyMsg;
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
