package com.examflow.controller;

import com.examflow.dto.DashboardMetricsDto;
import com.examflow.service.ExaminationService;
import com.examflow.service.ResultService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final ResultService resultService;
    private final ExaminationService examinationService;

    public HomeController(ResultService resultService, ExaminationService examinationService) {
        this.resultService = resultService;
        this.examinationService = examinationService;
    }

    @GetMapping("/")
    public String home(Model model) {
        DashboardMetricsDto metrics = resultService.getDashboardMetrics();
        model.addAttribute("metrics", metrics);
        model.addAttribute("activeExams", examinationService.getActiveExams());
        return "index";
    }

    @GetMapping("/about")
    public String about(Model model) {
        return "about";
    }
}
