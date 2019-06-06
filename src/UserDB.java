/**
 * 这个类与数据库中的User表的表项对应
 *
 * @author 岑少锋
 */
public class UserDB {
    private int id;
    private String username;
    private String password;

    /**
     * This method Return the value of ID
     *
     * @return The value of ID.
     */
    public int getID() {
        return this.id;
    }

    /**
     * This method set the value of ID
     *
     * @param id The value of ID.
     */
    public void setID(int id) {
        this.id = id;
    }

    /**
     * This method Return the value of username
     *
     * @return The value of username.
     */
    public String getUserName() {
        return this.username;
    }

    /**
     * This method set the value of username
     *
     * @param name The value of username.
     */
    public void setUserName(String name) {
        this.username = name;
    }

    /**
     * This method Return the value of password
     *
     * @return The value of password.
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * This method set the value of password
     *
     * @param password The value of password.
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
