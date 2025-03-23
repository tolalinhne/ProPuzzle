package Controller;

import Model.Practice;
import Model.PracticeStore;
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
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class TeacherPracticeController implements Initializable {

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
    private Button btnThemMoi;

    @FXML
    private TableView<Practice> tableDeThi;
    @FXML
    private TableColumn<Practice, String> colSTT;
    @FXML
    private TableColumn<Practice, String> colMaDeThi;
    @FXML
    private TableColumn<Practice, String> colTenDeThi;

    @FXML private TextField txtTuKhoa;
    @FXML private Button btnTimKiem;
    @FXML private ComboBox<Subject> cbMonHoc;

    @FXML
    private TableColumn<Practice, Void> colAction;
    @FXML
    private TableColumn<Practice, Void> colHistory;


    private ObservableList<Practice> practiceList;
    private FilteredList<Practice> filteredPracticeList;

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

            loadPractices();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colMaDeThi.setCellValueFactory(new PropertyValueFactory<>("practiceId"));
        colTenDeThi.setCellValueFactory(new PropertyValueFactory<>("name"));

        colSTT.setCellFactory(col -> new TableCell<Practice, String>() {
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

        addActionButtons();

        ObservableList<Subject> subjectOptions = FXCollections.observableArrayList();
        subjectOptions.add(new Subject("All"));
        subjectOptions.addAll(SubjectStore.getAllSubjects());
        cbMonHoc.setItems(subjectOptions);
        cbMonHoc.setValue(subjectOptions.get(0)); // Mặc định "All"

        btnTimKiem.setOnAction(event -> handleSearch(event));

        addHistoryColumn();
    }

    private void loadPractices() {
        List<Practice> teacherPractices = PracticeStore.getPracticesByTeacher(currentUser.getUsername());
        practiceList = FXCollections.observableArrayList(teacherPractices);
        filteredPracticeList = new FilteredList<>(practiceList, p -> true);
        tableDeThi.setItems(filteredPracticeList);
    }

    private void addHistoryColumn() {
        colHistory.setCellFactory(col -> new TableCell<Practice, Void>() {
            private final Button btnHistory = new Button("Lịch sử");
            {
                btnHistory.setOnAction(e -> {
                    Practice practice = getTableView().getItems().get(getIndex());
                    openHistory(practice);
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

    private void openHistory(Practice practice) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Controller/HistoryPracticeAttemptView.fxml"));
            Parent root = loader.load();
            HistoryPracticeAttemptController ctrl = loader.getController();
            ctrl.setPractice(practice);

            Stage stage = new Stage();
            stage.setTitle("Lịch sử làm bài: " + practice.getName());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Không thể mở HistoryAttemptView: " + e.getMessage());
        }
    }


    @FXML
    private void handleSearch(ActionEvent event) {
        String keyword = txtTuKhoa.getText().trim().toLowerCase();
        Subject selectedSubject = cbMonHoc.getValue();

        filteredPracticeList.setPredicate(practice -> {
            boolean matchesKeyword = keyword.isEmpty() || practice.getName().toLowerCase().contains(keyword);
            boolean matchesSubject;
            if (selectedSubject == null || "all".equalsIgnoreCase(selectedSubject.getSubjectName())) {
                matchesSubject = true;
            } else {
                matchesSubject = practice.getSubject() != null &&
                        practice.getSubject().getSubjectName().equalsIgnoreCase(selectedSubject.getSubjectName());
            }
            return matchesKeyword && matchesSubject;
        });
    }

    private void addActionButtons() {
        Callback<TableColumn<Practice, Void>, TableCell<Practice, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Practice, Void> call(final TableColumn<Practice, Void> param) {
                final TableCell<Practice, Void> cell = new TableCell<>() {
                    private final Button btnEdit = new Button("Sửa");
                    private final Button btnDelete = new Button("Xóa");

                    {
                        btnEdit.setOnAction((ActionEvent event) -> {
                            Practice practice = getTableView().getItems().get(getIndex());
                            handleEditPractice(practice);
                        });
                        btnDelete.setOnAction((ActionEvent event) -> {
                            Practice practice = getTableView().getItems().get(getIndex());
                            handleDeletePractice(practice);
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

    private void handleEditPractice(Practice practice) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Controller/EditPracticeView.fxml"));
            Parent editPracticeRoot = loader.load();

            EditPracticeController editPracticeController = loader.getController();
            editPracticeController.setPractice(practice);
            editPracticeController.setCurrentUser(currentUser);
            editPracticeController.loadPracticeData();
            Stage stage = (Stage) tableDeThi.getScene().getWindow();
            stage.setScene(new Scene(editPracticeRoot));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Không thể tải giao diện sửa đề luyện tập: " + e.getMessage());
        }
    }

    private void handleDeletePractice(Practice practice) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận xóa");
        confirm.setHeaderText("Bạn có chắc muốn xóa đề luyện tập này?");
        confirm.setContentText(practice.getName());
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            boolean deleted = PracticeStore.removePractice(practice.getPracticeId());
            if (deleted) {
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Xóa đề luyện tập thành công!");
                loadPractices(); // Làm mới bảng
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Xóa đề luyện tập thất bại!");
            }
        }
    }

    @FXML
    private void handleAddPractice(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Controller/AddPracticeView.fxml"));
            Parent addPracticeRoot = loader.load();

            AddPracticeController addPracticeController = loader.getController();
            addPracticeController.setCurrentUser(currentUser);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(addPracticeRoot));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot load AddPracticeView: " + e.getMessage());
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
    private void handlePractice(ActionEvent event) {
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

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
