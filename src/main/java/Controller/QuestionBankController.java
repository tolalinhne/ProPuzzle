package Controller;

import Model.Question;
import Model.Subject;
import Model.User;
import Model.QuestionStore;
import Model.SubjectStore;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class QuestionBankController implements Initializable {

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
    private ComboBox<Subject> comboSubject;

    @FXML
    private Button btnThemMoi;

    @FXML
    private TextField txtTuKhoa;

    @FXML
    private Button btnTimKiem;

    @FXML
    private TableColumn<Question, Void> colAction;

    @FXML
    private TableColumn<Question, String> colCauHoi;

    @FXML
    private TableColumn<Question, String> colDapAn;

    @FXML
    private TableColumn<Question, String> colMaCauHoi;


    private FilteredList<Question> filteredQuestions;

    @FXML
    private TableView<Question> tableQuestions;

    @FXML
    private Label lblUsername;

    @FXML
    private Label lblFullName;

    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            lblUsername.setText(user.getUsername());
            lblFullName.setText(user.getName());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colMaCauHoi.setCellValueFactory(new PropertyValueFactory<>("questionId"));
        colCauHoi.setCellValueFactory(new PropertyValueFactory<>("content"));
        colDapAn.setCellValueFactory(new PropertyValueFactory<>("correctAnswer"));

        addButtonToTable();
        loadQuestions();
        loadSubjects();
    }

    private void addButtonToTable() {
        Callback<TableColumn<Question, Void>, TableCell<Question, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Question, Void> call(final TableColumn<Question, Void> param) {
                final TableCell<Question, Void> cell = new TableCell<>() {

                    private final Button btnEdit = new Button("Sửa");
                    private final Button btnDelete = new Button("Xóa");

                    {
                        btnEdit.setOnAction((ActionEvent event) -> {
                            Question data = getTableView().getItems().get(getIndex());
                            handleEditQuestionFromCell(data);
                        });
                        btnDelete.setOnAction((ActionEvent event) -> {
                            Question data = getTableView().getItems().get(getIndex());
                            handleDeleteQuestionFromCell(data);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox pane = new HBox(btnEdit, btnDelete);
                            pane.setSpacing(10);
                            setGraphic(pane);
                        }
                    }
                };
                return cell;
            }
        };

        colAction.setCellFactory(cellFactory);
    }

    private void loadQuestions() {
        ObservableList<Question> questions = FXCollections.observableArrayList(QuestionStore.getAllQuestions());
        filteredQuestions = new FilteredList<>(questions, p -> true);
        tableQuestions.setItems(filteredQuestions);
    }

    private void loadSubjects() {
        ObservableList<Subject> subjects = FXCollections.observableArrayList(SubjectStore.getAllSubjects());
        comboSubject.setItems(subjects);
    }

    @FXML
    private void handleAddNewQuestion(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Controller/AddQuestionView.fxml"));
            Parent addQuestionRoot = loader.load();

            AddQuestionController addQuestionController = loader.getController();
            addQuestionController.setCurrentUser(currentUser);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(addQuestionRoot));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot load AddQuestionView: " + e.getMessage());
        }
    }

    @FXML
    private void handleLoadFromExcel(ActionEvent event) {
        // Implement logic: mở FileChooser, đọc file Excel, chuyển dữ liệu thành đối tượng Question
        showAlert(Alert.AlertType.INFORMATION, "Load from Excel", "Chức năng tải từ Excel đang được phát triển!");
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        String keyword = txtTuKhoa.getText().trim().toLowerCase();
        Subject selectedSubject = comboSubject.getValue();

        filteredQuestions.setPredicate(new Predicate<Question>() {
            @Override
            public boolean test(Question q) {
                if (keyword.isEmpty() && selectedSubject == null) {
                    return true;
                }
                boolean matchesKeyword = keyword.isEmpty() || q.getContent().toLowerCase().contains(keyword);

                boolean matchesSubject = (selectedSubject == null) ||
                        (q.getSubject() != null && q.getSubject().getSubjectName().equals(selectedSubject.getSubjectName()));

                return matchesKeyword && matchesSubject;
            }
        });
    }

    private void handleEditQuestionFromCell(Question question) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Controller/EditQuestionView.fxml"));
            Parent editQuestionRoot = loader.load();

            EditQuestionController editQuestionController = loader.getController();
            editQuestionController.setQuestion(question);
            editQuestionController.setCurrentUser(currentUser);

            Stage stage = (Stage) tableQuestions.getScene().getWindow();
            stage.setScene(new Scene(editQuestionRoot));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Không thể tải giao diện sửa câu hỏi: " + e.getMessage());
        }
    }

    private void handleDeleteQuestionFromCell(Question question) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận xóa");
        confirm.setHeaderText("Bạn có chắc muốn xóa câu hỏi này?");
        confirm.setContentText(question.getContent());
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            boolean deleted = Model.QuestionStore.removeQuestion(question.getQuestionId());
            if (deleted) {
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Xóa câu hỏi thành công!");
                loadQuestions();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Xóa câu hỏi thất bại!");
            }
        }
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Controller/TeacherView.fxml"));
            Parent homeRoot = loader.load();

            TeacherController teacherController = loader.getController();
            teacherController.setCurrentUser(currentUser);

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




