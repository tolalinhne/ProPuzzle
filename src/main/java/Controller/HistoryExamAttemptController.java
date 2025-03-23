package Controller;

import Model.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import java.util.ArrayList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class HistoryExamAttemptController {

    @FXML private Label lblPracticeName;
    @FXML private TableView<Attempt> tableAttempt;
    @FXML private TableColumn<Attempt, String> colUser;
    @FXML private TableColumn<Attempt, String> colFullName;
    @FXML private TableColumn<Attempt, String> colScore;
    @FXML private TableColumn<Attempt, String> colTime;
    @FXML private Button btnClose;

    private Exam currentExam;

    public void setPractice(Exam exam) {
        this.currentExam = exam;
        lblPracticeName.setText("Lịch sử làm bài: " + exam.getName());
        loadAttempts();
    }

    @FXML
    private void initialize() {
        colUser.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getUserId()));

        colFullName.setCellValueFactory(cellData -> {
            String username = cellData.getValue().getUserId();
            User user = UserStore.findByUsername(username);
            String fullName = (user != null) ? user.getName() : "Unknown";
            return new SimpleStringProperty(fullName);
        });


        colScore.setCellValueFactory(cellData -> {
            double score = cellData.getValue().getScore();
            return new SimpleStringProperty(String.valueOf(score));
        });

        colTime.setCellValueFactory(cellData -> {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            return new SimpleStringProperty(cellData.getValue().getTimestamp().format(fmt));
        });
    }

    private void loadAttempts() {
        if (currentExam == null) return;

        List<Attempt> attempts = AttemptStore.getAllAttempts().stream()
                .filter(a -> a.getPracticeId().equalsIgnoreCase(currentExam.getExamId()))
                .collect(Collectors.toCollection(ArrayList::new));

        if (!attempts.isEmpty()) {
            attempts.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
        }
        tableAttempt.getItems().setAll(attempts);

    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }
}
