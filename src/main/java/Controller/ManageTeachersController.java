package Controller;

import Model.User;
import Model.UserStore;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Pair;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ManageTeachersController implements Initializable {

    @FXML
    private TableView<User> tableStudents;
    @FXML
    private TableColumn<User, String> colUsername;
    @FXML
    private TableColumn<User, String> colFullName;
    @FXML
    private TableColumn<User, Void> colActions;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadTeacherData();
    }

    private void loadTeacherData() {
        ObservableList<User> students = FXCollections.observableArrayList(
                UserStore.getAllUsers().stream()
                        .filter(u -> "Giảng Viên".equalsIgnoreCase(u.getRole()))
                        .collect(Collectors.toList())
        );

        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colFullName.setCellValueFactory(new PropertyValueFactory<>("name"));

        Callback<TableColumn<User, Void>, TableCell<User, Void>> cellFactory = new Callback<TableColumn<User, Void>, TableCell<User, Void>>() {
            @Override
            public TableCell<User, Void> call(final TableColumn<User, Void> param) {
                return new TableCell<User, Void>() {

                    private final Button btnDelete = new Button("Xóa");
                    private final Button btnChangePassword = new Button("Đổi mật khẩu");

                    {
                        btnDelete.setOnAction((ActionEvent event) -> {
                            User student = getTableView().getItems().get(getIndex());
                            boolean result = UserStore.deleteUser(student);
                            if (result) {
                                showAlert(Alert.AlertType.INFORMATION, "Xóa", "Xóa sinh viên thành công!");
                                loadTeacherData();
                            } else {
                                showAlert(Alert.AlertType.ERROR, "Xóa", "Xóa sinh viên thất bại!");
                            }
                        });

                        btnChangePassword.setOnAction((ActionEvent event) -> {
                            User teacher = getTableView().getItems().get(getIndex());

                            Dialog<Pair<String, String>> dialog = new Dialog<>();
                            dialog.setTitle("Đổi mật khẩu");
                            dialog.setHeaderText("Nhập mật khẩu mới cho giảng viên: " + teacher.getUsername());

                            ButtonType changeButtonType = new ButtonType("Đổi mật khẩu", ButtonBar.ButtonData.OK_DONE);
                            dialog.getDialogPane().getButtonTypes().addAll(changeButtonType, ButtonType.CANCEL);

                            GridPane grid = new GridPane();
                            grid.setHgap(10);
                            grid.setVgap(10);

                            PasswordField passwordField = new PasswordField();
                            passwordField.setPromptText("Mật khẩu mới");
                            PasswordField confirmPasswordField = new PasswordField();
                            confirmPasswordField.setPromptText("Xác nhận mật khẩu");

                            grid.add(new Label("Mật khẩu:"), 0, 0);
                            grid.add(passwordField, 1, 0);
                            grid.add(new Label("Xác nhận:"), 0, 1);
                            grid.add(confirmPasswordField, 1, 1);

                            dialog.getDialogPane().setContent(grid);

                            dialog.setResultConverter(dialogButton -> {
                                if (dialogButton == changeButtonType) {
                                    return new Pair<>(passwordField.getText(), confirmPasswordField.getText());
                                }
                                return null;
                            });

                            Optional<Pair<String, String>> result = dialog.showAndWait();

                            result.ifPresent(passwords -> {
                                String newPassword = passwords.getKey();
                                String confirmPassword = passwords.getValue();

                                if (newPassword.trim().isEmpty()) {
                                    showAlert(Alert.AlertType.ERROR, "Đổi mật khẩu", "Mật khẩu không được để trống!");
                                } else if (!newPassword.equals(confirmPassword)) {
                                    showAlert(Alert.AlertType.ERROR, "Đổi mật khẩu", "Mật khẩu không khớp. Vui lòng thử lại!");
                                } else {
                                    // Cập nhật mật khẩu cho sinh viên
                                    boolean updated = UserStore.updatePassword(teacher, newPassword);
                                    if (updated) {
                                        showAlert(Alert.AlertType.INFORMATION, "Đổi mật khẩu", "Đổi mật khẩu thành công cho sinh viên " + teacher.getUsername());
                                    } else {
                                        showAlert(Alert.AlertType.ERROR, "Đổi mật khẩu", "Cập nhật mật khẩu thất bại!");
                                    }
                                }
                            });
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox hbox = new HBox(10, btnDelete, btnChangePassword);
                            setGraphic(hbox);
                        }
                    }
                };
            }
        };
        colActions.setCellFactory(cellFactory);

        tableStudents.setItems(students);
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
