import java.sql.*;
import java.util.*;
import net.sf.json.*;

public class ProcessSQL {
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private static final String DB_URL = "jdbc:mysql://47.93.23.141/test";

	private static final String USER = "faraway";
	private static final String PASS = "123";

	private Connection conn = null;
	private Statement stmt = null;

	public ProcessSQL() {
		try {
			Class.forName(JDBC_DRIVER);

			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL,USER,PASS);

			System.out.println("Creating statement...");
			stmt = conn.createStatement();

		} catch(SQLException se) {
			se.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public boolean ContainsName(String username) {
		boolean flag = true;
		try {
			String sql = "SELECT * FROM User WHERE username = " + username;
			ResultSet rs = stmt.executeQuery(sql);
			flag = rs.next();
			rs.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return flag;
	}

	private void ExecuteSqlAndBuildJsonArray(String sql, JSONArray jsonArray) {
		try {
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()){
				int id = rs.getInt("id");
				String senderid = rs.getString("senderid");
				String receiverid = rs.getString("receiverid");
				long time = rs.getLong("time");
				String text = rs.getString("text");
				String type = rs.getString("type");
				
				MSGDB msg = new MSGDB();
				msg.setID(id);
				msg.setSenderID(senderid);
				msg.setReceiverID(receiverid);
				msg.setTime(time);
				msg.setText(text);
				msg.setType(type);
				
				JSONObject jsonObject = JSONObject.fromObject(msg);
				jsonArray.add(jsonObject);
			}
			rs.close();
		} catch (SQLException e) {
		}
	}
	public JSONArray PullMsg(String sender, String receiver) {
		JSONArray jsonArray = new JSONArray();
		String sql = "";
		if (receiver == null) 
			sql = "SELECT * FROM MSG WHERE senderid = " + sender + " OR receiverid = " + sender;
		else 
			sql = "SELECT * FROM MSG WHERE (senderid = " + sender + " AND receiver = " + receiver + ") OR (senderid = " + receiver + " AND receiver = " + sender + ")";
		this.ExecuteSqlAndBuildJsonArray(sql, jsonArray);
		return jsonArray;
	}

	public JSONArray Login(String username, String password) 
			throws UserNotExistsException, PasswordErrorException {
		try {
			String sql = "SELECT password from USER WHERE username = " + username;
			ResultSet rs = stmt.executeQuery(sql);
			if(rs.next()) {
				String pw = rs.getString("password");
				if(password.equals(pw) == true) {
					return this.PullMsg(username, null);
				} else 
					throw new PasswordErrorException(username, password);
			} else 
				throw new UserNotExistsException(username);
		} catch(SQLException e) {
		}
		return null;
	}

	public void SignUp(String username, String password) 
			throws UserAlreadyExistsException, DatabaseInsertFailException {
		if(this.ContainsName(username) == true) 
			throw new UserAlreadyExistsException(username);
		else {
			if (this.InsertUser(username, password) == false) 
				throw new DatabaseInsertFailException();
		}
	}

	private boolean InsertUser(String username, String password) {
		try {
			String sql = "INSERT INTO USER VALUES(\""+username+"\", \"" + password + "\")";
			int rs = stmt.executeUpdate(sql);
			if(rs > 0)
				return true;
			else
				return false;
		} catch(SQLException e) {

		}
		return true;
	}

	void close() {
		try {
			stmt.close();
			conn.close();
		} catch(SQLException se) {
			se.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try{
				if(stmt!=null)
					stmt.close();
			}catch(SQLException se2){
			}
			try{
				if(conn!=null)
					conn.close();
			}catch(SQLException se){
				se.printStackTrace();
			}
		}
	}
}
