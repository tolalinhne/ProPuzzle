package Controller;

import Model.*;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.web.WebView;

import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.image.Image;
import Utils.LaTexRenderer;

public class AddQuestionController implements Initializable {

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
    private Button btnInsertFormula;
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnCancel;
    @FXML
    private WebView webViewPreview;

    private FilteredList<Question> filteredQuestions;

    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML
    private void handleInsertFormula(ActionEvent event) {
        // Tạo dialog cho user nhập LaTeX
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Chèn công thức");
        dialog.setHeaderText("Nhập công thức toán học theo định dạng LaTeX:");
        dialog.setContentText("Công thức:");

        dialog.showAndWait().ifPresent(formulaInput -> {
            String latexSyntax = "$$" + formulaInput + "$$";

            txtCauHoi.appendText(" " + latexSyntax + " ");

            updatePreview();
        });
    }


    private void updatePreview() {
        String textWithLatex = txtCauHoi.getText();

        String htmlContent = """
        <html>
          <head>
            <script>
              MathJax = {
                tex: {inlineMath: [['$', '$'], ['\\\\(', '\\\\)']]},
                svg: {fontCache: 'global'}
              };
            </script>
            <script src="https://cdn.jsdelivr.net/npm/mathjax@3/es5/tex-chtml.js"></script>
          </head>
          <body>
            REPLACE_CONTENT
          </body>
        </html>
        """;


        htmlContent = htmlContent.replace("REPLACE_CONTENT", textWithLatex);

        webViewPreview.getEngine().loadContent(htmlContent, "text/html");
    }
    @FXML
    private void handleLoadFromExcel(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Load from Excel", "Chức năng tải từ Excel đang được phát triển!");
    }

    @FXML
    private void handleAddQuestion(ActionEvent event) {
        String questionContent = txtCauHoi.getText().trim();
        String optionA = txtDapAnA.getText().trim();
        String optionB = txtDapAnB.getText().trim();
        String optionC = txtDapAnC.getText().trim();
        String optionD = txtDapAnD.getText().trim();
        String correctAnswer = txtDapAnDung.getText().trim().toUpperCase();
        String subjectInput = txtMonHoc.getText().trim();

        if (questionContent.isEmpty() || optionA.isEmpty() || optionB.isEmpty() ||
                optionC.isEmpty() || optionD.isEmpty() || correctAnswer.isEmpty() || subjectInput.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Vui lòng nhập đầy đủ thông tin cho câu hỏi và môn học!");
            return;
        }
        if (!("A".equals(correctAnswer) || "B".equals(correctAnswer) ||
                "C".equals(correctAnswer) || "D".equals(correctAnswer))) {
            showAlert(Alert.AlertType.ERROR, "Error", "Đáp án đúng phải là A, B, C hoặc D!");
            return;
        }

        Subject subject = SubjectStore.findByName(subjectInput);
        if (subject == null) {
            subject = new Subject(subjectInput);
            SubjectStore.addSubject(subject);
        }

        Question newQuestion = new Question("Q" + System.currentTimeMillis(),
                questionContent, optionA, optionB, optionC, optionD, correctAnswer);
        newQuestion.setSubject(subject);

        QuestionStore.addQuestion(newQuestion);

        showAlert(Alert.AlertType.INFORMATION, "Success", "Câu hỏi đã được thêm thành công!");
        handleCancel(event);
    }

    @FXML
    private void handleCancel(ActionEvent event) {
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

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
