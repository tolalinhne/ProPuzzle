package Controller;

import Model.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
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
import java.util.List;
import java.util.stream.Collectors;

public class EditExamController implements javafx.fxml.Initializable {

    @FXML private TextField txtExamName, txtPass, txtTimeStart, txtTimeEnd;
    @FXML private DatePicker dateStart, dateEnd;
    @FXML private ComboBox<Subject> cbMonHoc;
    @FXML private TableView<Question> tableQuestions;
    @FXML private TableColumn<Question,String> colQuestionId,colQuestionContent,colAnswer;
    @FXML private Button btnSave, btnCancel;

    private User currentUser;

    private Exam currentExam;

    public void setCurrentUser(User user) {
        this.currentUser=user;
    }

    public void setExam(Exam exam) {
        this.currentExam=exam;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colQuestionId.setCellValueFactory(cell-> new SimpleStringProperty(cell.getValue().getQuestionId()));
        colQuestionContent.setCellValueFactory(cell-> new SimpleStringProperty(cell.getValue().getContent()));
        colAnswer.setCellValueFactory(cell-> new SimpleStringProperty(cell.getValue().getCorrectAnswer()));

        tableQuestions.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        cbMonHoc.setItems(FXCollections.observableArrayList(SubjectStore.getAllSubjects()));
    }

    public void loadExamData() {
        if(currentExam==null) return;
        txtExamName.setText(currentExam.getName());
        txtPass.setText(currentExam.getPass());

        // set date/time
        if(currentExam.getStartTime()!=null) {
            dateStart.setValue(currentExam.getStartTime().toLocalDate());
            txtTimeStart.setText(currentExam.getStartTime().toLocalTime().toString());
        }

        if(currentExam.getEndTime()!=null) {
            dateEnd.setValue(currentExam.getEndTime().toLocalDate());
            txtTimeEnd.setText(currentExam.getEndTime().toLocalTime().toString());
        }

        // subject
        if(currentExam.getSubject()!=null) {
            cbMonHoc.setValue(currentExam.getSubject());
        }

        // load question
        List<Question> allQ=QuestionStore.getAllQuestions();
        tableQuestions.setItems(FXCollections.observableArrayList(allQ));

        // Chọn sẵn questionIds
        List<String> qids = currentExam.getQuestionIds();
        for(int i=0; i<allQ.size(); i++){
            if(qids.contains(allQ.get(i).getQuestionId())){
                tableQuestions.getSelectionModel().select(i);
            }
        }
    }

    @FXML
    private void handleSave(ActionEvent e) {
        String examName = txtExamName.getText().trim();
        String pass     = txtPass.getText().trim();
        if(examName.isEmpty()||pass.isEmpty()) {
            showAlert(Alert.AlertType.ERROR,"Error","Vui lòng nhập Tên đề & Mật khẩu!");
            return;
        }

        LocalDate dStart = dateStart.getValue();
        LocalDate dEnd   = dateEnd.getValue();
        if(dStart==null||dEnd==null){
            showAlert(Alert.AlertType.ERROR,"Error","Chưa chọn ngày bắt đầu/kết thúc!");
            return;
        }
        LocalTime tStart,tEnd;
        try {
            tStart = LocalTime.parse(txtTimeStart.getText().trim());
            tEnd   = LocalTime.parse(txtTimeEnd.getText().trim());
        } catch(Exception ex2) {
            showAlert(Alert.AlertType.ERROR,"Error","Thời gian nhập sai định dạng HH:mm!");
            return;
        }
        LocalDateTime startTime = LocalDateTime.of(dStart,tStart);
        LocalDateTime endTime   = LocalDateTime.of(dEnd,tEnd);

        Subject s=cbMonHoc.getValue();

        // question
        List<Question> selectedQ=tableQuestions.getSelectionModel().getSelectedItems();
        if(selectedQ.isEmpty()) {
            showAlert(Alert.AlertType.ERROR,"Error","Chưa chọn câu hỏi!");
            return;
        }

        currentExam.setName(examName);
        currentExam.setPass(pass);
        currentExam.setStartTime(startTime);
        currentExam.setEndTime(endTime);
        currentExam.setSubject(s);

        List<String> qids=selectedQ.stream().map(Question::getQuestionId).collect(Collectors.toList());
        currentExam.setQuestionIds(qids);

        ExamStore.updateExam(currentExam);
        showAlert(Alert.AlertType.INFORMATION,"Success","Cập nhật đề kiểm tra thành công!");
        handleCancel(e);
    }

    @FXML
    private void handleCancel(ActionEvent e) {
        try {
            FXMLLoader loader=new FXMLLoader(getClass().getResource("/Controller/TeacherExamView.fxml"));
            Parent root=loader.load();

            TeacherExamController ctrl=loader.getController();
            ctrl.setCurrentUser(currentUser);

            Stage stage=(Stage)((Node)e.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch(IOException ex2) {
            ex2.printStackTrace();
            showAlert(Alert.AlertType.ERROR,"Error","Không thể mở TeacherExamView: "+ex2.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type,String title,String msg){
        Alert alert=new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
