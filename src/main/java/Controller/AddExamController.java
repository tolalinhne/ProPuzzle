package Controller;

import Model.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.List;

public class AddExamController implements Initializable {

    @FXML private TextField txtExamName;
    @FXML private TextField txtPass;
    @FXML private DatePicker dateStart;
    @FXML private TextField txtTimeStart;
    @FXML private DatePicker dateEnd;
    @FXML private TextField txtTimeEnd;

    @FXML private ComboBox<Subject> cbMonHoc;
    @FXML private TableView<Question> tableQuestions;
    @FXML private TableColumn<Question,String> colQuestionId;
    @FXML private TableColumn<Question,String> colQuestionContent;
    @FXML private TableColumn<Question,String> colAnswer;
    @FXML private Button btnCreateExam, btnCancel;

    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colQuestionId.setCellValueFactory(cell->new javafx.beans.property.SimpleStringProperty(cell.getValue().getQuestionId()));
        colQuestionContent.setCellValueFactory(cell->new javafx.beans.property.SimpleStringProperty(cell.getValue().getContent()));
        colAnswer.setCellValueFactory(cell->new javafx.beans.property.SimpleStringProperty(cell.getValue().getCorrectAnswer()));
        tableQuestions.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // load question
        List<Question> allQ=QuestionStore.getAllQuestions();
        tableQuestions.setItems(FXCollections.observableArrayList(allQ));

        // combo subject
        cbMonHoc.setItems(FXCollections.observableArrayList(SubjectStore.getAllSubjects()));
    }

    @FXML
    private void handleCreateExam(ActionEvent e) {
        String examName = txtExamName.getText().trim();
        String pass     = txtPass.getText().trim();
        if(examName.isEmpty()||pass.isEmpty()) {
            showAlert(Alert.AlertType.ERROR,"Error","Vui lòng nhập Tên đề & Mật khẩu!");
            return;
        }

        // Lấy time
        LocalDate dStart = dateStart.getValue();
        LocalDate dEnd   = dateEnd.getValue();
        if(dStart==null||dEnd==null) {
            showAlert(Alert.AlertType.ERROR,"Error","Chưa chọn ngày bắt đầu/kết thúc!");
            return;
        }
        LocalTime tStart, tEnd;
        try {
            tStart = LocalTime.parse(txtTimeStart.getText().trim()); // "HH:mm"
            tEnd   = LocalTime.parse(txtTimeEnd.getText().trim());
        } catch(Exception ex) {
            showAlert(Alert.AlertType.ERROR,"Error","Thời gian nhập sai định dạng (HH:mm)!");
            return;
        }

        LocalDateTime startTime = LocalDateTime.of(dStart,tStart);
        LocalDateTime endTime   = LocalDateTime.of(dEnd,tEnd);

        // Lấy subject
        Subject s = cbMonHoc.getValue();

        // Lấy question
        List<Question> selectedQ=tableQuestions.getSelectionModel().getSelectedItems();
        if(selectedQ.isEmpty()) {
            showAlert(Alert.AlertType.ERROR,"Error","Chưa chọn câu hỏi!");
            return;
        }

        // Tạo exam
        Exam exam = new Exam();
        exam.setExamId("EX"+System.currentTimeMillis());
        exam.setName(examName);
        exam.setTeacherUsername(currentUser.getUsername());
        exam.setSubject(s);
        exam.setStartTime(startTime);
        exam.setEndTime(endTime);
        exam.setPass(pass);

        // questionIds
        List<String> qids = selectedQ.stream().map(Question::getQuestionId).collect(Collectors.toList());
        exam.setQuestionIds(qids);

        // Lưu
        ExamStore.addExam(exam);
        showAlert(Alert.AlertType.INFORMATION,"Success","Tạo đề kiểm tra thành công!");
        handleCancel(e);
    }

    @FXML
    private void handleCancel(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Controller/TeacherExamView.fxml"));
            Parent root = loader.load();

            TeacherExamController ctrl = loader.getController();
            ctrl.setCurrentUser(currentUser);

            Stage stage=(Stage)((Node)e.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch(IOException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR,"Error","Không thể mở TeacherExamView: "+ex.getMessage());
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
