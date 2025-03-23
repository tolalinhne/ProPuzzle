package Controller;

import Model.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class StudentPracticeController implements Initializable {

    @FXML private Hyperlink btnExam, btnHome, btnPractice, btnProfile;
    @FXML private Button btnLogout;
    @FXML private Label lblUsername, lblFullName;

    @FXML private TableView<Practice> tableSaved;
    @FXML private TableColumn<Practice, String> colSavedName;
    @FXML private TableColumn<Practice, String> colSavedSubject;
    @FXML private TableColumn<Practice, Void>  colSavedAction;

    @FXML private TableView<Practice> tableRecent;
    @FXML private TableColumn<Practice, String> colRecentName;
    @FXML private TableColumn<Practice, String> colRecentScore;
    @FXML private TableColumn<Practice, Void>  colRecentAction;

    private User currentUser;

    private Map<String, Double> practiceScoreMap = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colSavedName.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));
        colSavedSubject.setCellValueFactory(cellData -> {
            Practice p = cellData.getValue();
            if (p.getSubject() != null) {
                return new SimpleStringProperty(p.getSubject().getSubjectName());
            } else {
                return new SimpleStringProperty("");
            }
        });
        colSavedAction.setCellFactory(col -> new TableCell<Practice, Void>() {
            private final Button btnDoExam = new Button("Làm bài");
            private final Button btnRemove = new Button("Xóa");
            {
                btnDoExam.setOnAction(e -> {
                    Practice p = getTableView().getItems().get(getIndex());
                    openExam(p);
                });
                btnRemove.setOnAction(e -> {
                    Practice p = getTableView().getItems().get(getIndex());
                    removeFavoritePractice(p);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(5, btnDoExam, btnRemove);
                    setGraphic(box);
                }
            }
        });

        colRecentName.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));

        colRecentScore.setCellValueFactory(cellData -> {
            Practice p = cellData.getValue();
            Double score = practiceScoreMap.get(p.getPracticeId());
            if (score == null) {
                return new SimpleStringProperty("");
            } else {
                return new SimpleStringProperty(String.valueOf(score));
            }
        });

        colRecentAction.setCellFactory(col -> new TableCell<Practice, Void>() {
            private final Button btnDoExam = new Button("Làm bài");
            private final Button btnHistory = new Button("Lịch sử");
            {
                btnDoExam.setOnAction(e -> {
                    Practice p = getTableView().getItems().get(getIndex());
                    openExam(p);
                });
                btnHistory.setOnAction(e -> {
                    Practice p = getTableView().getItems().get(getIndex());
                    openHistory(p);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(5, btnDoExam, btnHistory);
                    setGraphic(box);
                }
            }
        });
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            lblUsername.setText(user.getUsername());
            lblFullName.setText(user.getName());

            loadSavedPractices();
            loadRecentPractices();
        }
    }

    private void loadSavedPractices() {
        if (currentUser == null) return;

        List<String> favIds = currentUser.getFavoritePracticeIds();
        if (favIds == null || favIds.isEmpty()) {
            tableSaved.setItems(FXCollections.observableArrayList());
            return;
        }
        List<Practice> savedPractices = new ArrayList<>();
        for (String pid : favIds) {
            Practice p = PracticeStore.getAllPractices().stream()
                    .filter(x -> x.getPracticeId().equalsIgnoreCase(pid))
                    .findFirst().orElse(null);
            if (p != null) savedPractices.add(p);
        }
        tableSaved.setItems(FXCollections.observableArrayList(savedPractices));
    }

    private void removeFavoritePractice(Practice p) {
        currentUser.getFavoritePracticeIds().removeIf(id -> id.equalsIgnoreCase(p.getPracticeId()));
        UserStore.updateUser(currentUser);
        loadSavedPractices();
    }

    private void loadRecentPractices() {
        if (currentUser == null) {
            tableRecent.setItems(FXCollections.observableArrayList());
            return;
        }
        List<Attempt> allAttempts = AttemptStore.getAllAttempts().stream()
                .filter(a -> a.getUserId().equalsIgnoreCase(currentUser.getUsername()))
                .collect(Collectors.toList());
        if (allAttempts.isEmpty()) {
            tableRecent.setItems(FXCollections.observableArrayList());
            return;
        }
        allAttempts.sort((a,b)->b.getTimestamp().compareTo(a.getTimestamp()));
        Map<String, Attempt> latestMap = new LinkedHashMap<>();
        for (Attempt at : allAttempts) {
            if (!latestMap.containsKey(at.getPracticeId())) {
                latestMap.put(at.getPracticeId(), at);
            }
        }
        List<Practice> recents = new ArrayList<>();
        practiceScoreMap.clear();

        for (Attempt a : latestMap.values()) {
            Practice p = PracticeStore.getAllPractices().stream()
                    .filter(x -> x.getPracticeId().equalsIgnoreCase(a.getPracticeId()))
                    .findFirst().orElse(null);
            if (p != null) {
                recents.add(p);
                practiceScoreMap.put(p.getPracticeId(), a.getScore());
            }
        }
        List<Practice> top5 = recents.stream().limit(5).collect(Collectors.toList());
        tableRecent.setItems(FXCollections.observableArrayList(top5));
    }

    private void openExam(Practice p) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Controller/ExamView.fxml"));
            Parent examRoot = loader.load();

            ExamController examController = loader.getController();
            examController.setCurrentUser(currentUser);
            examController.setPractice(p);

            Stage stage = (Stage) lblUsername.getScene().getWindow();
            stage.setScene(new Scene(examRoot));
            stage.show();
        } catch(IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Không thể mở ExamView: " + e.getMessage());
        }
    }

    private void openHistory(Practice p) {
        List<Attempt> attempts = AttemptStore.getAttemptsByUserAndPractice(currentUser.getUsername(), p.getPracticeId());
        if (attempts.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Lịch sử", "Chưa có lịch sử làm bài.");
            return;
        }
        attempts.sort((a,b)->b.getTimestamp().compareTo(a.getTimestamp()));
        StringBuilder sb = new StringBuilder("Lịch sử làm bài của: " + p.getName() + "\n");
        int count = 1;
        for (Attempt a : attempts) {
            sb.append("Lần ").append(count++).append(": ")
                    .append(a.getScore()).append("đ (").append(a.getTimestamp()).append(")\n");
        }
        showAlert(Alert.AlertType.INFORMATION, "Lịch sử", sb.toString());
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
    private void handlePracticeExam(ActionEvent event) {
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
