package Controller;

import Model.*;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.io.IOException;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

public class ExamDoController {

    @FXML private Label lblTitle;
    @FXML private TextArea txtQuestionContent;
    @FXML private RadioButton rdoA, rdoB, rdoC, rdoD;
    @FXML private ToggleGroup toggleGroupAnswer;
    @FXML private Button btnPrevious, btnNext, btnSubmit, btnExit;

    @FXML private Label lblCountdown;
    @FXML private TextField txtSubject, txtExamName;

    @FXML private ListView<Integer> listQuestionNumber;

    private User currentUser;
    private Exam currentExam;

    private List<String> questionIds;
    private String[] userAnswers;
    private int currentIndex=0;

    private Timeline timeline; // for countdown
    private long secondsLeft=0; // total second countdown

    public void setCurrentUser(User user){
        this.currentUser=user;
    }

    public void setExam(Exam exam){
        this.currentExam=exam;
        // 1) Nếu exam có pass => hỏi pass
        if(exam.getPass()!=null && !exam.getPass().isEmpty()){
            TextInputDialog passDialog = new TextInputDialog();
            passDialog.setTitle("Nhập mật khẩu bài thi");
            passDialog.setHeaderText("Bài kiểm tra này yêu cầu mật khẩu để vào");
            passDialog.setContentText("Mật khẩu:");
            Optional<String> result = passDialog.showAndWait();
            if(result.isPresent()){
                String passInput = result.get();
                if(!passInput.equals(exam.getPass())){
                    showAlert(Alert.AlertType.ERROR,"Sai mật khẩu","Mật khẩu không đúng!");
                    backToStudent();
                    return; // Dừng, ko cho initExamData
                }
            } else {
                // user bấm Cancel => thoát
                backToStudent();
                return;
            }
        }

        // 2) Pass ok => init
        initExamData();
    }

