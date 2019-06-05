import net.sf.json.JSONArray;

public class API {
    private static ProcessSQL psql = new ProcessSQL();

    public static boolean containsName(String username) {
        return psql.ContainsName(username);
    }

    public static JSONArray pullMessageList(String username_1, String username_2) {
        return psql.PullMsg(username_1, username_2);
    }

    static JSONArray login(String username, String password)
            throws UserNotExistsException, PasswordErrorException {
        return psql.Login(username, password);
    }

    static void signUp(String username, String password)
            throws UserAlreadyExistsException, DatabaseInsertFailException {
        psql.SignUp(username, password);
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