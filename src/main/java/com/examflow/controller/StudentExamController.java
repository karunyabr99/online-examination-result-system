package com.examflow.controller;

import com.examflow.dto.ResultDetailDto;
import com.examflow.dto.SanitizedQuestionDto;
import com.examflow.dto.StudentRegistrationDto;
import com.examflow.model.Exam;
import com.examflow.model.ExamAttempt;
import com.examflow.model.Student;
import com.examflow.service.ExaminationService;
import com.examflow.service.QuestionService;
import com.examflow.service.ResultService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequestMapping("/exam")
public class StudentExamController {

    private static final Logger logger = LoggerFactory.getLogger(StudentExamController.class);

    private final ExaminationService examinationService;
    private final QuestionService questionService;
    private final ResultService resultService;

    public StudentExamController(ExaminationService examinationService,
                                 QuestionService questionService,
                                 ResultService resultService) {
        this.examinationService = examinationService;
        this.questionService = questionService;
        this.resultService = resultService;
    }

    /**
     * Step 1: Student Entry Form
     */
    @GetMapping("/register")
    public String showRegistrationForm(@RequestParam(value = "examId", required = false) Long examId,
                                       Model model) {
        StudentRegistrationDto dto = new StudentRegistrationDto();
        if (examId != null) {
            dto.setExamId(examId);
        }
        model.addAttribute("registrationDto", dto);
        model.addAttribute("activeExams", examinationService.getActiveExams());
        return "exam/register";
    }

    /**
     * Step 2: Validate entry form and display Examination Instructions
     */
    @PostMapping("/instructions")
    public String showInstructions(@Valid @ModelAttribute("registrationDto") StudentRegistrationDto registrationDto,
                                   BindingResult bindingResult,
                                   Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("activeExams", examinationService.getActiveExams());
            return "exam/register";
        }

        Optional<Exam> examOpt = examinationService.getExamById(registrationDto.getExamId());
        if (examOpt.isEmpty()) {
            bindingResult.rejectValue("examId", "error.examId", "The selected examination does not exist.");
            model.addAttribute("activeExams", examinationService.getActiveExams());
            return "exam/register";
        }

        Exam exam = examOpt.get();
        List<SanitizedQuestionDto> questions = questionService.getSanitizedQuestionsForExam(exam.getId());

        model.addAttribute("registrationDto", registrationDto);
        model.addAttribute("exam", exam);
        model.addAttribute("questionCount", questions.size());
        return "exam/instructions";
    }

    /**
     * Step 3: Start examination, record attempt in DB, and redirect to live test UI
     */
    @PostMapping("/start")
    public String startExam(@Valid @ModelAttribute("registrationDto") StudentRegistrationDto registrationDto,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Invalid examination registration details.");
            return "redirect:/exam/register";
        }

        Optional<Exam> examOpt = examinationService.getExamById(registrationDto.getExamId());
        if (examOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Selected examination not found.");
            return "redirect:/exam/register";
        }

        Student student = examinationService.registerOrUpdateStudent(registrationDto);
        ExamAttempt attempt = examinationService.createAttempt(student, examOpt.get());

        return "redirect:/exam/take/" + attempt.getAttemptUuid();
    }

    /**
     * Step 4: Live MCQ Examination Interface
     */
    @GetMapping("/take/{attemptUuid}")
    public String takeExam(@PathVariable("attemptUuid") String attemptUuid,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        Optional<ExamAttempt> attemptOpt = examinationService.getAttemptByUuid(attemptUuid);
        if (attemptOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Examination session not found.");
            return "redirect:/exam/register";
        }

        ExamAttempt attempt = attemptOpt.get();
        if ("COMPLETED".equalsIgnoreCase(attempt.getStatus())) {
            // Already submitted, redirect straight to result
            return "redirect:/exam/result/" + attemptUuid;
        }

        Exam exam = examinationService.getExamById(attempt.getExamId())
                .orElseThrow(() -> new IllegalStateException("Associated examination not found"));

        List<SanitizedQuestionDto> questions = questionService.getSanitizedQuestionsForExam(exam.getId());

        model.addAttribute("attempt", attempt);
        model.addAttribute("exam", exam);
        model.addAttribute("questions", questions);
        model.addAttribute("durationSeconds", exam.getDurationMinutes() * 60);

        return "exam/take";
    }

    /**
     * Step 5: Submit Examination and perform Server-side Automatic Evaluation
     */
    @PostMapping("/submit")
    public String submitExam(@RequestParam("attemptUuid") String attemptUuid,
                             HttpServletRequest request,
                             RedirectAttributes redirectAttributes) {
        try {
            // Extract all answers submitted from the form (supports answers[123]=A or answer_123=A)
            Map<Long, String> userAnswers = new HashMap<>();
            Enumeration<String> paramNames = request.getParameterNames();

            while (paramNames.hasMoreElements()) {
                String paramName = paramNames.nextElement();
                Long questionId = null;

                if (paramName.startsWith("answers[") && paramName.endsWith("]")) {
                    String idStr = paramName.substring(8, paramName.length() - 1);
                    questionId = Long.parseLong(idStr);
                } else if (paramName.startsWith("answer_")) {
                    String idStr = paramName.substring(7);
                    questionId = Long.parseLong(idStr);
                }

                if (questionId != null) {
                    String val = request.getParameter(paramName);
                    if (val != null && !val.trim().isEmpty()) {
                        userAnswers.put(questionId, val.trim().toUpperCase());
                    }
                }
            }

            examinationService.evaluateAndSaveAttempt(attemptUuid, userAnswers);
            return "redirect:/exam/result/" + attemptUuid;

        } catch (Exception e) {
            logger.error("Error evaluating examination attempt: {}", attemptUuid, e);
            redirectAttributes.addFlashAttribute("error", "Error processing examination: " + e.getMessage());
            return "redirect:/exam/result/" + attemptUuid;
        }
    }

    /**
     * Step 6: Polished Student Result Card
     */
    @GetMapping("/result/{attemptUuid}")
    public String showResult(@PathVariable("attemptUuid") String attemptUuid,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        Optional<ResultDetailDto> resultOpt = resultService.getResultDetail(attemptUuid);
        if (resultOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Result record not found for attempt: " + attemptUuid);
            return "redirect:/";
        }

        model.addAttribute("result", resultOpt.get());
        return "exam/result";
    }
}
