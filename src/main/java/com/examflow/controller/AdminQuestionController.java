package com.examflow.controller;

import com.examflow.dto.QuestionFormDto;
import com.examflow.model.Exam;
import com.examflow.model.Question;
import com.examflow.service.ExaminationService;
import com.examflow.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/questions")
public class AdminQuestionController {

    private final QuestionService questionService;
    private final ExaminationService examinationService;

    public AdminQuestionController(QuestionService questionService, ExaminationService examinationService) {
        this.questionService = questionService;
        this.examinationService = examinationService;
    }

    @GetMapping
    public String listQuestions(@RequestParam(value = "examId", required = false) Long examId,
                                Model model) {
        List<Question> questions;
        if (examId != null && examId > 0) {
            questions = questionService.getQuestionsByExam(examId);
        } else {
            questions = questionService.getAllQuestions();
        }

        List<Exam> exams = examinationService.getActiveExams();
        model.addAttribute("questions", questions);
        model.addAttribute("exams", exams);
        model.addAttribute("selectedExamId", examId);

        return "admin/questions";
    }

    @GetMapping("/new")
    public String showCreateForm(@RequestParam(value = "examId", required = false) Long examId,
                                 Model model) {
        QuestionFormDto dto = new QuestionFormDto();
        if (examId != null) {
            dto.setExamId(examId);
        }
        model.addAttribute("questionForm", dto);
        model.addAttribute("exams", examinationService.getActiveExams());
        model.addAttribute("isEdit", false);
        return "admin/question-form";
    }

    @PostMapping("/save")
    public String saveQuestion(@Valid @ModelAttribute("questionForm") QuestionFormDto questionForm,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("exams", examinationService.getActiveExams());
            model.addAttribute("isEdit", false);
            return "admin/question-form";
        }

        questionService.createQuestion(questionForm);
        redirectAttributes.addFlashAttribute("successMessage", "Question successfully added to examination.");
        return "redirect:/admin/questions?examId=" + questionForm.getExamId();
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        Optional<Question> questionOpt = questionService.getQuestionById(id);
        if (questionOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Question not found with ID: " + id);
            return "redirect:/admin/questions";
        }

        QuestionFormDto dto = questionService.toFormDto(questionOpt.get());
        model.addAttribute("questionForm", dto);
        model.addAttribute("exams", examinationService.getActiveExams());
        model.addAttribute("isEdit", true);
        return "admin/question-form";
    }

    @PostMapping("/update/{id}")
    public String updateQuestion(@PathVariable("id") Long id,
                                 @Valid @ModelAttribute("questionForm") QuestionFormDto questionForm,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("exams", examinationService.getActiveExams());
            model.addAttribute("isEdit", true);
            return "admin/question-form";
        }

        questionForm.setId(id);
        questionService.updateQuestion(questionForm);
        redirectAttributes.addFlashAttribute("successMessage", "Question successfully updated.");
        return "redirect:/admin/questions?examId=" + questionForm.getExamId();
    }

    @PostMapping("/delete/{id}")
    public String deleteQuestion(@PathVariable("id") Long id,
                                 @RequestParam(value = "examId", required = false) Long examId,
                                 RedirectAttributes redirectAttributes) {
        boolean deleted = questionService.deleteQuestion(id);
        if (deleted) {
            redirectAttributes.addFlashAttribute("successMessage", "Question deleted successfully.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not delete question (not found).");
        }

        return "redirect:/admin/questions" + (examId != null ? "?examId=" + examId : "");
    }
}
