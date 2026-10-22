package com.examflow.model;

public class Exam {
    private Long id;
    private String title;
    private String subject;
    private String description;
    private int durationMinutes;
    private int totalMarks;
    private double passingPercentage;
    private boolean active;

    public Exam() {
    }

    public Exam(Long id, String title, String subject, String description, int durationMinutes, int totalMarks, double passingPercentage, boolean active) {
        this.id = id;
        this.title = title;
        this.subject = subject;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.totalMarks = totalMarks;
        this.passingPercentage = passingPercentage;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public int getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(int totalMarks) {
        this.totalMarks = totalMarks;
    }

    public double getPassingPercentage() {
        return passingPercentage;
    }

    public void setPassingPercentage(double passingPercentage) {
        this.passingPercentage = passingPercentage;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "Exam{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", subject='" + subject + '\'' +
                ", durationMinutes=" + durationMinutes +
                ", totalMarks=" + totalMarks +
                ", passingPercentage=" + passingPercentage +
                '}';
    }
}
