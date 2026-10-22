package com.examflow.service;

import com.examflow.dto.DashboardMetricsDto;
import com.examflow.dto.ResultDetailDto;
import com.examflow.repository.AnswerRepository;
import com.examflow.repository.QuestionRepository;
import com.examflow.repository.ResultRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResultServiceTest {

    @Mock
    private ResultRepository resultRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AnswerRepository answerRepository;

    @InjectMocks
    private ResultService resultService;

    @Test
    @DisplayName("CSV Export: Formats headers and values correctly with escaping")
    void testExportResultsToCsv() {
        ResultDetailDto r1 = new ResultDetailDto();
        r1.setAttemptUuid("attempt-111");
        r1.setStudentId("1MS21CS001");
        r1.setStudentName("Naik, Yashwanth");
        r1.setCourse("BTech CSE");
        r1.setExamTitle("Java Programming");
        r1.setScore(9);
        r1.setTotalMarks(10);
        r1.setPercentage(90.0);
        r1.setPassFail("PASS");
        r1.setSubmittedAt(LocalDateTime.of(2026, 10, 22, 10, 30, 0));

        String csv = resultService.exportResultsToCsv(List.of(r1));

        assertThat(csv).contains("Attempt ID,Student ID,Student Name,Course,Examination,Score,Total Marks,Percentage,Status,Submission Date");
        // Verify comma in name is escaped with quotes
        assertThat(csv).contains("\"Naik, Yashwanth\"");
        assertThat(csv).contains("attempt-111");
        assertThat(csv).contains("90.00%");
        assertThat(csv).contains("PASS");
        assertThat(csv).contains("2026-10-22 10:30:00");
    }

    @Test
    @DisplayName("Dashboard Metrics: Passes through repository metrics accurately")
    void testGetDashboardMetrics() {
        DashboardMetricsDto mockMetrics = new DashboardMetricsDto();
        mockMetrics.setTotalStudents(25);
        mockMetrics.setTotalAttempts(30);
        mockMetrics.setTotalPassed(24);
        mockMetrics.setTotalFailed(6);
        mockMetrics.setPassRate(80.0);
        mockMetrics.setAverageScorePercentage(74.5);
        mockMetrics.setTotalExams(2);
        mockMetrics.setTotalQuestions(15);

        when(resultRepository.getDashboardMetrics()).thenReturn(mockMetrics);

        DashboardMetricsDto metrics = resultService.getDashboardMetrics();

        assertThat(metrics.getTotalStudents()).isEqualTo(25);
        assertThat(metrics.getTotalAttempts()).isEqualTo(30);
        assertThat(metrics.getTotalPassed()).isEqualTo(24);
        assertThat(metrics.getPassRate()).isEqualTo(80.0);
    }
}
