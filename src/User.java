/**
 * @author Yirany
 * @version 1.0
 * @since 5/31/2019
 **/
public class User {
    private String name;
    private String ip;

    public User(String name, String ip) {
        this.name = name;
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}