package com.examflow.model;

import java.time.LocalDateTime;

public class ExamAttempt {
    private Long id;
    private String attemptUuid;
    private Long studentId;
    private Long examId;
    private LocalDateTime startedAt;
    private LocalDateTime submittedAt;
    private String status; // 'IN_PROGRESS', 'COMPLETED', 'TIMED_OUT'

    public ExamAttempt() {
    }

    public ExamAttempt(Long id, String attemptUuid, Long studentId, Long examId, LocalDateTime startedAt, LocalDateTime submittedAt, String status) {
        this.id = id;
        this.attemptUuid = attemptUuid;
        this.studentId = studentId;
        this.examId = examId;
        this.startedAt = startedAt;
        this.submittedAt = submittedAt;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAttemptUuid() {
        return attemptUuid;
    }

    public void setAttemptUuid(String attemptUuid) {
        this.attemptUuid = attemptUuid;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ExamAttempt{" +
                "id=" + id +
                ", attemptUuid='" + attemptUuid + '\'' +
                ", studentId=" + studentId +
                ", examId=" + examId +
                ", status='" + status + '\'' +
                '}';
    }
}
