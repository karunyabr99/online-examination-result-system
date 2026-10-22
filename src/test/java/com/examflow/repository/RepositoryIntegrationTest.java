package com.examflow.repository;

import com.examflow.dto.DashboardMetricsDto;
import com.examflow.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class RepositoryIntegrationTest {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ExamAttemptRepository examAttemptRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private ResultRepository resultRepository;

    @Test
    @DisplayName("Integration: Seed data is loaded and exams are available")
    void testSeedDataAvailability() {
        List<Exam> exams = examRepository.findAll();
        assertThat(exams).isNotEmpty();
        assertThat(exams.size()).isGreaterThanOrEqualTo(2);

        List<Question> questions = questionRepository.findByExamId(1L);
        assertThat(questions).isNotEmpty();
        assertThat(questions.size()).isGreaterThanOrEqualTo(10);
    }

    @Test
    @DisplayName("Integration: Student persistence, update, and retrieval")
    void testStudentPersistence() {
        String uniqueStudentId = "STU_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Student s = new Student();
        s.setStudentId(uniqueStudentId);
        s.setFullName("Ada Lovelace");
        s.setEmail("ada@computing.org");
        s.setCourse("BTech CSE");

        Student saved = studentRepository.saveOrUpdate(s);
        assertThat(saved.getId()).isNotNull();

        Optional<Student> fetched = studentRepository.findByStudentId(uniqueStudentId);
        assertThat(fetched).isPresent();
        assertThat(fetched.get().getFullName()).isEqualTo("Ada Lovelace");
    }

    @Test
    @DisplayName("Integration: Question CRUD lifecycle in SQLite")
    void testQuestionCrud() {
        Question q = new Question();
        q.setExamId(1L);
        q.setQuestionText("What is the size of int in Java?");
        q.setOptionA("16-bit");
        q.setOptionB("32-bit");
        q.setOptionC("64-bit");
        q.setOptionD("Depends on OS");
        q.setCorrectOption("B");
        q.setMarks(1);
        q.setExplanation("Java primitive int is guaranteed 32-bit signed two's complement integer.");

        Question saved = questionRepository.save(q);
        assertThat(saved.getId()).isNotNull();

        Optional<Question> found = questionRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getCorrectOption()).isEqualTo("B");

        // Update
        saved.setQuestionText("Updated: What is the size of int in Java?");
        questionRepository.update(saved);
        Optional<Question> updated = questionRepository.findById(saved.getId());
        assertThat(updated.get().getQuestionText()).startsWith("Updated:");

        // Delete
        boolean deleted = questionRepository.deleteById(saved.getId());
        assertThat(deleted).isTrue();
        assertThat(questionRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    @DisplayName("Integration: End-to-end Attempt, Answer batching, and Result calculation in SQLite")
    void testFullExamAttemptAndResultPersistence() {
        // 1. Create Student
        String studentId = "TEST_" + UUID.randomUUID().toString().substring(0, 8);
        Student student = new Student();
        student.setStudentId(studentId);
        student.setFullName("Grace Hopper");
        student.setCourse("Computer Science");
        Student savedStudent = studentRepository.saveOrUpdate(student);

        // 2. Create Attempt
        String attemptUuid = UUID.randomUUID().toString();
        ExamAttempt attempt = new ExamAttempt();
        attempt.setAttemptUuid(attemptUuid);
        attempt.setStudentId(savedStudent.getId());
        attempt.setExamId(1L);
        attempt.setStartedAt(LocalDateTime.now().minusMinutes(5));
        attempt.setStatus("IN_PROGRESS");
        ExamAttempt savedAttempt = examAttemptRepository.createAttempt(attempt);
        assertThat(savedAttempt.getId()).isNotNull();

        // 3. Complete Attempt
        LocalDateTime submittedAt = LocalDateTime.now();
        examAttemptRepository.updateStatus(savedAttempt.getId(), "COMPLETED", submittedAt);

        // 4. Save Answer Batch
        Answer ans = new Answer();
        ans.setAttemptId(savedAttempt.getId());
        ans.setQuestionId(1L);
        ans.setSelectedOption("B");
        ans.setCorrect(true);
        ans.setMarksAwarded(1);
        answerRepository.saveBatch(List.of(ans));

        List<Answer> answers = answerRepository.findByAttemptId(savedAttempt.getId());
        assertThat(answers).hasSize(1);
        assertThat(answers.get(0).getSelectedOption()).isEqualTo("B");

        // 5. Save Result
        Result res = new Result();
        res.setAttemptId(savedAttempt.getId());
        res.setScore(1);
        res.setTotalMarks(10);
        res.setPercentage(10.0);
        res.setPassFail("FAIL");
        Result savedResult = resultRepository.save(res);
        assertThat(savedResult.getId()).isNotNull();

        // 6. Verify Dashboard Metrics reflect this submission
        DashboardMetricsDto metrics = resultRepository.getDashboardMetrics();
        assertThat(metrics.getTotalAttempts()).isGreaterThanOrEqualTo(1);
    }
}
