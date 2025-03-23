package Controller;

import Model.*;
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

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class PracticeBySubjectController implements Initializable {

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
    private TableView<Practice> tableDeThi;
    @FXML
    private TableColumn<Practice, String> colSTT;
    @FXML
    private TableColumn<Practice, String> colMaDeThi;
    @FXML
    private TableColumn<Practice, String> colTenDeThi;
    @FXML
    private TableColumn<Practice, Void> colAction;
    @FXML
    private TableColumn<Practice, Void> colHistory;


    @FXML private TextField txtTuKhoa;
    @FXML private Button btnTimKiem;
    @FXML private ComboBox<Subject> cbMonHoc;

    private ObservableList<Practice> practiceList;
    private FilteredList<Practice> filteredPracticeList;

    @FXML
    private Label lblUsername;

    @FXML
    private Label lblFullName;

    private String subjectName;

    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            lblUsername.setText(user.getUsername());
            lblFullName.setText(user.getName());

            loadPractices();
        }
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
        loadPracticeData();
    }

    public void loadPracticeData() {
        List<Practice> allPractices = PracticeStore.getAllPractices();
        if (subjectName == null || subjectName.trim().isEmpty() || subjectName.equalsIgnoreCase("All")) {
            practiceList = FXCollections.observableArrayList(allPractices);
        } else {
            List<Practice> filtered = allPractices.stream()
                    .filter(p -> p.getSubject() != null &&
                            p.getSubject().getSubjectName().equalsIgnoreCase(subjectName))
                    .collect(Collectors.toList());
            practiceList = FXCollections.observableArrayList(filtered);
        }
        filteredPracticeList = new FilteredList<>(practiceList, p -> true);
        tableDeThi.setItems(filteredPracticeList);
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

        ObservableList<Subject> subjectOptions = FXCollections.observableArrayList();
        subjectOptions.add(new Subject("All"));
        subjectOptions.addAll(SubjectStore.getAllSubjects());
        cbMonHoc.setItems(subjectOptions);
        cbMonHoc.setValue(subjectOptions.get(0)); // Mặc định "All"

        cbMonHoc.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                setSubjectName(newVal.getSubjectName());
            }
        });

        btnTimKiem.setOnAction(event -> handleSearch(event));
        addActionButtons();
        addHistoryColumn();
    }

    private void addActionButtons() {
        colAction.setCellFactory(col -> new TableCell<Practice, Void>() {
            private final Button btnSave = new Button("Lưu");
            private final Button btnOpen = new Button("Mở");

            {
                btnSave.setOnAction(e -> {
                    Practice practice = getTableView().getItems().get(getIndex());
                    handleSavePractice(practice);
                });
                btnOpen.setOnAction(e -> {
                    Practice practice = getTableView().getItems().get(getIndex());
                    handleOpenPractice(practice);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(5, btnSave, btnOpen);
                    setGraphic(box);
                }
            }
        });
    }

    private void addHistoryColumn() {
        colHistory.setCellFactory(col -> new TableCell<Practice, Void>() {
            private final Label lbl = new Label();

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Practice practice = getTableView().getItems().get(getIndex());
                    List<Attempt> attempts = AttemptStore.getAttemptsByUserAndPractice(
                            currentUser.getUsername(),
                            practice.getPracticeId()
                    );
                    if (attempts.isEmpty()) {
                        lbl.setText("Chưa có lịch sử");
                    } else {
                        attempts.sort((a, b) -> a.getTimestamp().compareTo(b.getTimestamp()));
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
                        StringBuilder sb = new StringBuilder();
                        int count = 1;
                        for (Attempt a : attempts) {
                            sb.append("Lần ").append(count++)
                                    .append(": ").append(a.getScore())
                                    .append("đ (").append(a.getTimestamp().format(formatter))
                                    .append(")").append("\n");
                        }
                        lbl.setText(sb.toString());
                    }
                    setGraphic(lbl);
                }
            }
        });
    }

    private void handleSavePractice(Practice practice) {
        if (currentUser == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Bạn phải đăng nhập trước.");
            return;
        }
        currentUser.addFavoritePractice(practice.getPracticeId());

        UserStore.updateUser(currentUser);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Lưu đề");
        alert.setHeaderText(null);
        alert.setContentText("Bạn đã lưu đề: " + practice.getName() + " cho user " + currentUser.getUsername());
        alert.showAndWait();
    }

    private void handleOpenPractice(Practice practice) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Controller/ExamView.fxml"));
            Parent examRoot = loader.load();

            ExamController examController = loader.getController();
            examController.setCurrentUser(currentUser);
            examController.setPractice(practice);

            Stage stage = (Stage) tableDeThi.getScene().getWindow();
            stage.setScene(new Scene(examRoot));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Không thể mở ExamView: " + e.getMessage());
        }
    }

    private void loadPractices() {
        List<Practice> teacherPractices = PracticeStore.getPracticesByTeacher(currentUser.getUsername());
        practiceList = FXCollections.observableArrayList(teacherPractices);
        filteredPracticeList = new FilteredList<>(practiceList, p -> true);
        tableDeThi.setItems(filteredPracticeList);  // Sử dụng FilteredList ở đây
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
    private void handlePractice(ActionEvent event) {
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
