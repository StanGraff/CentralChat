package centralsoft.uco.edu.centralchat;

/**
 * Created by Stanislav on 11/15/2015.
 */
public interface GcmConstants {

    // Php Application URL to store Reg ID created
    static final String APP_SERVER_URL = "http://204.154.117.80/~gq00c/centralChatGcm.php?shareRegId=true";
    static final String APP_SEND_MESSAGE_URL = "http://204.154.117.80/~gq00c/centralChatGcm.php?deliverMessage=true";


    // Google Project Number
    static final String GOOGLE_PROJ_ID = "953333338448";
    // Message Key
    static final String MSG_KEY = "m";
    static final String MSG_FROM = "fromUser";
    static final String MSG_TO = "toUser";
    static final String MSG = "message";

}
