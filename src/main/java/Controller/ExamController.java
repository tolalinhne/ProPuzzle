package Controller;

import Model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ExamController implements Initializable {

    @FXML private Label lblTitle;
    @FXML private TextArea txtQuestionContent;
    @FXML private RadioButton rdoA, rdoB, rdoC, rdoD;
    @FXML private ToggleGroup toggleGroupAnswer;
    @FXML private Button btnPrevious, btnNext;

    @FXML private TextField txtSubject;
    @FXML private TextField txtPracticeName;
    @FXML private TextField txtName;

    @FXML private ListView<Integer> listQuestionNumber;

    @FXML private Button btnSubmit;
    @FXML private Button btnExit;

    // Dữ liệu thi
    private List<String> questionIdList;
    private String[] userAnswers;
    private int currentIndex = 0;
    private User currentUser;
    private Practice currentPractice;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listQuestionNumber.setCellFactory(lv -> new ListCell<Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText("Câu " + item);
                    if (userAnswers != null && userAnswers.length >= item && userAnswers[item - 1] != null) {
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });
        listQuestionNumber.setOnMouseClicked(event -> {
            Integer selected = listQuestionNumber.getSelectionModel().getSelectedItem();
            if (selected != null) {
                saveUserChoice();
                displayQuestion(selected - 1);
            }
        });
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            txtName.setText(user.getName());
        }
    }

    public void setPractice(Practice practice) {
        this.currentPractice = practice;
        if (practice != null) {
            if (practice.getSubject() != null) {
                txtSubject.setText(practice.getSubject().getSubjectName());
            } else {
                txtSubject.setText("");
            }
            txtPracticeName.setText(practice.getName());

            List<String> originalList = practice.getQuestionIds();
            if (originalList != null) {
                this.questionIdList = new ArrayList<>(originalList);
                java.util.Collections.shuffle(this.questionIdList);
            } else {
                this.questionIdList = FXCollections.observableArrayList();
            }

            userAnswers = new String[questionIdList.size()];

            displayQuestion(0);

            ObservableList<Integer> numbers = FXCollections.observableArrayList();
            for (int i = 1; i <= questionIdList.size(); i++) {
                numbers.add(i);
            }
            listQuestionNumber.setItems(numbers);
        }
    }

    private void displayQuestion(int index) {
        if (index < 0 || index >= questionIdList.size()) return;
        currentIndex = index;
        String questionId = questionIdList.get(currentIndex);
        Question q = QuestionStore.getQuestionById(questionId);
        if (q == null) {
            txtQuestionContent.setText("Không tìm thấy câu hỏi với ID: " + questionId);
            rdoA.setText("A. ");
            rdoB.setText("B. ");
            rdoC.setText("C. ");
            rdoD.setText("D. ");
        } else {
            lblTitle.setText("Câu " + (currentIndex + 1));
            txtQuestionContent.setText(q.getContent());
            rdoA.setText("A. " + q.getOptionA());
            rdoB.setText("B. " + q.getOptionB());
            rdoC.setText("C. " + q.getOptionC());
            rdoD.setText("D. " + q.getOptionD());
        }
        String chosen = userAnswers[currentIndex];
        if ("A".equals(chosen)) rdoA.setSelected(true);
        else if ("B".equals(chosen)) rdoB.setSelected(true);
        else if ("C".equals(chosen)) rdoC.setSelected(true);
        else if ("D".equals(chosen)) rdoD.setSelected(true);
        else toggleGroupAnswer.selectToggle(null);

        listQuestionNumber.getSelectionModel().select(currentIndex + 1);
        listQuestionNumber.refresh();
    }

    private void saveUserChoice() {
        RadioButton selected = (RadioButton) toggleGroupAnswer.getSelectedToggle();
        if (selected != null) {
            if (selected == rdoA) userAnswers[currentIndex] = "A";
            else if (selected == rdoB) userAnswers[currentIndex] = "B";
            else if (selected == rdoC) userAnswers[currentIndex] = "C";
            else if (selected == rdoD) userAnswers[currentIndex] = "D";
        } else {
            userAnswers[currentIndex] = null;
        }
        listQuestionNumber.refresh();
    }

    @FXML
    private void handlePrevious(ActionEvent event) {
        saveUserChoice();
        if (currentIndex > 0) {
            displayQuestion(currentIndex - 1);
        }
    }

    @FXML
    private void handleNext(ActionEvent event) {
        saveUserChoice();
        if (currentIndex < questionIdList.size() - 1) {
            displayQuestion(currentIndex + 1);
        }
    }

    @FXML
    private void handleSubmit(ActionEvent event) {
        saveUserChoice();
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Bạn chắc chắn nộp bài? Bài thi sẽ được chấm ngay.",
                ButtonType.OK, ButtonType.CANCEL);
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            double score = calculateScore(); // Tạo 1 hàm calculateScore()

            Attempt att = new Attempt(
                    currentUser.getUsername(),
                    currentPractice.getPracticeId(),
                    score,
                    LocalDateTime.now()
            );
            AttemptStore.addAttempt(att);

            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("Kết quả");
            info.setHeaderText(null);
            info.setContentText("Bạn được " + score + " điểm.");
            info.showAndWait();

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Controller/StudentView.fxml"));
                Parent root = loader.load();
                StudentController sc = loader.getController();
                sc.setCurrentUser(currentUser);
                Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Không thể chuyển về StudentView: " + e.getMessage());
            }
        }
    }

    private double calculateScore() {
        int score = 0;
        for (int i = 0; i < questionIdList.size(); i++) {
            String questionId = questionIdList.get(i);
            Question q = QuestionStore.getQuestionById(questionId);
            if (q != null && q.getCorrectAnswer().equalsIgnoreCase(userAnswers[i])) {
                score++;
            }
        }
        return score * 10 / questionIdList.size();
    }

    @FXML
    private void handleExit(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Controller/StudentView.fxml"));
            Parent root = loader.load();

            StudentController sc = loader.getController();
            sc.setCurrentUser(currentUser);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Không thể chuyển về StudentView: " + e.getMessage());
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
