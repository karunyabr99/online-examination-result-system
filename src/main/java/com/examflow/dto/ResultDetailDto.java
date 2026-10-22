package com.examflow.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ResultDetailDto {
    private Long resultId;
    private Long attemptId;
    private String attemptUuid;
    private String studentId;
    private String studentName;
    private String studentEmail;
    private String course;
    private Long examId;
    private String examTitle;
    private String examSubject;
    private int durationMinutes;
    private int score;
    private int totalMarks;
    private double percentage;
    private String passFail;
    private double passingPercentage;
    private LocalDateTime startedAt;
    private LocalDateTime submittedAt;
    private LocalDateTime evaluatedAt;
    private long timeTakenSeconds;
    private int totalQuestions;
    private int answeredQuestions;
    private int correctCount;
    private int incorrectCount;
    private int unattemptedCount;
    private List<QuestionReviewDto> questions = new ArrayList<>();

    public ResultDetailDto() {
    }

    public Long getResultId() {
        return resultId;
    }

    public void setResultId(Long resultId) {
        this.resultId = resultId;
    }

    public Long getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(Long attemptId) {
        this.attemptId = attemptId;
    }

    public String getAttemptUuid() {
        return attemptUuid;
    }

    public void setAttemptUuid(String attemptUuid) {
        this.attemptUuid = attemptUuid;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public String getExamTitle() {
        return examTitle;
    }

    public void setExamTitle(String examTitle) {
        this.examTitle = examTitle;
    }

    public String getExamSubject() {
        return examSubject;
    }

    public void setExamSubject(String examSubject) {
        this.examSubject = examSubject;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
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

    public double getPassingPercentage() {
        return passingPercentage;
    }

    public void setPassingPercentage(double passingPercentage) {
        this.passingPercentage = passingPercentage;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public LocalDateTime getEvaluatedAt() {
        return evaluatedAt;
    }

    public void setEvaluatedAt(LocalDateTime evaluatedAt) {
        this.evaluatedAt = evaluatedAt;
    }

    public long getTimeTakenSeconds() {
        return timeTakenSeconds;
    }

    public void setTimeTakenSeconds(long timeTakenSeconds) {
        this.timeTakenSeconds = timeTakenSeconds;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public int getAnsweredQuestions() {
        return answeredQuestions;
    }

    public void setAnsweredQuestions(int answeredQuestions) {
        this.answeredQuestions = answeredQuestions;
    }

    public int getCorrectCount() {
        return correctCount;
    }

    public void setCorrectCount(int correctCount) {
        this.correctCount = correctCount;
    }

    public int getIncorrectCount() {
        return incorrectCount;
    }

    public void setIncorrectCount(int incorrectCount) {
        this.incorrectCount = incorrectCount;
    }

    public int getUnattemptedCount() {
        return unattemptedCount;
    }

    public void setUnattemptedCount(int unattemptedCount) {
        this.unattemptedCount = unattemptedCount;
    }

    public List<QuestionReviewDto> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionReviewDto> questions) {
        this.questions = questions;
    }

    public boolean isPassed() {
        return "PASS".equalsIgnoreCase(this.passFail);
    }

    public String getFormattedSubmittedAt() {
        if (submittedAt == null) return "N/A";
        return submittedAt.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm:ss"));
    }

    public String getFormattedTimeTaken() {
        if (timeTakenSeconds <= 0) return "Less than 1 min";
        long minutes = timeTakenSeconds / 60;
        long seconds = timeTakenSeconds % 60;
        return String.format("%d min %02d sec", minutes, seconds);
    }
}
