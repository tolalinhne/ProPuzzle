package Controller;

import Model.Exam;
import Model.Question;
import Model.Teacher;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class TeacherController implements Initializable {

    // Bảng câu hỏi
    @FXML
    private TableView<Question> tableQuestions;
    @FXML
    private TableColumn<Question, String> colQuestionId;
    @FXML
    private TableColumn<Question, String> colContent;
    @FXML
    private TableColumn<Question, String> colCorrectAnswer;

    // Bảng đề thi
    @FXML
    private TableView<Exam> tableExams;
    @FXML
    private TableColumn<Exam, String> colExamId;
    @FXML
    private TableColumn<Exam, String> colExamName;
    @FXML
    private TableColumn<Exam, String> colStartTime;
    @FXML
    private TableColumn<Exam, String> colEndTime;

    // Các TextField nhập câu hỏi
    @FXML
    private TextField txtQuestionContent, txtOptionA, txtOptionB, txtOptionC, txtOptionD, txtCorrectAnswer;

    // Các TextField nhập đề thi
    @FXML
    private TextField txtExamName, txtStartTime, txtEndTime;

    // Thuộc tính lưu thông tin giảng viên hiện tại (nếu cần)
    private Teacher currentTeacher;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupQuestionTable();
        setupExamTable();
        // Tải dữ liệu ban đầu (nếu cần)
        loadQuestions();
        loadExams();
    }

    // Gọi khi LoginController (hoặc nơi khác) chuyển sang TeacherView
    public void setTeacher(Teacher teacher) {
        this.currentTeacher = teacher;
        // Có thể hiển thị tên giảng viên, ...
        // showAlert(Alert.AlertType.INFORMATION, "Hello", "Welcome, " + teacher.getFullName());
    }

    /**
     * Thiết lập bảng câu hỏi
     */
    private void setupQuestionTable() {
        colQuestionId.setCellValueFactory(new PropertyValueFactory<>("questionId"));
        colContent.setCellValueFactory(new PropertyValueFactory<>("content"));
        colCorrectAnswer.setCellValueFactory(new PropertyValueFactory<>("correctAnswer"));

        // Chọn row -> hiển thị lên form để chỉnh sửa
        tableQuestions.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                txtQuestionContent.setText(newVal.getContent());
                txtOptionA.setText(newVal.getOptionA());
                txtOptionB.setText(newVal.getOptionB());
                txtOptionC.setText(newVal.getOptionC());
                txtOptionD.setText(newVal.getOptionD());
                txtCorrectAnswer.setText(newVal.getCorrectAnswer());
            }
        });
    }

    /**
     * Thiết lập bảng đề thi
     */
    private void setupExamTable() {
        colExamId.setCellValueFactory(new PropertyValueFactory<>("examId"));
        colExamName.setCellValueFactory(new PropertyValueFactory<>("examName"));
        // Định dạng thời gian
        colStartTime.setCellValueFactory(cellData -> {
            LocalDateTime st = cellData.getValue().getStartTime();
            if (st != null) {
                return new SimpleStringProperty(st.toString());
            }
            return new SimpleStringProperty("");
        });
        colEndTime.setCellValueFactory(cellData -> {
            LocalDateTime et = cellData.getValue().getEndTime();
            if (et != null) {
                return new SimpleStringProperty(et.toString());
            }
            return new SimpleStringProperty("");
        });

        // Chọn row -> hiển thị lên form để chỉnh sửa
        tableExams.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                txtExamName.setText(newVal.getExamName());
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                if (newVal.getStartTime() != null) {
                    txtStartTime.setText(newVal.getStartTime().format(fmt));
                }
                if (newVal.getEndTime() != null) {
                    txtEndTime.setText(newVal.getEndTime().format(fmt));
                }
            }
        });
    }

    /**
     * Tải danh sách câu hỏi (có thể từ DB hoặc file)
     */
    private void loadQuestions() {
        // Ví dụ cứng. Thực tế bạn gọi service/DB
        // List<Question> questions = QuestionService.getAll();
        List<Question> questions = List.of(
                new Question("Q001", "What is 2+2?", "1", "2", "3", "4", "D"),
                new Question("Q002", "Capital of France?", "Paris", "Rome", "Berlin", "Madrid", "A")
        );
        tableQuestions.setItems(FXCollections.observableArrayList(questions));
    }

    /**
     * Tải danh sách đề thi
     */
    private void loadExams() {
        // Ví dụ cứng
        // List<Exam> exams = ExamService.getAll();
        List<Exam> exams = List.of(
                new Exam("E001", "Math Exam", LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)),
                new Exam("E002", "History Exam", LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(4))
        );
        tableExams.setItems(FXCollections.observableArrayList(exams));
    }

    /**
     * Xử lý khi nhấn nút "Create Question"
     */
    @FXML
    private void handleCreateQuestion(ActionEvent event) {
        // Lấy dữ liệu từ form
        String content = txtQuestionContent.getText().trim();
        String optA = txtOptionA.getText().trim();
        String optB = txtOptionB.getText().trim();
        String optC = txtOptionC.getText().trim();
        String optD = txtOptionD.getText().trim();
        String correct = txtCorrectAnswer.getText().trim().toUpperCase();

        // Kiểm tra hợp lệ
        if (content.isEmpty() || optA.isEmpty() || optB.isEmpty() || optC.isEmpty() || optD.isEmpty() || correct.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields!");
            return;
        }
        if (!List.of("A","B","C","D").contains(correct)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Correct answer must be A, B, C, or D.");
            return;
        }

        // Tạo ID câu hỏi mới (ví dụ cứng)
        String newQuestionId = "Q" + (tableQuestions.getItems().size() + 1);

        // Tạo đối tượng Question
        Question newQ = new Question(newQuestionId, content, optA, optB, optC, optD, correct);

        // Thêm vào TableView
        tableQuestions.getItems().add(newQ);

        // Gọi service/DB nếu cần
        // QuestionService.create(newQ);

        showAlert(Alert.AlertType.INFORMATION, "Success", "New question created!");
        clearQuestionFields();
    }

    /**
     * Xử lý khi nhấn nút "Update Question"
     */
    @FXML
    private void handleUpdateQuestion(ActionEvent event) {
        Question selectedQ = tableQuestions.getSelectionModel().getSelectedItem();
        if (selectedQ == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No question selected!");
            return;
        }
        // Cập nhật dữ liệu
        selectedQ.setContent(txtQuestionContent.getText().trim());
        selectedQ.setOptionA(txtOptionA.getText().trim());
        selectedQ.setOptionB(txtOptionB.getText().trim());
        selectedQ.setOptionC(txtOptionC.getText().trim());
        selectedQ.setOptionD(txtOptionD.getText().trim());
        selectedQ.setCorrectAnswer(txtCorrectAnswer.getText().trim().toUpperCase());

        // Gọi service/DB nếu cần (QuestionService.update(selectedQ))
        tableQuestions.refresh(); // Refresh hiển thị
        showAlert(Alert.AlertType.INFORMATION, "Success", "Question updated!");
        clearQuestionFields();
    }

    private void clearQuestionFields() {
        txtQuestionContent.clear();
        txtOptionA.clear();
        txtOptionB.clear();
        txtOptionC.clear();
        txtOptionD.clear();
        txtCorrectAnswer.clear();
    }

    /**
     * Xử lý khi nhấn nút "Create Exam"
     */
    @FXML
    private void handleCreateExam(ActionEvent event) {
        String examName = txtExamName.getText().trim();
        String start = txtStartTime.getText().trim();
        String end = txtEndTime.getText().trim();

        if (examName.isEmpty() || start.isEmpty() || end.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in exam name, start time, end time!");
            return;
        }

        // Chuyển start, end sang LocalDateTime
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime st, et;
        try {
            st = LocalDateTime.parse(start, fmt);
            et = LocalDateTime.parse(end, fmt);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid date/time format! Use yyyy-MM-dd HH:mm");
            return;
        }

        // Tạo ID đề thi mới (ví dụ cứng)
        String newExamId = "E" + (tableExams.getItems().size() + 1);

        Exam exam = new Exam(newExamId, examName, st, et);
        tableExams.getItems().add(exam);

        // Gọi service/DB nếu cần (ExamService.create(exam))
        showAlert(Alert.AlertType.INFORMATION, "Success", "New exam created!");
        clearExamFields();
    }

    /**
     * Xử lý khi nhấn nút "Update Exam"
     */
    @FXML
    private void handleUpdateExam(ActionEvent event) {
        Exam selectedExam = tableExams.getSelectionModel().getSelectedItem();
        if (selectedExam == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No exam selected!");
            return;
        }

        // Cập nhật
        selectedExam.setExamName(txtExamName.getText().trim());

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        try {
            LocalDateTime st = LocalDateTime.parse(txtStartTime.getText().trim(), fmt);
            LocalDateTime et = LocalDateTime.parse(txtEndTime.getText().trim(), fmt);
            selectedExam.setStartTime(st);
            selectedExam.setEndTime(et);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid date/time format! Use yyyy-MM-dd HH:mm");
            return;
        }

        // Gọi service/DB nếu cần (ExamService.update(selectedExam))
        tableExams.refresh();
        showAlert(Alert.AlertType.INFORMATION, "Success", "Exam updated!");
        clearExamFields();
    }

    private void clearExamFields() {
        txtExamName.clear();
        txtStartTime.clear();
        txtEndTime.clear();
    }

    /**
     * Xử lý khi nhấn nút "Logout"
     */
    @FXML
    private void handleLogout(ActionEvent event) {
        try {
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
