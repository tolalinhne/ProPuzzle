package Controller;

import Model.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Comparator;
import java.util.List;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class StudentController implements Initializable {

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
    private HBox hboxSubjects;

    @FXML
    private ScrollPane scrollPaneSubjects;

    @FXML
    private Label lblUsername;

    @FXML
    private Label lblFullName;

    @FXML
    private VBox vboxRecentPractices;

    private User currentUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadSubjectsHorizontally();
        loadRecentPractices();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            lblUsername.setText(user.getUsername());
            lblFullName.setText(user.getName());
        }
    }

    private void loadSubjectsHorizontally() {
        hboxSubjects.getChildren().clear();

        List<Subject> subjects = SubjectStore.getAllSubjects();
        for (Subject s : subjects) {
            Hyperlink link = new Hyperlink(s.getSubjectName());
            link.setOnAction(e -> handleSubjectClicked(s.getSubjectName()));

            link.setStyle("-fx-font-weight: bold; -fx-text-fill: #b30000;");

            hboxSubjects.getChildren().add(link);
        }
    }

    private void handleSubjectClicked(String subjectName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Controller/PracticeBySubjectView.fxml"));
            Parent practiceRoot = loader.load();

            PracticeBySubjectController practiceBySubjectController = loader.getController();
            practiceBySubjectController.setCurrentUser(currentUser);
            practiceBySubjectController.setSubjectName(subjectName);

            Stage stage = (Stage) hboxSubjects.getScene().getWindow();
            stage.setScene(new Scene(practiceRoot));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot load StudentPracticeView: " + e.getMessage());
        }
    }

    private void loadRecentPractices() {
        if (vboxRecentPractices == null) return;

        vboxRecentPractices.getChildren().clear();

        List<Practice> allPractices = PracticeStore.getAllPractices();

        List<Practice> recentPractices = allPractices.stream()
                .sorted(Comparator.comparing(Practice::getPracticeId).reversed())
                .limit(7)
                .collect(Collectors.toList());

        for (Practice p : recentPractices) {
            Hyperlink link = new Hyperlink(p.getName());
            link.setOnAction(e -> handlePracticeDetail(p.getPracticeId()));
            link.setStyle("-fx-font-weight: bold; -fx-text-fill: #b30000;");
            vboxRecentPractices.getChildren().add(link);
        }
    }

    private void handlePracticeDetail(String practiceId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Controller/ExamView.fxml"));
            Parent examRoot = loader.load();

            ExamController examController = loader.getController();
            examController.setCurrentUser(currentUser);
            Practice selectedPractice = PracticeStore.getAllPractices().stream()
                    .filter(p -> p.getPracticeId().equalsIgnoreCase(practiceId))
                    .findFirst().orElse(null);
            if (selectedPractice != null) {
                examController.setPractice(selectedPractice);
            }
            Stage stage = (Stage) vboxRecentPractices.getScene().getWindow();
            stage.setScene(new Scene(examRoot));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot load ExamView: " + e.getMessage());
        }
    }

    @FXML
    private void handleProfileInfo(ActionEvent event) {
        try {
            URL profileInfoUrl = getClass().getResource("/Controller/ProfileStudentView.fxml");
            System.out.println("ProfileInfo URL: " + profileInfoUrl);

            FXMLLoader loader = new FXMLLoader(profileInfoUrl);
            Parent profileRoot = loader.load();

            ProfileStudentController profileController = loader.getController();
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
    private void handlePracticeExam(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Controller/StudentPracticeView.fxml"));
            Parent practiceRoot = loader.load();

            StudentPracticeController practiceController = loader.getController();
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Controller/StudentExamView.fxml"));
            Parent examRoot = loader.load();

            StudentExamController examController = loader.getController();
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Controller/StudentView.fxml"));
            Parent homeRoot = loader.load();

            StudentController studentController = loader.getController();
            studentController.setCurrentUser(currentUser);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(homeRoot));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot load StudentView: " + e.getMessage());
        }
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
