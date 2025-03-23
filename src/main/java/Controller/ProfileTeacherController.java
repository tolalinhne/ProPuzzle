package Controller;

import Model.User;
import Model.UserStore;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class ProfileTeacherController implements Initializable {

    @FXML private Hyperlink btnExam;

    @FXML private Hyperlink btnHome;

    @FXML private Hyperlink btnPractice;

    @FXML private Hyperlink btnProfile;

    @FXML private Hyperlink btnQuestionBank;

    @FXML private TextField txtName;

    @FXML private TextField txtUsername;

    @FXML private TextField txtBirthday;

    @FXML private TextField txtEmail;

    @FXML private TextField txtPhone;

    @FXML private ComboBox<String> comboGender;

    @FXML private Button btnSave;

    private User currentUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<String> genders = Arrays.asList("Nam", "Nữ", "Khác");
        comboGender.setItems(FXCollections.observableArrayList(genders));
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            txtUsername.setText(user.getUsername());
            txtName.setText(user.getName());
            txtBirthday.setText(user.getBirthday());
            txtEmail.setText(user.getEmail());
            txtPhone.setText(user.getPhone());
            if (user.getGender() != null) {
                comboGender.setValue(user.getGender());
            }
        }
    }

    @FXML
    private void handleSaveProfile(ActionEvent event) {
        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Không có user để cập nhật!");
            return;
        }
        String newName = txtName.getText().trim();
        String newBirthday = txtBirthday.getText().trim();
        String newEmail = txtEmail.getText().trim();
        String newPhone = txtPhone.getText().trim();
        String newGender = comboGender.getValue();

        if (newName.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Họ tên không được để trống!");
            return;
        }

        currentUser.setName(newName);
        currentUser.setBirthday(newBirthday);
        currentUser.setEmail(newEmail);
        currentUser.setPhone(newPhone);
        currentUser.setGender(newGender);

        UserStore.updateUser(currentUser);

        showAlert(Alert.AlertType.INFORMATION, "Thành công", "Thông tin cá nhân đã được cập nhật!");
    }

    @FXML
    private void handleProfileInfo(ActionEvent event) {
    }

    @FXML
    private void handleQuestionBank(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Controller/QuestionBankView.fxml"));
            Parent questionBankRoot = loader.load();

            QuestionBankController questionBankController = loader.getController();
            questionBankController.setCurrentUser(currentUser);

            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(questionBankRoot));
            stage.show();
        } catch(IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Không thể tải giao diện QuestionBank: " + e.getMessage());
        }
    }

    @FXML
    private void handlePracticeExam(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Controller/TeacherPracticeView.fxml"));
            Parent practiceRoot = loader.load();

            TeacherPracticeController practiceController = loader.getController();
            practiceController.setCurrentUser(currentUser);

            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(practiceRoot));
            stage.show();
        } catch(IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Không thể tải giao diện TeacherPracticeView: " + e.getMessage());
        }
    }

    @FXML
    private void handleExamTest(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Controller/TeacherExamView.fxml"));
            Parent examRoot = loader.load();

            TeacherExamController teacherExamController = loader.getController();
            teacherExamController.setCurrentUser(currentUser);

            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(examRoot));
            stage.show();
        } catch(IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Không thể tải giao diện TeacherExamView: " + e.getMessage());
        }
    }

    @FXML
    private void handleHome(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Controller/TeacherView.fxml"));
            Parent homeRoot = loader.load();

            TeacherController teacherController = loader.getController();
            teacherController.setCurrentUser(currentUser);

            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(homeRoot));
            stage.show();
        } catch(IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Không thể tải giao diện TeacherView: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
