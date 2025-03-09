package Controller;

import Model.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import Model.Admin;
import Model.UserStore;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private Button loginButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initComponent();
    }

    private void initComponent() {
        username.clear();
        password.clear();
        loginButton.setDisable(true);


        username.textProperty().addListener((obs, oldVal, newVal) -> {
            loginButton.setDisable(newVal.trim().isEmpty() || password.getText().trim().isEmpty());
        });
        password.textProperty().addListener((obs, oldVal, newVal) -> {
            loginButton.setDisable(newVal.trim().isEmpty() || username.getText().trim().isEmpty());
        });

        Platform.runLater(() -> username.requestFocus());
    }

    @FXML
    private void onLoginButtonClicked(ActionEvent event) throws IOException {
        String usernameInput = username.getText().trim();
        String passwordInput = password.getText().trim();

        if (usernameInput.isEmpty() || passwordInput.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Vui lòng nhập đầy đủ thông tin đăng nhập!");
            return;
        }


        if (usernameInput.equals(Admin.USERNAME) && passwordInput.equals(Admin.PASSWORD)) {
            showAlert(Alert.AlertType.INFORMATION, "Login", "Đăng nhập Admin thành công!");
            // TODO: Load giao diện Admin (ví dụ: loadScene(event, "/fxml/AdminDashboard.fxml"))
            return;
        }

        else {
            Model.User user = UserStore.checkLogin(usernameInput, passwordInput);
            if (user != null) {
                showAlert(Alert.AlertType.INFORMATION, "Login", "Đăng nhập thành công!");
                if (user.getRole().equalsIgnoreCase("teacher")) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Controller/TeacherView.fxml"));
                    Parent teacherRoot = loader.load();

                    TeacherController teacherController = loader.getController();
                    // teacherController.setTeacher(teacher); // Giả sử bạn có lớp Teacher
                    // teacher = new Teacher(user)...

                    Scene teacherScene = new Scene(teacherRoot);
                    Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                    stage.setScene(teacherScene);
                    stage.show();
                } else if (user.getRole().equalsIgnoreCase("student")) {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Controller/StudentView.fxml"));
                        Parent studentRoot = loader.load();
                        StudentController studentController = loader.getController();
                        // Truyền thông tin Student (giả sử bạn có lớp Student thay vì User)
                        // studentController.setStudent(student); // student = new Student(user)...

                        Scene studentScene = new Scene(studentRoot);
                        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                        stage.setScene(studentScene);
                        stage.show();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Login", "Vai trò không xác định!");
                }
            } else {
                wrongPasswordAlert();
            }
        }
    }


    @FXML
    private void onSignUpLinkClicked(ActionEvent event) {
        try {
            URL signUpUrl = getClass().getResource("/Controller/SignUpView.fxml");
            System.out.println("SignUpView URL: " + signUpUrl);
            if (signUpUrl == null) {
                throw new IOException("Không tìm thấy file SignUpView.fxml tại đường dẫn /fxml/SignUpView.fxml");
            }
            FXMLLoader loader = new FXMLLoader(signUpUrl);
            Parent signUpRoot = loader.load();
            Scene signUpScene = new Scene(signUpRoot);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(signUpScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Không thể tải màn hình Sign Up");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    private void wrongPasswordAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Sai tài khoản hoặc mật khẩu!");
        alert.setContentText("Vui lòng kiểm tra lại hoặc liên hệ admin để được hỗ trợ");
        Optional<ButtonType> result = alert.showAndWait();
        initComponent();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void loadScene(ActionEvent event, String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
