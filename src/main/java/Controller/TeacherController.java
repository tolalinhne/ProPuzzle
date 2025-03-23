package Controller;

import Model.*;
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
import java.util.ResourceBundle;

public class TeacherController implements Initializable {

    @FXML
    private Hyperlink btnExam;

    @FXML
    private Hyperlink btnHome;

    @FXML
    private Button btnLogout;

    @FXML
    private Hyperlink btnPractice;

    @FXML
    private Hyperlink btnProfile;

    @FXML
    private Hyperlink btnQuestionBank;

    @FXML
    private Label lblUsername;

    @FXML
    private Label lblFullName;

    @FXML
    private Label lblNumPractice;
    @FXML
    private Label lblNumExam;
    @FXML
    private Label lblNumQuestion;
    @FXML
    private Label lblNumAttempt;

    private User currentUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            lblUsername.setText(user.getUsername());
            lblFullName.setText(user.getName());

            loadStatistics();
        }
    }

    private void loadStatistics() {
        int teacherPracticeCount = PracticeStore.getPracticesByTeacher(currentUser.getUsername()).size();

        int teacherExamCount = ExamStore.getExamsByTeacher(currentUser.getUsername()).size();

        int totalQuestion = QuestionStore.getAllQuestions().size();

        int teacherAttemptCount = (int) AttemptStore.getAllAttempts().stream()
                .filter(a -> PracticeStore.getPracticesByTeacher(currentUser.getUsername())
                        .stream()
                        .anyMatch(p -> p.getPracticeId().equalsIgnoreCase(a.getPracticeId())))
                .count();

        lblNumPractice.setText("Số bài luyện tập: " + teacherPracticeCount);
        lblNumExam.setText("Số đề kiểm tra: " + teacherExamCount);
        lblNumQuestion.setText("Số câu hỏi: " + totalQuestion);
        lblNumAttempt.setText("Lượt làm bài: " + teacherAttemptCount);
    }

    @FXML
    private void handleProfileInfo(ActionEvent event) {
        try {
            URL profileInfoUrl = getClass().getResource("/Controller/ProfileTeacherView.fxml");
            System.out.println("ProfileInfo URL: " + profileInfoUrl);

            FXMLLoader loader = new FXMLLoader(profileInfoUrl);
            Parent profileRoot = loader.load();

            ProfileTeacherController profileController = loader.getController();
            profileController.setCurrentUser(currentUser);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(profileRoot));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot load ProfileStudentView: " + e.getMessage());
        }
    }

    @FXML
    private void handleQuestionBank(ActionEvent event) {
        try {
            URL questionBankUrl = getClass().getResource("/Controller/QuestionBankView.fxml");
            System.out.println("ProfileInfo URL: " + questionBankUrl);

            FXMLLoader loader = new FXMLLoader(questionBankUrl);
            Parent questionBankRoot = loader.load();

            QuestionBankController questionBankController = loader.getController();
            questionBankController.setCurrentUser(currentUser);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(questionBankRoot));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot load ProfileStudentView: " + e.getMessage());
        }
    }

    @FXML
    private void handlePracticeExam(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Controller/TeacherPracticeView.fxml"));
            Parent practiceRoot = loader.load();

            TeacherPracticeController practiceController = loader.getController();
            practiceController.setCurrentUser(currentUser);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(practiceRoot));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot load StudentPracticeView: " + e.getMessage());
        }
    }

    @FXML
    private void handleExamTest(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Controller/TeacherExamView.fxml"));
            Parent examRoot = loader.load();

            TeacherExamController examController = loader.getController();
            examController.setCurrentUser(currentUser);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(examRoot));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot load StudentExamView: " + e.getMessage());
        }
    }

    @FXML
    private void handleHome(ActionEvent event) {
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/Controller/LoginView.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loginRoot));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot load LoginView: " + e.getMessage());
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
