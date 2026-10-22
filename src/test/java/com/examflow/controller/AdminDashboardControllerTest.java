package com.examflow.controller;

import com.examflow.config.AdminAuthInterceptor;
import com.examflow.config.WebMvcConfig;
import com.examflow.dto.DashboardMetricsDto;
import com.examflow.model.AdminUser;
import com.examflow.service.ExaminationService;
import com.examflow.service.ResultService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminDashboardController.class)
@Import({WebMvcConfig.class, AdminAuthInterceptor.class})
public class AdminDashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ResultService resultService;

    @MockBean
    private ExaminationService examinationService;

    @Test
    @DisplayName("Admin Security: Unauthenticated request to /admin/dashboard redirects to /admin/login?error=required")
    void testUnauthenticatedAccessRedirectsToLogin() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/login?error=required"));
    }

    @Test
    @DisplayName("Admin Security: Authenticated admin session successfully accesses /admin/dashboard")
    void testAuthenticatedAdminCanAccessDashboard() throws Exception {
        MockHttpSession session = new MockHttpSession();
        AdminUser admin = new AdminUser(1L, "admin", "hash", "Administrator", "ADMIN", LocalDateTime.now());
        session.setAttribute(AdminAuthInterceptor.ADMIN_SESSION_KEY, admin);

        DashboardMetricsDto metrics = new DashboardMetricsDto();
        metrics.setTotalStudents(12);
        metrics.setTotalAttempts(15);
        when(resultService.getDashboardMetrics()).thenReturn(metrics);
        when(resultService.searchAndFilterResults(any(), any(), any())).thenReturn(List.of());
        when(examinationService.getActiveExams()).thenReturn(List.of());

        mockMvc.perform(get("/admin/dashboard").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"))
                .andExpect(model().attributeExists("metrics"))
                .andExpect(content().string(containsString("Examination Results Dashboard")));
    }

    @Test
    @DisplayName("CSV Export: Generates downloadable attachment response")
    void testExportCsvEndpoint() throws Exception {
        MockHttpSession session = new MockHttpSession();
        AdminUser admin = new AdminUser(1L, "admin", "hash", "Administrator", "ADMIN", LocalDateTime.now());
        session.setAttribute(AdminAuthInterceptor.ADMIN_SESSION_KEY, admin);

        when(resultService.searchAndFilterResults(any(), any(), any())).thenReturn(List.of());
        when(resultService.exportResultsToCsv(any())).thenReturn("Attempt ID,Student ID\n1,100\n");

        mockMvc.perform(get("/admin/results/export-csv").session(session))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", containsString("attachment; filename=\"examflow_results_")))
                .andExpect(content().contentType("text/csv;charset=UTF-8"))
                .andExpect(content().string(containsString("Attempt ID,Student ID")));
    }
}
