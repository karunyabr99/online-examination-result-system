package com.examflow.service;

import com.examflow.dto.ResultDetailDto;
import com.examflow.model.Exam;
import com.examflow.model.ExamAttempt;
import com.examflow.model.Question;
import com.examflow.model.Result;
import com.examflow.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExaminationServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private ExamRepository examRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private ExamAttemptRepository examAttemptRepository;

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private ResultRepository resultRepository;

    @InjectMocks
    private ExaminationService examinationService;

    private Exam sampleExam;
    private ExamAttempt sampleAttempt;
    private List<Question> sampleQuestions;

    @BeforeEach
    void setUp() {
        sampleExam = new Exam(1L, "Java & Database Foundations", "Computer Science", "Test exam", 10, 10, 40.0, true);
        sampleAttempt = new ExamAttempt(100L, "uuid-12345", 50L, 1L, LocalDateTime.now().minusMinutes(5), null, "IN_PROGRESS");

        sampleQuestions = new ArrayList<>();
        // 10 questions with answer 'B'
        for (long i = 1; i <= 10; i++) {
            Question q = new Question();
            q.setId(i);
            q.setExamId(1L);
            q.setQuestionText("Sample question " + i);
            q.setOptionA("Alpha");
            q.setOptionB("Beta");
            q.setOptionC("Gamma");
            q.setOptionD("Delta");
            q.setCorrectOption("B");
            q.setMarks(1);
            sampleQuestions.add(q);
        }
    }

    @Test
    @DisplayName("Evaluate: All correct answers result in 100% score and PASS")
    void testEvaluateAllCorrectAnswers() {
        when(examAttemptRepository.findByUuid("uuid-12345")).thenReturn(Optional.of(sampleAttempt));
        when(examRepository.findById(1L)).thenReturn(Optional.of(sampleExam));
        when(questionRepository.findByExamId(1L)).thenReturn(sampleQuestions);

        ResultDetailDto expectedResult = new ResultDetailDto();
        expectedResult.setAttemptUuid("uuid-12345");
        expectedResult.setScore(10);
        expectedResult.setTotalMarks(10);
        expectedResult.setPercentage(100.0);
        expectedResult.setPassFail("PASS");

        when(resultRepository.findByAttemptUuid("uuid-12345")).thenReturn(Optional.of(expectedResult));

        // Candidate answers all with 'B'
        Map<Long, String> candidateAnswers = new HashMap<>();
        for (long i = 1; i <= 10; i++) {
            candidateAnswers.put(i, "B");
        }

        ResultDetailDto result = examinationService.evaluateAndSaveAttempt("uuid-12345", candidateAnswers);

        assertThat(result).isNotNull();
        assertThat(result.getScore()).isEqualTo(10);
        assertThat(result.getTotalMarks()).isEqualTo(10);
        assertThat(result.getPercentage()).isEqualTo(100.0);
        assertThat(result.isPassed()).isTrue();
        assertThat(result.getPassFail()).isEqualTo("PASS");

        verify(resultRepository, times(1)).save(argThat(r ->
                r.getScore() == 10 && r.getPercentage() == 100.0 && "PASS".equals(r.getPassFail())));
        verify(examAttemptRepository, times(1)).updateStatus(eq(100L), eq("COMPLETED"), any());
    }

    @Test
    @DisplayName("Evaluate: All incorrect answers result in 0% score and FAIL")
    void testEvaluateAllIncorrectAnswers() {
        when(examAttemptRepository.findByUuid("uuid-12345")).thenReturn(Optional.of(sampleAttempt));
        when(examRepository.findById(1L)).thenReturn(Optional.of(sampleExam));
        when(questionRepository.findByExamId(1L)).thenReturn(sampleQuestions);

        ResultDetailDto expectedResult = new ResultDetailDto();
        expectedResult.setAttemptUuid("uuid-12345");
        expectedResult.setScore(0);
        expectedResult.setTotalMarks(10);
        expectedResult.setPercentage(0.0);
        expectedResult.setPassFail("FAIL");

        when(resultRepository.findByAttemptUuid("uuid-12345")).thenReturn(Optional.of(expectedResult));

        // Candidate answers all with 'A' (incorrect)
        Map<Long, String> candidateAnswers = new HashMap<>();
        for (long i = 1; i <= 10; i++) {
            candidateAnswers.put(i, "A");
        }

        ResultDetailDto result = examinationService.evaluateAndSaveAttempt("uuid-12345", candidateAnswers);

        assertThat(result.getScore()).isEqualTo(0);
        assertThat(result.getPercentage()).isEqualTo(0.0);
        assertThat(result.isPassed()).isFalse();
        assertThat(result.getPassFail()).isEqualTo("FAIL");

        verify(resultRepository, times(1)).save(argThat(r ->
                r.getScore() == 0 && r.getPercentage() == 0.0 && "FAIL".equals(r.getPassFail())));
    }

    @Test
    @DisplayName("Boundary: Exactly at 40% passing threshold results in PASS")
    void testPassingThresholdBoundaryPass() {
        when(examAttemptRepository.findByUuid("uuid-12345")).thenReturn(Optional.of(sampleAttempt));
        when(examRepository.findById(1L)).thenReturn(Optional.of(sampleExam));
        when(questionRepository.findByExamId(1L)).thenReturn(sampleQuestions);

        ResultDetailDto expectedResult = new ResultDetailDto();
        expectedResult.setAttemptUuid("uuid-12345");
        expectedResult.setScore(4);
        expectedResult.setTotalMarks(10);
        expectedResult.setPercentage(40.0);
        expectedResult.setPassFail("PASS");

        when(resultRepository.findByAttemptUuid("uuid-12345")).thenReturn(Optional.of(expectedResult));

        // 4 correct (questions 1 to 4), 6 incorrect
        Map<Long, String> candidateAnswers = new HashMap<>();
        for (long i = 1; i <= 4; i++) {
            candidateAnswers.put(i, "B");
        }
        for (long i = 5; i <= 10; i++) {
            candidateAnswers.put(i, "C");
        }

        ResultDetailDto result = examinationService.evaluateAndSaveAttempt("uuid-12345", candidateAnswers);

        assertThat(result.getScore()).isEqualTo(4);
        assertThat(result.getPercentage()).isEqualTo(40.0);
        assertThat(result.isPassed()).isTrue();
        assertThat(result.getPassFail()).isEqualTo("PASS");

        verify(resultRepository, times(1)).save(argThat(r ->
                r.getScore() == 4 && r.getPercentage() == 40.0 && "PASS".equals(r.getPassFail())));
    }

    @Test
    @DisplayName("Boundary: Just below 40% (3 out of 10 = 30%) results in FAIL")
    void testPassingThresholdBoundaryFail() {
        when(examAttemptRepository.findByUuid("uuid-12345")).thenReturn(Optional.of(sampleAttempt));
        when(examRepository.findById(1L)).thenReturn(Optional.of(sampleExam));
        when(questionRepository.findByExamId(1L)).thenReturn(sampleQuestions);

        ResultDetailDto expectedResult = new ResultDetailDto();
        expectedResult.setAttemptUuid("uuid-12345");
        expectedResult.setScore(3);
        expectedResult.setTotalMarks(10);
        expectedResult.setPercentage(30.0);
        expectedResult.setPassFail("FAIL");

        when(resultRepository.findByAttemptUuid("uuid-12345")).thenReturn(Optional.of(expectedResult));

        // 3 correct
        Map<Long, String> candidateAnswers = new HashMap<>();
        for (long i = 1; i <= 3; i++) {
            candidateAnswers.put(i, "B");
        }
        for (long i = 4; i <= 10; i++) {
            candidateAnswers.put(i, "D");
        }

        ResultDetailDto result = examinationService.evaluateAndSaveAttempt("uuid-12345", candidateAnswers);

        assertThat(result.getScore()).isEqualTo(3);
        assertThat(result.getPercentage()).isEqualTo(30.0);
        assertThat(result.isPassed()).isFalse();
        assertThat(result.getPassFail()).isEqualTo("FAIL");

        verify(resultRepository, times(1)).save(argThat(r ->
                r.getScore() == 3 && r.getPercentage() == 30.0 && "FAIL".equals(r.getPassFail())));
    }

    @Test
    @DisplayName("Duplicate Submission Prevention: Already COMPLETED attempt returns existing result without re-evaluating")
    void testDuplicateSubmissionPrevention() {
        ExamAttempt completedAttempt = new ExamAttempt(100L, "uuid-completed", 50L, 1L,
                LocalDateTime.now().minusMinutes(10), LocalDateTime.now().minusMinutes(2), "COMPLETED");

        when(examAttemptRepository.findByUuid("uuid-completed")).thenReturn(Optional.of(completedAttempt));

        ResultDetailDto existingResult = new ResultDetailDto();
        existingResult.setAttemptUuid("uuid-completed");
        existingResult.setScore(9);
        existingResult.setTotalMarks(10);
        existingResult.setPercentage(90.0);
        existingResult.setPassFail("PASS");

        when(resultRepository.findByAttemptUuid("uuid-completed")).thenReturn(Optional.of(existingResult));

        ResultDetailDto result = examinationService.evaluateAndSaveAttempt("uuid-completed", Map.of(1L, "A"));

        assertThat(result).isNotNull();
        assertThat(result.getScore()).isEqualTo(9);

        // Verification: ensure no new save was called
        verify(resultRepository, never()).save(any(Result.class));
        verify(answerRepository, never()).saveBatch(anyList());
        verify(examAttemptRepository, never()).updateStatus(anyLong(), anyString(), any());
    }
}
