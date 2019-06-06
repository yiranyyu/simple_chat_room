/**
 * Store the user information need to be displayed in GUI
 *
 * @author Yirany
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

    void setName(String name) {
        this.name = name;
    }

    String getName() {
        return name;
    }

    String getIp() {
        return ip;
    }

    void setIp(String ip) {
        this.ip = ip;
    }
}
