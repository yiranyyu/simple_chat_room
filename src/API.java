public class API {
    public static boolean containsName(String username) {
        return false;
    }

    public static String pullMessageList(String username_1, String username_2) {
        return ""; // json format
    }

    public static void login(String username, String password) throws UserNotExistsException, PasswordErrorException{
    }

    public static void signup(String username, String password) throws UserAlreadyExistsException{
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

class UserAlreadyExistsException extends Exception{
    UserAlreadyExistsException(String username){
        super("User [" + username + "] already exists");
    }
}