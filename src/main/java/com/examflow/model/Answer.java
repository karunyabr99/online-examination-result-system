package com.examflow.model;

public class Answer {
    private Long id;
    private Long attemptId;
    private Long questionId;
    private String selectedOption; // 'A', 'B', 'C', 'D' or null
    private boolean correct;
    private int marksAwarded;

    public Answer() {
    }

    public Answer(Long id, Long attemptId, Long questionId, String selectedOption, boolean correct, int marksAwarded) {
        this.id = id;
        this.attemptId = attemptId;
        this.questionId = questionId;
        this.selectedOption = selectedOption;
        this.correct = correct;
        this.marksAwarded = marksAwarded;
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

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(String selectedOption) {
        this.selectedOption = selectedOption;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public int getMarksAwarded() {
        return marksAwarded;
    }

    public void setMarksAwarded(int marksAwarded) {
        this.marksAwarded = marksAwarded;
    }

    @Override
    public String toString() {
        return "Answer{" +
                "id=" + id +
                ", attemptId=" + attemptId +
                ", questionId=" + questionId +
                ", selectedOption='" + selectedOption + '\'' +
                ", correct=" + correct +
                ", marksAwarded=" + marksAwarded +
                '}';
    }
}
