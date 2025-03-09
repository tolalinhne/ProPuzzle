package Controller;

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
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();
        String confirm = txtConfirmPassword.getText().trim();
        String role = comboRole.getValue();

        // Kiểm tra các trường đầu vào thông qua hàm validateInputs()
        String validationError = validateInputs(username, password, confirm, role);
        if (validationError != null) {
            showAlert(Alert.AlertType.ERROR, "Registration Error", validationError);
            return;
        }

        // Kiểm tra xem tài khoản đã tồn tại chưa
        if (UserStore.userExists(username)) {
            showAlert(Alert.AlertType.ERROR, "Registration Error", "Tài khoản đã tồn tại!");
            return;
        }

        // Tạo tài khoản mới với role đã chọn
        UserStore.addUser(username, password, role);
        showAlert(Alert.AlertType.INFORMATION, "Success", "Đăng ký thành công!");

        // Quay lại màn hình Login sau đăng ký thành công
        try {
            backToLogin(event);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Không thể chuyển về màn hình đăng nhập: " + e.getMessage());
        }
    }

    /**
     * Kiểm tra tính hợp lệ của các trường đầu vào.
     * @return thông báo lỗi nếu có, hoặc null nếu hợp lệ.
     */
    private String validateInputs(String username, String password, String confirm, String role) {
        // Kiểm tra trường trống
        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty() || role == null) {
            return "Vui lòng nhập đầy đủ thông tin và chọn vai trò!";
        }
        // Kiểm tra độ dài username (ví dụ: tối thiểu 4 ký tự)
        if (username.length() < 4) {
            return "Tên đăng nhập phải có ít nhất 4 ký tự!";
        }
        // Kiểm tra username không chứa khoảng trắng
        if (username.contains(" ")) {
            return "Tên đăng nhập không được chứa khoảng trắng!";
        }
        // Kiểm tra độ dài mật khẩu (ví dụ: tối thiểu 6 ký tự)
        if (password.length() < 6) {
            return "Mật khẩu phải có ít nhất 6 ký tự!";
        }
        // Kiểm tra mật khẩu và xác nhận mật khẩu có khớp không
        if (!password.equals(confirm)) {
            return "Mật khẩu và xác nhận mật khẩu không khớp!";
        }
        // Bạn có thể thêm các quy tắc kiểm tra khác (ví dụ: chứa chữ và số, ký tự đặc biệt, …)
        return null;  // Hợp lệ
    }

    @FXML
    public void onBackToLoginClicked(ActionEvent event) throws IOException {
        backToLogin(event);
    }

    private void backToLogin(ActionEvent event) throws IOException {
        // Đảm bảo rằng file LoginView.fxml nằm đúng vị trí (ví dụ: /fxml/LoginView.fxml)
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Controller/LoginView.fxml"));
        Parent loginRoot = loader.load();
        Scene loginScene = new Scene(loginRoot);
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.setScene(loginScene);
        stage.show();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        // In ra console để kiểm tra debug
        System.out.println(title + ": " + message);

        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
