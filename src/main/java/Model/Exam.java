package Model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class Exam {
    private String examId;
    private String name;
    private Subject subject;
    private String teacherUsername;

    private List<String> questionIds;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private String pass;

    public Exam() {
        this.questionIds = new ArrayList<>();
    }

    public Exam(String examId, String name, Subject subject,
                String teacherUsername,
                LocalDateTime startTime,
                LocalDateTime endTime,
                String pass) {
        this.examId = examId;
        this.name = name;
        this.subject = subject;
        this.teacherUsername = teacherUsername;
        this.startTime = startTime;
        this.endTime = endTime;
        this.pass = pass;
        this.questionIds = new ArrayList<>();
    }

    public String getExamId() {
        return examId;
    }
    public void setExamId(String examId) {
        this.examId = examId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Subject getSubject() {
        return subject;
    }
    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public String getTeacherUsername() {
        return teacherUsername;
    }
    public void setTeacherUsername(String teacherUsername) {
        this.teacherUsername = teacherUsername;
    }

    public List<String> getQuestionIds() {
        return questionIds;
    }
    public void setQuestionIds(List<String> questionIds) {
        this.questionIds = questionIds;
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

    public String getPass() {
        return pass;
    }
    public void setPass(String pass) {
        this.pass = pass;
    }
}
