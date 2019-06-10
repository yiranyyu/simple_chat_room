import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.sql.*;
import java.util.Date;

/**
 * 这个类用于远程连接并操作 MySQL 数据库
 *
 * @author 岑少锋
 */

public class ProcessSQL {
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://39.105.80.32/ChatDB";
    private static final String USER = "root";
    private static final String PASS = "123456Abc!hhh";
    private Connection conn = null;
    private Statement stmt = null;

    /**
     * This constructor connects mysql database
     */
    public ProcessSQL() {
        try {
            Class.forName(JDBC_DRIVER);
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Creating statement...");
            stmt = conn.createStatement();
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method judges if the username exists
     *
     * @param username username needs to be judged.
     * @return boolean.
     */
    public boolean ContainsName(String username) {
        boolean flag = true;
        try {
            String sql = String.format("SELECT * FROM User WHERE username = '%s'", username);
            ResultSet rs = stmt.executeQuery(sql);
            flag = rs.next();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * This method executes sql statement and return data organized by JSONArray
     *
     * @param sql sql statement.
     * @return JSONArray.
     */
    private JSONArray ExecuteSqlAndBuildJsonArray(String sql) {
        JSONArray jsonArray = new JSONArray();
        try {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
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

    /**
     * This method pulls message from mysql databases
     *
     * @param sender   a user in the chat.
     * @param receiver a user in the chat.
     * @return Nothing.
     */
    public JSONArray PullMsg(String sender, String receiver) {
        int senderid = this.findIdByusername(sender);
        int receiverid = this.findIdByusername(receiver);
        String sql = "";
        if (receiver == null)
            sql = String.format("SELECT * FROM Msg WHERE sender = %d OR receiver = %d", senderid, senderid);
        else
            sql = String.format("SELECT * FROM Msg WHERE (sender = %d AND receiver = %d) OR (sender = %d AND receiver = %d)", senderid, receiverid, receiverid, senderid);
        return this.ExecuteSqlAndBuildJsonArray(sql);
    }

    /**
     * This method verifies account and return data organized by JSONArray
     *
     * @param username username of the account.
     * @param password password of the account.
     * @return JSONArray.
     * @throws UserNotExistsException On username not exist error.
     * @throws PasswordErrorException On password wrong error.
     */
    public JSONArray Login(String username, String password)
            throws UserNotExistsException, PasswordErrorException {
        try {
            String sql = String.format("SELECT password from User WHERE username='%s'", username);
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                String pw = rs.getString("password");
                if (password.equals(pw) == true) {
                    return this.PullMsg(username, null);
                } else
                    throw new PasswordErrorException(username, password);
            } else
                throw new UserNotExistsException(username);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This method checks the username and add user data to the database
     *
     * @param username username of the account.
     * @param password password of the account.
     * @throws UserAlreadyExistsException  On duplicate username error.
     * @throws DatabaseInsertFailException On insert data to database error.
     */
    public void SignUp(String username, String password)
            throws UserAlreadyExistsException, DatabaseInsertFailException {
        if (this.ContainsName(username) == true)
            throw new UserAlreadyExistsException(username);
        else {
            if (this.InsertUser(username, password) == false)
                throw new DatabaseInsertFailException();
        }
    }

    /**
     * This method adds message data to the database
     *
     * @param sendername   sender's username.
     * @param receivername receiver's username.
     * @param content      content in the message.
     * @return boolean.
     * @throws UserNotExistsException On username not exist error.
     */
    public boolean SendMsg(String sendername, String receivername, String content,String time)
            throws UserNotExistsException {
        int senderid = this.findIdByusername(sendername);
        int receiverid = this.findIdByusername(receivername);
        if (senderid == -1)
            throw new UserNotExistsException(sendername);
        if (receiverid == -1)
            throw new UserNotExistsException(receivername);

        try {
            String sql = String.format("insert into Msg(sender,receiver,content,time) Values(%d,%d,'%s','%s')", senderid, receiverid, content, time);
            int rs = stmt.executeUpdate(sql);
            if (rs > 0)
                return true;
            else
                return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * This method finds username by userId in database
     *
     * @param username username of user.
     * @return int userid of a user -1 means not exist.
     */
    private int findIdByusername(String username) {
        if (username == null)
            return -1;
        try {
            String sql = String.format("SELECT id from User WHERE username = '%s'", username);
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next())
                return rs.getInt("id");
            else
                return -1;
        } catch (SQLException e) {
        }
        return -1;
    }

    /**
     * This method finds Id by username in database
     *
     * @param id id of user.
     * @return String username of a user null means not exist.
     */
	public String findUsernameById(int id) {
        if (id <= 0)
            return null;
        try {
            String sql = String.format("SELECT username from User WHERE id = '%d'", id);
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next())
                return rs.getString("username");
            else
                return null;
        } catch (SQLException e) {
        }
        return null;
    }

    /**
     * This method inserts a user data to the database
     *
     * @param username username of a user.
     * @param password password of a user.
     * @return boolean.
     */
    private boolean InsertUser(String username, String password) {
        try {
            String sql = String.format("insert into User(username,password) Values('%s','%s')", username, password);
            int rs = stmt.executeUpdate(sql);
            if (rs > 0)
                return true;
            else
                return false;
        } catch (SQLException e) {
        }
        return true;
    }

    /**
     * This method closes the connection to the databasae
     */
    void close() {
        try {
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException se2) {
            }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }
}
