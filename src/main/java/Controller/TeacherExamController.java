package Controller;

import Model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class TeacherExamController implements Initializable {

    @FXML private Hyperlink btnExam, btnHome, btnPractice, btnProfile, btnQuestionBank;
    @FXML private Button btnLogout, btnThemMoi, btnTimKiem;
    @FXML private Label lblUsername, lblFullName;

    @FXML private TextField txtTuKhoa;
    @FXML private ComboBox<Subject> cbMonHoc;

    @FXML private TableView<Exam> tableDeThi;
    @FXML private TableColumn<Exam, String> colSTT;
    @FXML private TableColumn<Exam, String> colMaDeThi;
    @FXML private TableColumn<Exam, String> colTenDeThi;
    @FXML private TableColumn<Exam, String> colMonHoc;
    @FXML private TableColumn<Exam, String> colThoiGian;
    @FXML private TableColumn<Exam, Void>   colAction;
    @FXML private TableColumn<Exam, Void> colHistory;

    private ObservableList<Exam> examList;
    private FilteredList<Exam> filteredExamList;

    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            lblUsername.setText(user.getUsername());
            lblFullName.setText(user.getName());
            loadExams();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colMaDeThi.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getExamId()));
        colTenDeThi.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        colMonHoc.setCellValueFactory(cellData -> {
            Exam e = cellData.getValue();
            if(e.getSubject()!=null) return new javafx.beans.property.SimpleStringProperty(e.getSubject().getSubjectName());
            else return new javafx.beans.property.SimpleStringProperty("");
        });
        colThoiGian.setCellValueFactory(cellData -> {
            Exam e = cellData.getValue();
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM HH:mm");
            String text = "";
            if(e.getStartTime()!=null) {
                text += e.getStartTime().format(fmt);
            }
            text += " - ";
            if(e.getEndTime()!=null) {
                text += e.getEndTime().format(fmt);
            }
            return new javafx.beans.property.SimpleStringProperty(text);
        });

        colSTT.setCellFactory(col -> new TableCell<Exam, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (this.getTableRow() != null && !empty) {
                    setText(String.valueOf(this.getIndex() + 1));
                } else {
                    setText(null);
                }
            }
        });

        addActionColumn();

        ObservableList<Subject> subjectOptions = FXCollections.observableArrayList();
        subjectOptions.add(new Subject("All"));
        subjectOptions.addAll(SubjectStore.getAllSubjects());
        cbMonHoc.setItems(subjectOptions);
        cbMonHoc.setValue(subjectOptions.get(0));

        btnTimKiem.setOnAction(e -> handleSearch());
        addHistoryColumn();
    }

    private void addHistoryColumn() {
        colHistory.setCellFactory(col -> new TableCell<Exam, Void>() {
            private final Button btnHistory = new Button("Lịch sử");
            {
                btnHistory.setOnAction(e -> {
                    Exam exam = getTableView().getItems().get(getIndex());
                    openHistory(exam);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnHistory);
                }
            }
        });
    }

    private void openHistory(Exam exam) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Controller/HistoryExamAttemptView.fxml"));
            Parent root = loader.load();

            HistoryExamAttemptController ctrl = loader.getController();
            ctrl.setPractice(exam);

            Stage stage = new Stage();
            stage.setTitle("Lịch sử làm bài: " + exam.getName());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Không thể mở HistoryAttemptView: " + e.getMessage());
        }
    }

    private void loadExams() {
        List<Exam> teacherExams = ExamStore.getExamsByTeacher(currentUser.getUsername());
        examList = FXCollections.observableArrayList(teacherExams);
        filteredExamList = new FilteredList<>(examList, e->true);
        tableDeThi.setItems(filteredExamList);
    }

    private void addActionColumn() {
        colAction.setCellFactory(col -> new TableCell<Exam, Void>() {
            private final Button btnEdit = new Button("Sửa");
            private final Button btnDelete = new Button("Xóa");
            {
                btnEdit.setOnAction(e->{
                    Exam exam = getTableView().getItems().get(getIndex());
                    handleEditExam(exam);
                });
                btnDelete.setOnAction(e->{
                    Exam exam = getTableView().getItems().get(getIndex());
                    handleDeleteExam(exam);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if(empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(5, btnEdit, btnDelete);
                    setGraphic(box);
                }
            }
        });
    }

    private void handleEditExam(Exam exam) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Controller/EditExamView.fxml"));
            Parent root = loader.load();

            EditExamController ctrl = loader.getController();
            ctrl.setExam(exam);
            ctrl.setCurrentUser(currentUser);
            ctrl.loadExamData();

            Stage stage = (Stage) tableDeThi.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch(IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Không thể mở EditExamView: " + e.getMessage());
        }
    }

    private void handleDeleteExam(Exam exam) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận xóa");
        confirm.setHeaderText("Bạn có chắc muốn xóa đề kiểm tra này?");
        confirm.setContentText(exam.getName());
        if(confirm.showAndWait().orElse(ButtonType.CANCEL)==ButtonType.OK) {
            boolean deleted = ExamStore.removeExam(exam.getExamId());
            if(deleted) {
                showAlert(Alert.AlertType.INFORMATION,"Thành công","Đã xóa đề kiểm tra!");
                loadExams();
            } else {
                showAlert(Alert.AlertType.ERROR,"Error","Xóa đề thất bại!");
            }
        }
    }

    @FXML
    private void handleSearch() {
        String keyword = txtTuKhoa.getText().trim().toLowerCase();
        Subject selectedSubject = cbMonHoc.getValue();

        filteredExamList.setPredicate(exam->{
            boolean matchesKeyword = keyword.isEmpty()||exam.getName().toLowerCase().contains(keyword);
            boolean matchesSubject;
            if(selectedSubject==null || "all".equalsIgnoreCase(selectedSubject.getSubjectName())) {
                matchesSubject=true;
            } else {
                matchesSubject= exam.getSubject()!=null &&
                        exam.getSubject().getSubjectName().equalsIgnoreCase(selectedSubject.getSubjectName());
            }
            return matchesKeyword && matchesSubject;
        });
    }

    @FXML
    private void handleAddExam(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Controller/AddExamView.fxml"));
            Parent root = loader.load();

            AddExamController ctrl = loader.getController();
            ctrl.setCurrentUser(currentUser);

            Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch(IOException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR,"Error","Không thể mở AddExamView: "+ex.getMessage());
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Controller/QuestionBankView.fxml"));
            Parent questionBankRoot = loader.load();

            QuestionBankController questionBankController = loader.getController();
            questionBankController.setCurrentUser(currentUser);

            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(questionBankRoot));
            stage.show();
        } catch(IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Không thể tải giao diện QuestionBank: " + e.getMessage());
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

            TeacherExamController teacherExamController = loader.getController();
            teacherExamController.setCurrentUser(currentUser);

            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(examRoot));
            stage.show();
        } catch(IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Không thể tải giao diện TeacherExamView: " + e.getMessage());
        }
    }

    @FXML
    private void handleHome(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Controller/TeacherView.fxml"));
            Parent homeRoot = loader.load();

            TeacherController teacherController = loader.getController();
            teacherController.setCurrentUser(currentUser);

            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(homeRoot));
            stage.show();
        } catch(IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Không thể tải giao diện TeacherView: " + e.getMessage());
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


    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
