package Controller;

import Model.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


public class AdminOverviewController implements Initializable {

    @FXML
    private Label lblNumTeachers;
    @FXML
    private Label lblNumStudents;
    @FXML
    private Label lblNumExams;
    @FXML
    private Label lblNumPractices;
    @FXML
    private VBox vboxTodayExams;

    @FXML
    private BarChart<String, Number> barChartStats;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadStatistics();
        loadTodayExams();
        loadBarChart();
    }

    private void loadStatistics() {
        int totalTeachers = UserStore.getAllUsers().stream()
                .filter(u -> "Giảng Viên".equalsIgnoreCase(u.getRole()))
                .toArray().length;
        int totalStudents = UserStore.getAllUsers().stream()
                .filter(u -> "Sinh Viên".equalsIgnoreCase(u.getRole()))
                .toArray().length;
        int totalExams = ExamStore.getAllExams().size();
        int totalPractices = PracticeStore.getAllPractices().size();

        lblNumTeachers.setText(String.valueOf(totalTeachers));
        lblNumStudents.setText(String.valueOf(totalStudents));
        lblNumExams.setText(String.valueOf(totalExams));
        lblNumPractices.setText(String.valueOf(totalPractices));
    }

    private void loadTodayExams() {
        vboxTodayExams.getChildren().clear();

        LocalDate today = LocalDate.now();

        List<Exam> todayExams = ExamStore.getAllExams().stream()
                .filter(e -> e.getStartTime() != null && e.getStartTime().toLocalDate().isEqual(today))
                .collect(Collectors.toList());

        if (todayExams.isEmpty()) {
            vboxTodayExams.getChildren().add(new Label("Hôm nay không có bài kiểm tra."));
        } else {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
            todayExams.forEach(exam -> {
                String display = exam.getName() + " (" + exam.getStartTime().format(fmt) + ")";
                vboxTodayExams.getChildren().add(new Label(display));
            });
        }
    }

    private void loadBarChart() {
        barChartStats.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Số đề kiểm tra theo tháng");

        int year = LocalDate.now().getYear();

        for (int month = 1; month <= 12; month++) {
            int finalMonth = month;
            long count = ExamStore.getAllExams().stream()
                    .filter(e -> e.getStartTime() != null &&
                            e.getStartTime().getYear() == year &&
                            e.getStartTime().getMonthValue() == finalMonth)
                    .count();
            series.getData().add(new XYChart.Data<>("Tháng " + month, count));
        }

        barChartStats.getData().add(series);
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

    @FXML
    private void handleOverView(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Controller/ AdminOverviewView.fxml"));
            Parent practiceRoot = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(practiceRoot));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot load StudentPracticeView: " + e.getMessage());
        }
    }

    @FXML
    private void handleManageTeachers(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Controller/ManageTeachersView.fxml"));
            Parent practiceRoot = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(practiceRoot));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot load StudentPracticeView: " + e.getMessage());
        }
    }

    @FXML
    private void handleManageStudents(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Controller/ManageStudentsView.fxml"));
            Parent practiceRoot = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(practiceRoot));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot load StudentPracticeView: " + e.getMessage());
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
