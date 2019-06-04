import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.Timestamp;
public class MSGDB {
	private int id;
	private String senderid;
	private String reveiverid;
	private String time;
	private String text;
	/* two options
	/  1. Text
	/  2. Pic
	*/
	private String type;

	public void setID(int id) {
		this.id = id;
	}
	
	public int getID() {
		return this.id;
	}
	
	public void setSenderID(String senderid) {
		this.senderid = senderid;
	}

	public String getSenderID() {
		return this.senderid;
	}

	public void setReceiverID(String receiverid) {
		this.reveiverid = receiverid;
	}

	public String getReceiverID() {
		return this.reveiverid;
	}

	public void setTime(long time) {
		this.time = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date(time));
	}

	public String getTime() {
		return this.time;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return this.text;
	}

	public void setType(String type) { 
		this.type = type;
	}

	public String getType() {
		return this.type;
	}
}
