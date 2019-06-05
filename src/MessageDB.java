public class MessageDB {
	private int id;
	private int senderid;
	private int receiverid;
	private String time;
	private String text;

	public MessageDB(int id,int senderid,int receiverid,String time,String text) {
		this.id = id;
		this.senderid = senderid;
		this.receiverid = receiverid;
		this.time = time;
		this.text = text;
	}

	public void setID(int id) {
		this.id = id;
	}
	
	public int getID() {
		return this.id;
	}
	
	public void setSenderID(int senderid) {
		this.senderid = senderid;
	}

	public int getSenderID() {
		return this.senderid;
	}

	public void setReceiverID(int receiverid) {
		this.receiverid = receiverid;
	}

	public int getReceiverID() {
		return this.receiverid;
	}

	public void setTime(String time) {
		this.time = time;
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

}
