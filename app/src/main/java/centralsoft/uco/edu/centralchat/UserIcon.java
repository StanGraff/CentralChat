package centralsoft.uco.edu.centralchat;

/**
 * Created by Justin on 11/9/15.
 */
public class UserIcon {
    private String icon;
    private String userID;

    public UserIcon(String userID, String icon){
        this.userID = userID;
        this.icon = icon;
    }

    public void setUserID(String userID){
        this.userID = userID;
    }

    public void setIcon(String icon){
        this.icon = icon;
    }

    public String getUserID(){
        return userID;
    }

    public String getIcon(){
        return icon;
    }
}
