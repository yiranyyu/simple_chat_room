/**
 * 这个类与数据库中的Msg表的表项对应
 *
 * @author 岑少锋
 */

public class Message {
    private String sender;
    private String receiver;
    private String time;
    private String text;

    public Message(String sender, String receiver, String time, String text) {
        this.sender = sender;
        this.receiver = receiver;
        this.time = time;
        this.text = text;
    }

    /**
     * This method returns the username of receiver
     * @return the username of receiver
     */

    public String getReceiver() {
        return receiver;
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
     * This method returns the username of sender
     * @return
     */
    public String getSender() {
        return sender;
    }

    /**
     * This method returns the value of text
     *
     * @return The value of test.
     */
    public String getText() {
        return this.text;
    }

    /**
     * This method set the value of text
     *
     * @param text The value of text
     */
    public void setText(String text) {
        this.text = text;
    }

}
