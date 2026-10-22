package com.examflow.model;

import java.time.LocalDateTime;

public class Result {
    private Long id;
    private Long attemptId;
    private int score;
    private int totalMarks;
    private double percentage;
    private String passFail; // 'PASS' or 'FAIL'
    private LocalDateTime evaluatedAt;

    public Result() {
    }

    public Result(Long id, Long attemptId, int score, int totalMarks, double percentage, String passFail, LocalDateTime evaluatedAt) {
        this.id = id;
        this.attemptId = attemptId;
        this.score = score;
        this.totalMarks = totalMarks;
        this.percentage = percentage;
        this.passFail = passFail;
        this.evaluatedAt = evaluatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(Long attemptId) {
        this.attemptId = attemptId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(int totalMarks) {
        this.totalMarks = totalMarks;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public String getPassFail() {
        return passFail;
    }

    public void setPassFail(String passFail) {
        this.passFail = passFail;
    }

    public LocalDateTime getEvaluatedAt() {
        return evaluatedAt;
    }

    public void setEvaluatedAt(LocalDateTime evaluatedAt) {
        this.evaluatedAt = evaluatedAt;
    }

    public boolean isPassed() {
        return "PASS".equalsIgnoreCase(this.passFail);
    }

    @Override
    public String toString() {
        return "Result{" +
                "id=" + id +
                ", attemptId=" + attemptId +
                ", score=" + score +
                ", totalMarks=" + totalMarks +
                ", percentage=" + percentage +
                ", passFail='" + passFail + '\'' +
                '}';
    }
}
