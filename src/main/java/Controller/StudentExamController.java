package Controller;

import Model.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class StudentExamController implements Initializable {

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
    private Label lblFullName;

    @FXML
    private Label lblUsername;

    @FXML
    private VBox vboxOngoingExams;

    @FXML
    private VBox vboxPastExams;

    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            lblUsername.setText(user.getUsername());
            lblFullName.setText(user.getName());
        }
        // Sau khi set xong user => load data
        loadPastExams();
        loadOngoingExams();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    private void loadPastExams() {
        if(vboxPastExams==null) return;
        vboxPastExams.getChildren().clear();

        List<Attempt> userAttempts = AttemptStore.getAllAttempts().stream()
                .filter(a->a.getUserId().equalsIgnoreCase(currentUser.getUsername()))
                .collect(Collectors.toList());
        if(userAttempts.isEmpty()) {
            Label lbl = new Label("Chưa có bài kiểm tra nào gần đây.");
            vboxPastExams.getChildren().add(lbl);
            return;
        }

        userAttempts.sort((a,b)-> b.getTimestamp().compareTo(a.getTimestamp()));

        List<Attempt> recent5 = userAttempts.stream().limit(5).collect(Collectors.toList());

        for(Attempt att : recent5) {
            Exam ex = ExamStore.getAllExams().stream()
                    .filter(x->x.getExamId().equalsIgnoreCase(att.getPracticeId()))
                    .findFirst().orElse(null);
            if(ex==null) continue;

            Hyperlink link = new Hyperlink(ex.getName()+" (điểm: "+att.getScore()+")");
            link.setOnAction(e-> showPastExamDetail(att, ex));
            vboxPastExams.getChildren().add(link);
        }
    }

    private void showPastExamDetail(Attempt att, Exam exam) {
        String msg = "Tên bài: "+exam.getName()+"\n"
                + "Thời gian nộp: "+att.getTimestamp()+"\n"
                + "Điểm: "+att.getScore()+"\n";
        showAlert(Alert.AlertType.INFORMATION,"Lịch sử làm bài", msg);
    }

    private void loadOngoingExams() {
        if(vboxOngoingExams==null) return;
        vboxOngoingExams.getChildren().clear();

        List<Exam> allExams = ExamStore.getAllExams();
        LocalDateTime now = LocalDateTime.now();

        List<Exam> ongoing = allExams.stream()
                .filter(ex-> {
                    if(ex.getStartTime()==null || ex.getEndTime()==null) return false;
                    return (!now.isBefore(ex.getStartTime()) && !now.isAfter(ex.getEndTime()));
                })
                .collect(Collectors.toList());

        if(ongoing.isEmpty()){
            Label lbl = new Label("Hiện không có bài kiểm tra nào đang diễn ra.");
            vboxOngoingExams.getChildren().add(lbl);
            return;
        }

        for(Exam ex : ongoing){
            Hyperlink link = new Hyperlink(ex.getName());
            link.setOnAction(e-> openExamDo(ex));
            vboxOngoingExams.getChildren().add(link);
        }
    }
    
    private void openExamDo(Exam exam){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Controller/ExamDoView.fxml"));
            Parent root = loader.load();

            ExamDoController doCtrl = loader.getController();
            doCtrl.setCurrentUser(currentUser);
            doCtrl.setExam(exam);

            Stage stage = (Stage)vboxOngoingExams.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch(IOException ex){
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR,"Error","Không thể mở ExamDoView: "+ex.getMessage());
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
