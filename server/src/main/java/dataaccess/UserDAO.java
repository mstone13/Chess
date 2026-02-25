package dataaccess;

import model.UserData;
import java.util.HashMap;
import java.util.Map;

public class UserDAO {
    private static final Map<String, UserData> users = new HashMap<>();

    public static void createUser(UserData user) {
        users.put(user.getUsername(), user);
    }

    public static void clearUsers() {
        users.clear();
    }

    //createUser

    //getUser
}
