import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.Date;
import java.sql.*;


public class ProcessSQL {
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private static final String DB_URL = "jdbc:mysql://39.105.80.32/ChatDB";

	private static final String USER = "root";
	private static final String PASS = "123456Abc!hhh";

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
            String sql = String.format("SELECT * FROM User WHERE username = '%s'", username);
			ResultSet rs = stmt.executeQuery(sql);
			flag = rs.next();
			rs.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return flag;
	}

	private JSONArray ExecuteSqlAndBuildJsonArray(String sql) {
		JSONArray jsonArray = new JSONArray();
		try {
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()){
				MessageDB msg = new MessageDB(rs.getInt("id"),
									  		  rs.getInt("sender"),
									  		  rs.getInt("receiver"),
									  		  rs.getString("time"),
									  		  rs.getString("content"));
				
				JSONObject jsonObject = JSONObject.fromObject(msg);
				jsonArray.add(jsonObject);
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return jsonArray;
	}
	
	public JSONArray PullMsg(String sender, String receiver) {
		int senderid = this.findIdByusername(sender);
		int receiverid = this.findIdByusername(receiver);
		
		String sql = "";
		if (receiver == null) 
			sql = String.format("SELECT * FROM Msg WHERE sender = %d OR receiver = %d", senderid, senderid);
		else 
			sql = String.format("SELECT * FROM Msg WHERE (sender = %d AND receiver = %d) OR (sender = %d AND receiver = %d)", senderid , receiverid, receiverid, senderid);
		return this.ExecuteSqlAndBuildJsonArray(sql);
	}
	
	public JSONArray Login(String username, String password) 
			throws UserNotExistsException, PasswordErrorException {
		try {
            String sql = String.format("SELECT password from User WHERE username='%s'", username);
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
			e.printStackTrace();
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

	public boolean SendMsg(String sendername, String receivername, String content) 
			throws UserNotExistsException {
		int senderid = this.findIdByusername(sendername);
		int receiverid = this.findIdByusername(receivername);
		if(senderid == -1)
			throw new UserNotExistsException(sendername);
		if(receiverid == -1)
			throw new UserNotExistsException(receivername);
		
		try {
			String sql = String.format("insert into Msg(sender,receiver,content,time) Values(%d,%d,'%s','%s')",senderid,receiverid,content,new Date());
			int rs = stmt.executeUpdate(sql);
			if(rs > 0)
				return true;
			else
				return false;
		} catch(SQLException e) {
		}
		return true;
	}
	
	private int findIdByusername(String username) {
		if (username == null)
			return -1;
		try {
			String sql = String.format("SELECT id from User WHERE username = '%s'", username);
			ResultSet rs = stmt.executeQuery(sql);
			if(rs.next())
				return rs.getInt("id");
			else 
				return -1;
		} catch (SQLException e) {
		}
		return -1;
	}

	private boolean InsertUser(String username, String password) {
		try {
			String sql = String.format("insert into User(username,password) Values('%s','%s')", username, password);
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
