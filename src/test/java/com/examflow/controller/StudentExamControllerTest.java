package com.examflow.controller;

import com.examflow.dto.SanitizedQuestionDto;
import com.examflow.model.Exam;
import com.examflow.model.ExamAttempt;
import com.examflow.service.ExaminationService;
import com.examflow.service.QuestionService;
import com.examflow.service.ResultService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentExamController.class)
public class StudentExamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExaminationService examinationService;

    @MockBean
    private QuestionService questionService;

    @MockBean
    private ResultService resultService;

    @Test
    @DisplayName("GET /exam/register: Displays registration form with active exams")
    void testShowRegistrationForm() throws Exception {
        Exam exam = new Exam(1L, "Java Programming", "CSE", "Core", 10, 10, 40.0, true);
        when(examinationService.getActiveExams()).thenReturn(List.of(exam));

        mockMvc.perform(get("/exam/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("exam/register"))
                .andExpect(model().attributeExists("registrationDto"))
                .andExpect(model().attributeExists("activeExams"))
                .andExpect(content().string(containsString("Student Examination Entry")));
    }

    @Test
    @DisplayName("Validation: POST /exam/instructions with blank fields fails validation and returns to register")
    void testValidationFailsForBlankFields() throws Exception {
        Exam exam = new Exam(1L, "Java Programming", "CSE", "Core", 10, 10, 40.0, true);
        when(examinationService.getActiveExams()).thenReturn(List.of(exam));

        // Submitting with empty fullName and invalid studentId
        mockMvc.perform(post("/exam/instructions")
                        .param("fullName", "")
                        .param("studentId", "!")
                        .param("course", "")
                        .param("examId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("exam/register"))
                .andExpect(model().hasErrors());
    }

    @Test
    @DisplayName("Validation: POST /exam/instructions with valid details proceeds to instructions view")
    void testValidationSuccessProceedsToInstructions() throws Exception {
        Exam exam = new Exam(1L, "Java & Database Assessment", "CSE", "Core", 10, 10, 40.0, true);
        when(examinationService.getExamById(1L)).thenReturn(Optional.of(exam));

        SanitizedQuestionDto sq = new SanitizedQuestionDto(10L, 1L, 1, "What is JVM?", "JDK", "Virtual Machine", "JRE", "JIT", 1);
        when(questionService.getSanitizedQuestionsForExam(1L)).thenReturn(List.of(sq));

        mockMvc.perform(post("/exam/instructions")
                        .param("fullName", "Yashwanth Naik")
                        .param("studentId", "1MS21CS099")
                        .param("course", "BTech CSE")
                        .param("examId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("exam/instructions"))
                .andExpect(model().attribute("questionCount", 1))
                .andExpect(content().string(containsString("Examination Guidelines")));
    }

    @Test
    @DisplayName("Security: Question answers are NOT leaked to the examination taking view")
    void testQuestionAnswersNotLeakedToBrowser() throws Exception {
        ExamAttempt attempt = new ExamAttempt(1L, "test-uuid-xyz", 10L, 1L, LocalDateTime.now(), null, "IN_PROGRESS");
        Exam exam = new Exam(1L, "Java Programming", "CSE", "Desc", 10, 10, 40.0, true);

        when(examinationService.getAttemptByUuid("test-uuid-xyz")).thenReturn(Optional.of(attempt));
        when(examinationService.getExamById(1L)).thenReturn(Optional.of(exam));

        // Sanitized question has NO correct answer field
        SanitizedQuestionDto sq = new SanitizedQuestionDto(101L, 1L, 1, "Which keyword prevents inheritance?", "abstract", "final", "static", "sealed", 1);
        when(questionService.getSanitizedQuestionsForExam(1L)).thenReturn(List.of(sq));

        mockMvc.perform(get("/exam/take/test-uuid-xyz"))
                .andExpect(status().isOk())
                .andExpect(view().name("exam/take"))
                .andExpect(model().attributeExists("questions"))
                .andExpect(content().string(containsString("Which keyword prevents inheritance?")));
    }
}
