package Model;

import java.util.HashMap;
import java.util.Map;

public class UserStore {
    // Lưu các tài khoản: username -> User
    private static final Map<String, User> userMap = new HashMap<>();

    // Kiểm tra đăng nhập: trả về đối tượng User nếu đúng, ngược lại trả về null
    public static User checkLogin(String username, String password) {
        if (!userMap.containsKey(username)) {
            return null;
        }
        User user = userMap.get(username);
        if (user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public static boolean userExists(String username) {
        return userMap.containsKey(username);
    }

    // Thêm tài khoản mới
    public static void addUser(String username, String password, String role) {
        userMap.put(username, new User(username, password, role));
    }
}
