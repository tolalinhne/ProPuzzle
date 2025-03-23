package Model;

import java.util.List;

public class Practice {
    private String practiceId;
    private String name;
    private List<String> questionIds;
    private Subject subject;
    private String teacherUsername;

    public Practice(String practiceId, String name, List<String> questionIds) {
        this.practiceId = practiceId;
        this.name = name;
        this.questionIds = questionIds;
    }

    public String getPracticeId() {
        return practiceId;
    }

    public void setPracticeId(String practiceId) {
        this.practiceId = practiceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getQuestionIds() {
        return questionIds;
    }

    public void setQuestionIds(List<String> questionIds) {
        this.questionIds = questionIds;
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

    @Override
    public String toString() {
        return name;
    }
}
