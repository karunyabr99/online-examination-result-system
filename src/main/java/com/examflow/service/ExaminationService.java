package com.examflow.service;

import com.examflow.dto.ResultDetailDto;
import com.examflow.dto.StudentRegistrationDto;
import com.examflow.model.*;
import com.examflow.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ExaminationService {

    private static final Logger logger = LoggerFactory.getLogger(ExaminationService.class);

    private final StudentRepository studentRepository;
    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final ExamAttemptRepository examAttemptRepository;
    private final AnswerRepository answerRepository;
    private final ResultRepository resultRepository;

    public ExaminationService(StudentRepository studentRepository,
                              ExamRepository examRepository,
                              QuestionRepository questionRepository,
                              ExamAttemptRepository examAttemptRepository,
                              AnswerRepository answerRepository,
                              ResultRepository resultRepository) {
        this.studentRepository = studentRepository;
        this.examRepository = examRepository;
        this.questionRepository = questionRepository;
        this.examAttemptRepository = examAttemptRepository;
        this.answerRepository = answerRepository;
        this.resultRepository = resultRepository;
    }

    public Student registerOrUpdateStudent(StudentRegistrationDto dto) {
        Student student = new Student();
        student.setStudentId(dto.getStudentId().toUpperCase().trim());
        student.setFullName(dto.getFullName().trim());
        student.setEmail(dto.getEmail());
        student.setCourse(dto.getCourse().trim());
        return studentRepository.saveOrUpdate(student);
    }

    public ExamAttempt createAttempt(Student student, Exam exam) {
        ExamAttempt attempt = new ExamAttempt();
        attempt.setAttemptUuid(UUID.randomUUID().toString());
        attempt.setStudentId(student.getId());
        attempt.setExamId(exam.getId());
        attempt.setStartedAt(LocalDateTime.now());
        attempt.setStatus("IN_PROGRESS");
        return examAttemptRepository.createAttempt(attempt);
    }

    public Optional<ExamAttempt> getAttemptByUuid(String uuid) {
        return examAttemptRepository.findByUuid(uuid);
    }

    public Optional<Exam> getExamById(Long id) {
        return examRepository.findById(id);
    }

    public List<Exam> getActiveExams() {
        return examRepository.findActiveExams();
    }

    /**
     * Evaluates submitted answers on the server side and saves results in SQLite.
     * Guaranteed duplicate submission prevention.
     */
    @Transactional
    public ResultDetailDto evaluateAndSaveAttempt(String attemptUuid, Map<Long, String> submittedAnswers) {
        ExamAttempt attempt = examAttemptRepository.findByUuid(attemptUuid)
                .orElseThrow(() -> new IllegalArgumentException("Examination attempt not found: " + attemptUuid));

        // Prevent duplicate evaluation: if already completed, return existing result
        if ("COMPLETED".equalsIgnoreCase(attempt.getStatus()) || "TIMED_OUT".equalsIgnoreCase(attempt.getStatus())) {
            logger.warn("Attempt {} is already submitted and evaluated. Returning existing result.", attemptUuid);
            return resultRepository.findByAttemptUuid(attemptUuid)
                    .orElseThrow(() -> new IllegalStateException("Attempt is marked completed but result record is missing."));
        }

        Exam exam = examRepository.findById(attempt.getExamId())
                .orElseThrow(() -> new IllegalStateException("Associated examination not found for attempt: " + attemptUuid));

        List<Question> questions = questionRepository.findByExamId(exam.getId());

        int calculatedScore = 0;
        int maxMarks = 0;
        List<Answer> recordedAnswers = new ArrayList<>();

        for (Question q : questions) {
            maxMarks += q.getMarks();
            String studentSelected = submittedAnswers != null ? submittedAnswers.get(q.getId()) : null;
            if (studentSelected != null) {
                studentSelected = studentSelected.trim().toUpperCase();
                if (studentSelected.isEmpty()) {
                    studentSelected = null;
                }
            }

            boolean isCorrect = studentSelected != null && studentSelected.equalsIgnoreCase(q.getCorrectOption());
            int marksAwarded = isCorrect ? q.getMarks() : 0;
            calculatedScore += marksAwarded;

            Answer ans = new Answer();
            ans.setAttemptId(attempt.getId());
            ans.setQuestionId(q.getId());
            ans.setSelectedOption(studentSelected);
            ans.setCorrect(isCorrect);
            ans.setMarksAwarded(marksAwarded);
            recordedAnswers.add(ans);
        }

        // Avoid division by zero
        int finalTotalMarks = (maxMarks > 0) ? maxMarks : (exam.getTotalMarks() > 0 ? exam.getTotalMarks() : 1);
        double percentage = ((double) calculatedScore / finalTotalMarks) * 100.0;
        percentage = Math.round(percentage * 100.0) / 100.0;

        double passThreshold = exam.getPassingPercentage() > 0 ? exam.getPassingPercentage() : 40.0;
        String passFail = (percentage >= passThreshold) ? "PASS" : "FAIL";

        // Mark attempt as completed
        LocalDateTime submittedAt = LocalDateTime.now();
        examAttemptRepository.updateStatus(attempt.getId(), "COMPLETED", submittedAt);

        // Save student answers
        answerRepository.saveBatch(recordedAnswers);

        // Save result record
        Result result = new Result();
        result.setAttemptId(attempt.getId());
        result.setScore(calculatedScore);
        result.setTotalMarks(finalTotalMarks);
        result.setPercentage(percentage);
        result.setPassFail(passFail);
        resultRepository.save(result);

        logger.info("Successfully evaluated attempt {}: Score={}/{}, Percentage={}% ({})",
                attemptUuid, calculatedScore, finalTotalMarks, percentage, passFail);

        return resultRepository.findByAttemptUuid(attemptUuid)
                .orElseThrow(() -> new IllegalStateException("Failed to load freshly saved result for attempt: " + attemptUuid));
    }
}
