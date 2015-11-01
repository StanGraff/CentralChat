package centralsoft.uco.edu.centralchat;

/**
 * Created by Justin on 10/28/15.
 */
public class Message {
    private String msgFrom;
    private String msg;
    private String isMyMsg;
    private String msgDate;

    public Message(String msgFrom, String msg, String isMyMsg, String msgDate) {
        this.msgFrom = msgFrom;
        this.msg = msg;
        this.isMyMsg = isMyMsg;
        this.msgDate = msgDate;
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

    public String getMsgDate() {
        return msgDate;
    }
}