    private void initExamData(){
        if(currentExam==null) return;
        txtExamName.setText(currentExam.getName());
        if(currentExam.getSubject()!=null){
            txtSubject.setText(currentExam.getSubject().getSubjectName());
        }
        questionIds = new ArrayList<>(currentExam.getQuestionIds());
        // trộn random
        Collections.shuffle(questionIds);
        userAnswers = new String[questionIds.size()];

        List<Integer> numbers=new ArrayList<>();
        for(int i=1;i<=questionIds.size();i++){
            numbers.add(i);
        }
        listQuestionNumber.setItems(javafx.collections.FXCollections.observableArrayList(numbers));
        listQuestionNumber.setCellFactory(lv->new ListCell<Integer>(){
            @Override
            protected void updateItem(Integer item, boolean empty){
                super.updateItem(item, empty);
                if(empty||item==null){
                    setText(null);
                    setStyle("");
                } else {
                    setText("Câu "+item);
                    if(userAnswers[item-1]!=null){
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });
        listQuestionNumber.setOnMouseClicked(e->{
            Integer idx=listQuestionNumber.getSelectionModel().getSelectedItem();
            if(idx!=null){
                saveUserChoice();
                displayQuestion(idx-1);
            }
        });

        if(!questionIds.isEmpty()){
            displayQuestion(0);
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime end = currentExam.getEndTime();
        if(end == null){
            secondsLeft = 1800;
        } else {
            secondsLeft = now.until(end, ChronoUnit.SECONDS);
            if(secondsLeft <= 0){
                autoSubmit();
                return;
            }
        }

        startCountdown();
    }

    private void startCountdown(){
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), ev->{
            secondsLeft--;
            if(secondsLeft<=0){
                // auto submit
                lblCountdown.setText("Hết giờ!");
                timeline.stop();
                autoSubmit();
            } else {
                long mm=secondsLeft/60;
                long ss=secondsLeft%60;
                lblCountdown.setText(String.format("%02d:%02d", mm, ss));
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void autoSubmit(){
        saveUserChoice();
        double score = calculateScore();
        Attempt att = new Attempt(
                currentUser.getUsername(),
                currentExam.getExamId(),
                score,
                java.time.LocalDateTime.now()
        );
        AttemptStore.addAttempt(att);

        showAlert(Alert.AlertType.INFORMATION,"Hết giờ","Bạn được "+score+" điểm!");
        backToStudent();
    }

    private void displayQuestion(int idx){
        if(idx<0 || idx>= questionIds.size()) return;
        currentIndex=idx;
        String qid= questionIds.get(currentIndex);
        Question q=QuestionStore.getQuestionById(qid);
        if(q==null){
            txtQuestionContent.setText("Không tìm thấy câu hỏi ID= "+qid);
            rdoA.setText("A. ");
            rdoB.setText("B. ");
            rdoC.setText("C. ");
            rdoD.setText("D. ");
            return;
        }
        lblTitle.setText("Câu "+(idx+1));
        txtQuestionContent.setText(q.getContent());
        rdoA.setText("A. "+q.getOptionA());
        rdoB.setText("B. "+q.getOptionB());
        rdoC.setText("C. "+q.getOptionC());
        rdoD.setText("D. "+q.getOptionD());

        String chosen=userAnswers[idx];
        if("A".equals(chosen)) rdoA.setSelected(true);
        else if("B".equals(chosen)) rdoB.setSelected(true);
        else if("C".equals(chosen)) rdoC.setSelected(true);
        else if("D".equals(chosen)) rdoD.setSelected(true);
        else toggleGroupAnswer.selectToggle(null);

        listQuestionNumber.getSelectionModel().select(idx+1);
        listQuestionNumber.refresh();
    }

    private void saveUserChoice(){
        RadioButton selected = (RadioButton) toggleGroupAnswer.getSelectedToggle();
        if(selected==null){
            userAnswers[currentIndex]=null;
        } else {
            if(selected==rdoA) userAnswers[currentIndex]="A";
            else if(selected==rdoB) userAnswers[currentIndex]="B";
            else if(selected==rdoC) userAnswers[currentIndex]="C";
            else if(selected==rdoD) userAnswers[currentIndex]="D";
        }
        listQuestionNumber.refresh();
    }

    private double calculateScore(){
        int score=0;
        for(int i=0; i<questionIds.size(); i++){
            String qid= questionIds.get(i);
            Question q=QuestionStore.getQuestionById(qid);
            if(q!=null && q.getCorrectAnswer().equalsIgnoreCase(userAnswers[i])){
                score++;
            }
        }
        return score*10.0/ questionIds.size();
    }

    @FXML
    private void handlePrevious(ActionEvent e){
        saveUserChoice();
        if(currentIndex>0) {
            displayQuestion(currentIndex-1);
        }
    }

    @FXML
    private void handleNext(ActionEvent e){
        saveUserChoice();
        if(currentIndex< questionIds.size()-1){
            displayQuestion(currentIndex+1);
        }
    }

    @FXML
    private void handleSubmit(ActionEvent e){
        Alert confirm=new Alert(Alert.AlertType.CONFIRMATION,
                "Bạn chắc chắn nộp bài?",
                ButtonType.OK, ButtonType.CANCEL);
        if(confirm.showAndWait().orElse(ButtonType.CANCEL)==ButtonType.OK){
            timeline.stop();
            saveUserChoice();
            double score=calculateScore();
            Attempt att = new Attempt(
                    currentUser.getUsername(),
                    currentExam.getExamId(),
                    score,
                    java.time.LocalDateTime.now()
            );
            AttemptStore.addAttempt(att);

            showAlert(Alert.AlertType.INFORMATION,"Kết quả",
                    "Bạn được "+score+" điểm!");
            backToStudent();
        }
    }

    @FXML
    private void handleExit(ActionEvent e){
        timeline.stop();
        backToStudent();
    }

    private void backToStudent(){
        try{
            FXMLLoader loader=new FXMLLoader(getClass().getResource("/Controller/StudentView.fxml"));
            Parent root=loader.load();
            StudentController sc=loader.getController();
            sc.setCurrentUser(currentUser);

            Stage stage=(Stage)lblCountdown.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch(IOException ex){
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR,"Error","Không thể về StudentView: "+ex.getMessage());
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
