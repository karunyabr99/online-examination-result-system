package com.examflow.dto;

import java.util.HashMap;
import java.util.Map;

public class ExamSubmissionDto {
    private String attemptUuid;
    private Map<Long, String> answers = new HashMap<>();

    public ExamSubmissionDto() {
    }

    public ExamSubmissionDto(String attemptUuid, Map<Long, String> answers) {
        this.attemptUuid = attemptUuid;
        this.answers = answers != null ? answers : new HashMap<>();
    }

    public String getAttemptUuid() {
        return attemptUuid;
    }

    public void setAttemptUuid(String attemptUuid) {
        this.attemptUuid = attemptUuid;
    }

    public Map<Long, String> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<Long, String> answers) {
        this.answers = answers;
    }
}
