package Controller;

import Model.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class EditQuestionController implements Initializable {

    @FXML
    private TextArea txtCauHoi;
    @FXML
    private TextField txtDapAnA;
    @FXML
    private TextField txtDapAnB;
    @FXML
    private TextField txtDapAnC;
    @FXML
    private TextField txtDapAnD;
    @FXML
    private TextField txtDapAnDung;
    @FXML
    private TextField txtMonHoc;

    @FXML
    private Button btnLuu;
    @FXML
    private Button btnHuyBo;

    private Question currentQuestion;

    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void setQuestion(Question question) {
        this.currentQuestion = question;
        if (question != null) {
            txtCauHoi.setText(question.getContent());
            txtDapAnA.setText(question.getOptionA());
            txtDapAnB.setText(question.getOptionB());
            txtDapAnC.setText(question.getOptionC());
            txtDapAnD.setText(question.getOptionD());
            txtDapAnDung.setText(question.getCorrectAnswer());
            if (question.getSubject() != null) {
                txtMonHoc.setText(question.getSubject().getSubjectName());
            } else {
                txtMonHoc.setText("");
            }
        }
    }

    @FXML
    private void handleSaveChanges(ActionEvent event) {
        String content = txtCauHoi.getText().trim();
        String optionA = txtDapAnA.getText().trim();
        String optionB = txtDapAnB.getText().trim();
        String optionC = txtDapAnC.getText().trim();
        String optionD = txtDapAnD.getText().trim();
        String correctAnswer = txtDapAnDung.getText().trim().toUpperCase();
        String subjectInput = txtMonHoc.getText().trim();

        if (content.isEmpty() || optionA.isEmpty() || optionB.isEmpty() ||
                optionC.isEmpty() || optionD.isEmpty() || correctAnswer.isEmpty() || subjectInput.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Vui lòng nhập đầy đủ thông tin cho câu hỏi và môn học!");
            return;
        }
        if (!("A".equals(correctAnswer) || "B".equals(correctAnswer) ||
                "C".equals(correctAnswer) || "D".equals(correctAnswer))) {
            showAlert(Alert.AlertType.ERROR, "Error", "Đáp án đúng phải là A, B, C hoặc D!");
            return;
        }

        currentQuestion.setContent(content);
        currentQuestion.setOptionA(optionA);
        currentQuestion.setOptionB(optionB);
        currentQuestion.setOptionC(optionC);
        currentQuestion.setOptionD(optionD);
        currentQuestion.setCorrectAnswer(correctAnswer);

        Subject subject = SubjectStore.findByName(subjectInput);
        if (subject == null) {
            subject = new Subject(subjectInput);
            SubjectStore.addSubject(subject);
        }
        currentQuestion.setSubject(subject);

        QuestionStore.updateQuestion(currentQuestion);

        showAlert(Alert.AlertType.INFORMATION, "Success", "Câu hỏi đã được cập nhật thành công!");
        navigateToQuestionBank(event);
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        navigateToQuestionBank(event);
    }

    private void navigateToQuestionBank(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Controller/QuestionBankView.fxml"));
            Parent questionBankRoot = loader.load();

            QuestionBankController questionBankController = loader.getController();
            questionBankController.setCurrentUser(currentUser);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(questionBankRoot));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot load QuestionBankView: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
