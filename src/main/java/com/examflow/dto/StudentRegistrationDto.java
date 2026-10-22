package com.examflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class StudentRegistrationDto {

    @NotBlank(message = "Student ID / Roll Number is required")
    @Size(min = 3, max = 30, message = "Student ID must be between 3 and 30 characters")
    @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "Student ID must contain only alphanumeric characters, dashes, or underscores")
    private String studentId;

    @NotBlank(message = "Full Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String fullName;

    private String email;

    @NotBlank(message = "Course / Subject is required")
    private String course;

    @NotNull(message = "Please select an examination to attempt")
    private Long examId;

    public StudentRegistrationDto() {
    }

    public StudentRegistrationDto(String studentId, String fullName, String email, String course, Long examId) {
        this.studentId = studentId;
        this.fullName = fullName;
        this.email = email;
        this.course = course;
        this.examId = examId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId != null ? studentId.trim().toUpperCase() : null;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName != null ? fullName.trim() : null;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email != null ? email.trim() : null;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course != null ? course.trim() : null;
    }

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }
}
