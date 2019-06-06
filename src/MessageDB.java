/**
 * 这个类与数据库中的Msg表的表项对应
 *
 * @author 岑少锋
 */

public class MessageDB {
    private int id;
    private int senderid;
    private int receiverid;
    private String time;
    private String text;

    public MessageDB(int id, int senderid, int receiverid, String time, String text) {
        this.id = id;
        this.senderid = senderid;
        this.receiverid = receiverid;
        this.time = time;
        this.text = text;
    }

    /**
     * This method Return the value of ID
     *
     * @return The value of Id.
     */
    public int getID() {
        return this.id;
    }

    /**
     * This method set the value of ID
     *
     * @param id The value of message id.
     */
    public void setID(int id) {
        this.id = id;
    }

    /**
     * This method Return the value of SenderID
     *
     * @return The value of Sender Id.
     */
    public int getSenderID() {
        return this.senderid;
    }

    /**
     * This method set the value of SenderID
     *
     * @param senderid The value of Sender id.
     */
    public void setSenderID(int senderid) {
        this.senderid = senderid;
    }

    /**
     * This method Return the value of ReceiverID
     *
     * @return The value of Receiver Id.
     */
    public int getReceiverID() {
        return this.receiverid;
    }

    /**
     * This method set the value of ReceiverID
     *
     * @param receiverid The value of Receiver id.
     */
    public void setReceiverID(int receiverid) {
        this.receiverid = receiverid;
    }

    /**
     * This method Return the value of Time as String
     *
     * @return The value of time.
     */
    public String getTime() {
        return this.time;
    }

    /**
     * This method set the value of Time
     *
     * @param time The value of Time.
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * This method Return the value of text
     *
     * @return The value of test.
     */
    public String getText() {
        return this.text;
    }

    /**
     * This method set the value of text
     *
     * @param text The value of Receiver id.
     */
    public void setText(String text) {
        this.text = text;
    }

}
