package Model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserStore {
    private static final String DATA_FILE = "users.json";
    private static List<User> userList = new ArrayList<>();

    static {
        loadFromFile();
    }

    private static void loadFromFile() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;

        try (FileReader reader = new FileReader(file)) {
            Gson gson = new GsonBuilder().create();
            User[] users = gson.fromJson(reader, User[].class);
            if (users != null) {
                for (User u : users) {
                    userList.add(u);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveToFile() {
        try (FileWriter writer = new FileWriter(DATA_FILE)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(userList, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addUser(User user) {
        userList.add(user);
        saveToFile();
    }

    public static boolean deleteUser(User user) {
        if (user == null) {
            return false;
        }
        return userList.remove(user);
    }

    public static List<User> getAllUsers() {
        return userList;
    }

    public static boolean userExists(String username) {
        return userList.stream()
                .anyMatch(u -> u.getUsername().equalsIgnoreCase(username));
    }

    public static User findByUsername(String username) {
        for (User u : userList) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                return u;
            }
        }
        return null;
    }

    public static void updateUser(User updatedUser) {
        for (int i = 0; i < userList.size(); i++) {
            User u = userList.get(i);
            if (u.getUsername().equalsIgnoreCase(updatedUser.getUsername())) {
                userList.set(i, updatedUser);
                break;
            }
        }
        saveToFile();
    }

    public static boolean updatePassword(User user, String newPassword) {
        if (user == null || newPassword == null || newPassword.trim().isEmpty()) {
            return false;
        }
        for (User u : userList) {
            if (u.getUsername().equals(user.getUsername())) {
                u.setPassword(newPassword);
                return true;
            }
        }
        return false;
    }

    public static User checkLogin(String username, String password) {
        return userList.stream()
                .filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }

}
