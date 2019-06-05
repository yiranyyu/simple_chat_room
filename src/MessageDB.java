import java.text.SimpleDateFormat;
import java.util.Date;

public class MessageDB {
    private int id;
    private String senderID;
    private String receiverID;
    private String time;
    private String text;
    /* two options
    /  1. Text
    /  2. Pic
    */
    private String type;

    public int getID() {
        return this.id;
    }

    void setID(int id) {
        this.id = id;
    }

    public String getSenderID() {
        return this.senderID;
    }

    void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getReceiverID() {
        return this.receiverID;
    }

    void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public String getTime() {
        return this.time;
    }

    void setTime(long time) {
        this.time = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date(time));
    }

    public String getText() {
        return this.text;
    }

    void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return this.type;
    }

    void setType(String type) {
        this.type = type;
    }
}
