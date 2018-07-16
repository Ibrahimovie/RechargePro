package bean.manager;

import java.util.*;

import bean.*;

/**
 * @author kingfans
 */
public class UserManager {
    private static final HashMap<String, User> userMap = new HashMap<>(1);
    private static final String USER_NAME = "user";

    public static void addUser(User user) {
        synchronized (userMap) {
            if (!userMap.isEmpty()) {
                userMap.clear();
            }
            userMap.put(USER_NAME, user);
        }
    }

    public static void removeUser() {
        synchronized (userMap) {
            userMap.clear();
        }
    }

    public static User getUser() {
        synchronized (userMap) {
            return userMap.get(USER_NAME);
        }
    }

}
