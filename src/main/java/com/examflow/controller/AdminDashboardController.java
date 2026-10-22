package com.examflow.controller;

import com.examflow.dto.DashboardMetricsDto;
import com.examflow.dto.ResultDetailDto;
import com.examflow.model.Exam;
import com.examflow.service.ExaminationService;
import com.examflow.service.ResultService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    private final ResultService resultService;
    private final ExaminationService examinationService;

    public AdminDashboardController(ResultService resultService, ExaminationService examinationService) {
        this.resultService = resultService;
        this.examinationService = examinationService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(value = "search", required = false) String search,
                            @RequestParam(value = "examId", required = false) Long examId,
                            @RequestParam(value = "passFail", required = false) String passFail,
                            Model model) {
        DashboardMetricsDto metrics = resultService.getDashboardMetrics();
        List<ResultDetailDto> results = resultService.searchAndFilterResults(search, examId, passFail);
        List<Exam> exams = examinationService.getActiveExams();

        model.addAttribute("metrics", metrics);
        model.addAttribute("results", results);
        model.addAttribute("exams", exams);
        model.addAttribute("search", search);
        model.addAttribute("selectedExamId", examId);
        model.addAttribute("selectedPassFail", passFail);

        return "admin/dashboard";
    }

    @GetMapping("/result/{attemptUuid}")
    public String viewResultDetails(@PathVariable("attemptUuid") String attemptUuid,
                                    Model model) {
        Optional<ResultDetailDto> detailOpt = resultService.getResultDetail(attemptUuid);
        if (detailOpt.isEmpty()) {
            return "redirect:/admin/dashboard?error=notfound";
        }

        model.addAttribute("result", detailOpt.get());
        return "admin/result-detail";
    }

    @GetMapping("/results/export-csv")
    public ResponseEntity<byte[]> exportCsv(@RequestParam(value = "search", required = false) String search,
                                            @RequestParam(value = "examId", required = false) Long examId,
                                            @RequestParam(value = "passFail", required = false) String passFail) {
        List<ResultDetailDto> results = resultService.searchAndFilterResults(search, examId, passFail);
        String csvContent = resultService.exportResultsToCsv(results);

        String filename = "examflow_results_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv";
        byte[] bytes = csvContent.getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .contentLength(bytes.length)
                .body(bytes);
    }
}
