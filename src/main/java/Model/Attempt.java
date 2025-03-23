package Model;

import java.time.LocalDateTime;

public class Attempt {
    private String userId;
    private String practiceId;
    private double score;
    private LocalDateTime timestamp;

    public Attempt(String userId, String practiceId, double score, LocalDateTime timestamp) {
        this.userId = userId;
        this.practiceId = practiceId;
        this.score = score;
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public String getPracticeId() {
        return practiceId;
    }

    public double getScore() {
        return score;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

}
