import net.sf.json.JSONArray;

public class API {
    public static ProcessSQL psql = new ProcessSQL();

    public static boolean containsName(String username) {
        return psql.ContainsName(username);
    }

    public static JSONArray pullMessageList(String username_1, String username_2) {
        return psql.PullMsg(username_1, username_2);
    }

    public static JSONArray login(String username, String password)
            throws UserNotExistsException, PasswordErrorException {
//        System.out.printf("Try login with %s %s\n", username, password);
        return psql.Login(username, password);
    }

    public static void signup(String username, String password)
            throws UserAlreadyExistsException, DatabaseInsertFailException {
        psql.SignUp(username, password);
    }

    public static void sendmsg(String sendername, String receivername, String content)
            throws UserNotExistsException {
        psql.SendMsg(sendername, receivername, content);
    }
}

class UserNotExistsException extends Exception {
    UserNotExistsException(String username) {
        super("Username [" + username + "] not exists.");
    }
}

class PasswordErrorException extends Exception {
    PasswordErrorException(String username, String password) {
        super("Invalid password[" + password + "] for user [" + username + "]");
    }
}

class UserAlreadyExistsException extends Exception {
    UserAlreadyExistsException(String username) {
        super("User [" + username + "] already exists");
    }
}

class DatabaseInsertFailException extends Exception {
    DatabaseInsertFailException() {
        super("Fail to Insert Value");
    }
}
