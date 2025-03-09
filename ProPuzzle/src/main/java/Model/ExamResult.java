package Model;

import java.time.LocalDateTime;

public class ExamResult {
    private String examResultId;
    private Exam exam;
    private Student student;
    private double score;
    private int correctCount;
    private int wrongCount;
    private LocalDateTime submittedAt;
    private boolean confirmed; // trạng thái đã ký xác nhận điểm

    public ExamResult() {
    }

    public ExamResult(String examResultId, Exam exam, Student student, double score, int correctCount, int wrongCount, LocalDateTime submittedAt, boolean confirmed) {
        this.examResultId = examResultId;
        this.exam = exam;
        this.student = student;
        this.score = score;
        this.correctCount = correctCount;
        this.wrongCount = wrongCount;
        this.submittedAt = submittedAt;
        this.confirmed = confirmed;
    }

    // Getters và Setters
    public String getExamResultId() {
        return examResultId;
    }

    public void setExamResultId(String examResultId) {
        this.examResultId = examResultId;
    }

    public Exam getExam() {
        return exam;
    }

    public void setExam(Exam exam) {
        this.exam = exam;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getCorrectCount() {
        return correctCount;
    }

    public void setCorrectCount(int correctCount) {
        this.correctCount = correctCount;
    }

    public int getWrongCount() {
        return wrongCount;
    }

    public void setWrongCount(int wrongCount) {
        this.wrongCount = wrongCount;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    @Override
    public String toString() {
        return "ExamResult{" +
                "examResultId='" + examResultId + '\'' +
                ", exam=" + exam +
                ", student=" + student +
                ", score=" + score +
                ", correctCount=" + correctCount +
                ", wrongCount=" + wrongCount +
                ", submittedAt=" + submittedAt +
                ", confirmed=" + confirmed +
                '}';
    }
}
