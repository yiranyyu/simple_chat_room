/**
 * Store the user information need to be displayed in GUI
 *
 * @author 余天予
 */
class User {
    /**
     * Name is one of the unique identifier of each user
     */
    private String name;

    /**
     * The ip address of this user's client
     */
    private String ip;

    User(String name, String ip) {
        this.name = name;
        this.ip = ip;
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    String getIp() {
        return ip;
    }

    void setIp(String ip) {
        this.ip = ip;
    }
}
