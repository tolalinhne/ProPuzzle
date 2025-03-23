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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class EditPracticeController implements Initializable {

    @FXML
    private TextField txtExamName;
    @FXML
    private ComboBox<Subject> cbMonHoc;
    @FXML
    private TableView<Question> tableQuestions;
    @FXML
    private TableColumn<Question, String> colQuestionId;
    @FXML
    private TableColumn<Question, String> colQuestionContent;
    @FXML
    private TableColumn<Question, String> colAnswer;

    private ObservableList<Question> availableQuestions;
    private FilteredList<Question> filteredQuestions;

    private Practice currentPractice;
    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public void setPractice(Practice practice) {
        this.currentPractice = practice;
        loadPracticeData();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colQuestionId.setCellValueFactory(new PropertyValueFactory<>("questionId"));
        colQuestionContent.setCellValueFactory(new PropertyValueFactory<>("content"));
        colAnswer.setCellValueFactory(new PropertyValueFactory<>("correctAnswer"));

        tableQuestions.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        cbMonHoc.setItems(FXCollections.observableArrayList(SubjectStore.getAllSubjects()));

        cbMonHoc.valueProperty().addListener((obs, oldVal, newVal) -> {
            filterQuestionsBySubject(newVal);
        });
    }


    public void loadPracticeData() {
        if (currentPractice == null) return;

        txtExamName.setText(currentPractice.getName());

        if (currentPractice.getSubject() != null) {
            Subject s = currentPractice.getSubject();
            Subject found = SubjectStore.findByName(s.getSubjectName());
            if (found != null) {
                cbMonHoc.setValue(found);
            }
        }

        List<Question> allQuestions = QuestionStore.getAllQuestions();
        availableQuestions = FXCollections.observableArrayList(allQuestions);
        filteredQuestions = new FilteredList<>(availableQuestions, q -> true);
        tableQuestions.setItems(filteredQuestions);


        List<String> qIds = currentPractice.getQuestionIds();
        for (Question q : availableQuestions) {
            if (qIds.contains(q.getQuestionId())) {
                int index = availableQuestions.indexOf(q);
                tableQuestions.getSelectionModel().select(index);
            }
        }
    }

    private void filterQuestionsBySubject(Subject subject) {
        if (subject == null) {
            filteredQuestions.setPredicate(q -> true);
        } else {
            filteredQuestions.setPredicate(q -> {
                return q.getSubject() != null &&
                        q.getSubject().getSubjectName().equalsIgnoreCase(subject.getSubjectName());
            });
        }
    }

    @FXML
    private void handleSave(ActionEvent event) {
        if (currentPractice == null) return;

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

        currentPractice.setName(examName);

        Subject selectedSubject = cbMonHoc.getValue();
        currentPractice.setSubject(selectedSubject);

        List<String> questionIds = selectedQuestions.stream()
                .map(Question::getQuestionId)
                .collect(Collectors.toList());
        currentPractice.setQuestionIds(questionIds);

        PracticeStore.updatePractice(currentPractice);

        showAlert(Alert.AlertType.INFORMATION, "Success", "Đã cập nhật đề luyện tập!");
        navigateToTeacherPractice(event);
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        navigateToTeacherPractice(event);
    }

    private void navigateToTeacherPractice(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Controller/TeacherPracticeView.fxml"));
            Parent root = loader.load();

            TeacherPracticeController tpc = loader.getController();
            tpc.setCurrentUser(currentUser);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
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
