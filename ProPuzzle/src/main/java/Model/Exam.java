package Model;

import java.time.LocalDateTime;
import java.util.List;

public class Exam {
    private String examId;
    private String examName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String examPassword; // có thể null nếu không dùng mật khẩu
    private List<Question> questions; // danh sách câu hỏi trong đề thi

    public Exam(String e001, String mathExam, LocalDateTime localDateTime, LocalDateTime dateTime) {
    }

    public Exam(String examId, String examName, LocalDateTime startTime, LocalDateTime endTime, String examPassword, List<Question> questions) {
        this.examId = examId;
        this.examName = examName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.examPassword = examPassword;
        this.questions = questions;
    }

    // Getters và Setters
    public String getExamId() {
        return examId;
    }

    public void setExamId(String examId) {
        this.examId = examId;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getExamPassword() {
        return examPassword;
    }

    public void setExamPassword(String examPassword) {
        this.examPassword = examPassword;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    @Override
    public String toString() {
        return "Exam{" +
                "examId='" + examId + '\'' +
                ", examName='" + examName + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", examPassword='" + examPassword + '\'' +
                ", questions=" + questions +
                '}';
    }
}
