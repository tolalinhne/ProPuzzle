package Controller;

import Model.Admin;
import Model.User;
import Model.UserStore;
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

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Controller/ AdminOverviewView.fxml"));
            Parent adminRoot = loader.load();
            Scene teacherScene = new Scene(adminRoot);
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(teacherScene);
            stage.show();
        }

        else {
            User user = UserStore.checkLogin(usernameInput, passwordInput);
            if (user != null) {
                showAlert(Alert.AlertType.INFORMATION, "Login", "Đăng nhập thành công!");

                if (user.getRole().equalsIgnoreCase("Giảng Viên")) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Controller/TeacherView.fxml"));
                    Parent teacherRoot = loader.load();

                    TeacherController teacherController = loader.getController();
                    teacherController.setCurrentUser(user);

                    Scene teacherScene = new Scene(teacherRoot);
                    Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                    stage.setScene(teacherScene);
                    stage.show();
                } else if (user.getRole().equalsIgnoreCase("Sinh viên")) {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Controller/StudentView.fxml"));
                        Parent studentRoot = loader.load();
                        StudentController studentController = loader.getController();
                        studentController.setCurrentUser(user);
                        studentController.setCurrentUser(user);

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Controller/SignUpView.fxml"));
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
