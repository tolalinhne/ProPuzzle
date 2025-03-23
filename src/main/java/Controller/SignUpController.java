package Controller;

import Model.User;
import Model.UserStore;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class SignUpController {
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private PasswordField txtConfirmPassword;
    @FXML
    private ComboBox<String> comboRole;
    @FXML
    private Button btnSignUp;

    @FXML
    public void onSignUpButtonClicked(ActionEvent event) {
        String name = txtName.getText().trim();
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();
        String confirm = txtConfirmPassword.getText().trim();
        String role = comboRole.getValue();

        String validationError = validateInputs(username, password, confirm, role);
        if (validationError != null) {
            showAlert(Alert.AlertType.ERROR, "Registration Error", validationError);
            return;
        }

        if (UserStore.userExists(username)) {
            showAlert(Alert.AlertType.ERROR, "Registration Error", "Tài khoản đã tồn tại!");
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setName(name);
        user.setBirthday("");
        user.setGender("N/A");
        user.setEmail("");
        user.setPhone("");
        user.setRole(role);
        UserStore.addUser(user);
        showAlert(Alert.AlertType.INFORMATION, "Success", "Đăng ký thành công!");

        try {
            backToLogin(event);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Không thể chuyển về màn hình đăng nhập: " + e.getMessage());
        }
    }

    private String validateInputs(String username, String password, String confirm, String role) {
        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty() || role == null) {
            return "Vui lòng nhập đầy đủ thông tin và chọn vai trò!";
        }
        if (username.length() < 4) {
            return "Tên đăng nhập phải có ít nhất 4 ký tự!";
        }
        if (username.contains(" ")) {
            return "Tên đăng nhập không được chứa khoảng trắng!";
        }
        if (password.length() < 6) {
            return "Mật khẩu phải có ít nhất 6 ký tự!";
        }
        if (!password.equals(confirm)) {
            return "Mật khẩu và xác nhận mật khẩu không khớp!";
        }
        return null;  // Không có lỗi
    }

    @FXML
    public void onBackToLoginClicked(ActionEvent event) throws IOException {
        backToLogin(event);
    }

    private void backToLogin(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Controller/LoginView.fxml"));
        Parent loginRoot = loader.load();
        Scene loginScene = new Scene(loginRoot);
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.setScene(loginScene);
        stage.show();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        System.out.println(title + ": " + message);

        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
