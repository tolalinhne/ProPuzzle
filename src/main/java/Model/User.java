package Model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String username;
    private String password;
    private String name;
    private String birthday;
    private String gender;
    private String email;
    private String phone;
    private String role;
    private List<String> favoritePracticeIds = new ArrayList<>();

    public User() {
    }

    public User(String username, String password, String name, String birthday, String gender, String email, String phone, String role) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.birthday = birthday;
        this.gender = gender;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<String> getFavoritePracticeIds() {
        return favoritePracticeIds;
    }

    public void setFavoritePracticeIds(List<String> favoritePracticeIds) {
        this.favoritePracticeIds = favoritePracticeIds;
    }

    public void addFavoritePractice(String practiceId) {
        if (!favoritePracticeIds.contains(practiceId)) {
            favoritePracticeIds.add(practiceId);
        }
    }

    public void removeFavoritePractice(String practiceId) {
        favoritePracticeIds.remove(practiceId);
    }
}
