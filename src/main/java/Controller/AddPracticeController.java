package Controller;

import Model.Practice;
import Model.PracticeStore;
import Model.Question;
import Model.QuestionStore;
import Model.Subject;
import Model.SubjectStore;
import Model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ResourceBundle;

public class AddPracticeController implements Initializable {

    @FXML
    private TextField txtExamName;
    @FXML
    private TableView<Question> tableQuestions;
    @FXML
    private TableColumn<Question, String> colQuestionId;
    @FXML
    private TableColumn<Question, String> colQuestionContent;
    @FXML
    private TableColumn<Question, String> colAnswer;

    @FXML
    private ComboBox<Subject> cbMonHoc;

    @FXML
    private Button btnCreateExam;
    @FXML
    private Button btnCancel;

    private ObservableList<Question> availableQuestions;

    private FilteredList<Question> filteredQuestions;

    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colQuestionId.setCellValueFactory(new PropertyValueFactory<>("questionId"));
        colQuestionContent.setCellValueFactory(new PropertyValueFactory<>("content"));
        colAnswer.setCellValueFactory(new PropertyValueFactory<>("correctAnswer"));

        tableQuestions.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Load câu hỏi từ QuestionStore
        loadAvailableQuestions();

        // Load danh sách môn học từ SubjectStore vào ComboBox
        cbMonHoc.setItems(FXCollections.observableArrayList(SubjectStore.getAllSubjects()));
        cbMonHoc.valueProperty().addListener((obs, oldVal, newVal) -> {
            filterQuestionsBySubject(newVal);
        });
    }

    // Load câu hỏi từ QuestionStore và khởi tạo FilteredList
    private void loadAvailableQuestions() {
        List<Question> questions = QuestionStore.getAllQuestions();
        availableQuestions = FXCollections.observableArrayList(questions);
        filteredQuestions = new FilteredList<>(availableQuestions, q -> true);
        tableQuestions.setItems(filteredQuestions);
    }

    // Bộ lọc câu hỏi theo môn học
    private void filterQuestionsBySubject(Subject subject) {
        if (subject == null) {
            filteredQuestions.setPredicate(q -> true);
        } else {
            filteredQuestions.setPredicate(q -> q.getSubject() != null &&
                    q.getSubject().getSubjectName().equalsIgnoreCase(subject.getSubjectName()));
        }
    }

    @FXML
    private void handleCreateExam(ActionEvent event) {
        String examName = txtExamName.getText().trim();
        if (examName.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Vui lòng nhập tên bài luyện tập!");
            return;
        }

        ObservableList<Question> selectedQuestions = tableQuestions.getSelectionModel().getSelectedItems();
        if (selectedQuestions.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Chưa chọn câu hỏi", "Vui lòng chọn ít nhất một câu hỏi!");
            return;
        }

        List<String> selectedQuestionIds = selectedQuestions.stream()
                .map(Question::getQuestionId)
                .collect(Collectors.toList());

        Practice practiceExam = new Practice("PE" + System.currentTimeMillis(), examName, selectedQuestionIds);

        practiceExam.setTeacherUsername(currentUser.getUsername());

        Subject subject = cbMonHoc.getValue();
        if (subject != null) {
            practiceExam.setSubject(subject);
        }

        PracticeStore.addPractice(practiceExam);

        showAlert(Alert.AlertType.INFORMATION, "Success", "Bài luyện tập đã được tạo thành công!");
        handleCancel(event);
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Controller/TeacherPracticeView.fxml"));
            Parent practiceRoot = loader.load();

            TeacherPracticeController teacherPracticeController = loader.getController();
            teacherPracticeController.setCurrentUser(currentUser);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(practiceRoot));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot load TeacherPracticeView: " + e.getMessage());
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
