package Controller;

import Model.Student;
import Model.ExamResult;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class StudentController implements Initializable {

    @FXML
    private Label lblWelcome;

    @FXML
    private TextField txtName, txtBirthday, txtGender, txtEmail;

    @FXML
    private TableView<ExamResult> tableExamResults;

    @FXML
    private TableColumn<ExamResult, String> colExamName;
    @FXML
    private TableColumn<ExamResult, Double> colScore;
    @FXML
    private TableColumn<ExamResult, Integer> colCorrectCount;
    @FXML
    private TableColumn<ExamResult, Integer> colWrongCount;
    @FXML
    private TableColumn<ExamResult, String> colSubmittedAt;
    @FXML
    private TableColumn<ExamResult, Boolean> colConfirmed;

    // Thuộc tính này sẽ lưu thông tin sinh viên hiện tại
    private Student currentStudent;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Thiết lập các cột cho TableView
        // colExamName, colScore, colCorrectCount, colWrongCount, colSubmittedAt, colConfirmed
        setupExamResultsTable();
    }

    private void setupExamResultsTable() {
        // Ví dụ: examName -> getExam().getExamName()
        colExamName.setCellValueFactory(cellData -> {
            if (cellData.getValue().getExam() != null) {
                return new SimpleStringProperty(cellData.getValue().getExam().getExamName());
            }
            return new SimpleStringProperty("N/A");
        });
        colScore.setCellValueFactory(new PropertyValueFactory<>("score"));
        colCorrectCount.setCellValueFactory(new PropertyValueFactory<>("correctCount"));
        colWrongCount.setCellValueFactory(new PropertyValueFactory<>("wrongCount"));

        // Định dạng thời gian
        colSubmittedAt.setCellValueFactory(cellData -> {
            if (cellData.getValue().getSubmittedAt() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                String formatted = cellData.getValue().getSubmittedAt().format(formatter);
                return new SimpleStringProperty(formatted);
            }
            return new SimpleStringProperty("");
        });
        colConfirmed.setCellValueFactory(new PropertyValueFactory<>("confirmed"));
    }

    /**
     * Phương thức này được gọi ngay sau khi chuyển scene sang StudentView.
     * LoginController (hoặc nơi khác) có thể gọi setStudent(...) để truyền
     * thông tin sinh viên hiện tại vào StudentController.
     */
    public void setStudent(Student student) {
        this.currentStudent = student;
        // Cập nhật label chào mừng
        lblWelcome.setText("Hello, " + student.getName() + "!");
        // Đổ dữ liệu ra textfield
        txtName.setText(student.getName());
        txtBirthday.setText(String.valueOf(student.getBirthday()));
        txtGender.setText(student.getGender());
        txtEmail.setText(student.getEmail());

        // Tải danh sách kết quả thi
        loadExamResults(student);
    }

    private void loadExamResults(Student student) {
        // Demo: tạo danh sách cứng hoặc gọi service/DB
        List<ExamResult> results = new ArrayList<>();
        // Ví dụ:
        // results.add(new ExamResult("ER001", new Exam("E001", "Math Exam", ...), student, 9.0, 18, 2, LocalDateTime.now(), true));
        // ...
        tableExamResults.setItems(FXCollections.observableArrayList(results));
    }

    /**
     * Xử lý khi nhấn nút "Update Info"
     * Lưu các thông tin chỉnh sửa vào đối tượng Student và gọi DB/Service nếu cần.
     */
    @FXML
    private void handleUpdateInfo(ActionEvent event) {
        if (currentStudent != null) {
            currentStudent.setName(txtName.getText());
            currentStudent.setBirthday(LocalDate.parse(txtBirthday.getText()));
            currentStudent.setGender(txtGender.getText());
            currentStudent.setEmail(txtEmail.getText());
            // Gọi service/DB nếu muốn lưu vĩnh viễn

            showAlert(Alert.AlertType.INFORMATION, "Update", "Cập nhật thông tin thành công!");
        }
    }

    /**
     * Xử lý khi nhấn nút "Logout"
     * Quay lại màn hình Login.
     */
    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            // Giả sử LoginView.fxml nằm ở /fxml/
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/Controller/LoginView.fxml"));
            Scene loginScene = new Scene(loginRoot);
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setScene(loginScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Không thể chuyển về màn hình đăng nhập!");
        }
    }

    /**
     * Xử lý khi nhấn nút "Practice Exam"
     * Có thể chuyển sang một màn hình luyện đề (PracticeView.fxml), v.v.
     */
    @FXML
    private void handlePracticeExam(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Practice", "Chức năng luyện thi đang được phát triển!");
        // TODO: load scene PracticeExam
    }

    /**
     * Xử lý khi nhấn nút "Take Exam"
     * Có thể chuyển sang màn hình làm bài thi (ExamView.fxml).
     */
    @FXML
    private void handleTakeExam(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Take Exam", "Chức năng thi chính thức đang được phát triển!");
        // TODO: load scene TakeExam
    }

    /**
     * Hàm tiện ích hiển thị Alert
     */
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
