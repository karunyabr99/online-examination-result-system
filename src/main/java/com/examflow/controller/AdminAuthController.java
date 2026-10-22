package com.examflow.controller;

import com.examflow.config.AdminAuthInterceptor;
import com.examflow.model.AdminUser;
import com.examflow.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminAuthController {

    private final AuthService authService;

    public AdminAuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout,
                                Model model,
                                HttpSession session) {
        if (session != null && session.getAttribute(AdminAuthInterceptor.ADMIN_SESSION_KEY) != null) {
            return "redirect:/admin/dashboard";
        }

        if ("invalid".equals(error)) {
            model.addAttribute("errorMessage", "Invalid administrator username or password.");
        } else if ("required".equals(error)) {
            model.addAttribute("errorMessage", "Please log in to access administrative functions.");
        }

        if (logout != null) {
            model.addAttribute("successMessage", "You have been successfully logged out.");
        }

        return "admin/login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam("username") String username,
                               @RequestParam("password") String password,
                               HttpServletRequest request,
                               RedirectAttributes redirectAttributes) {
        Optional<AdminUser> userOpt = authService.authenticate(username, password);
        if (userOpt.isPresent()) {
            HttpSession session = request.getSession(true);
            session.setAttribute(AdminAuthInterceptor.ADMIN_SESSION_KEY, userOpt.get());
            return "redirect:/admin/dashboard";
        } else {
            return "redirect:/admin/login?error=invalid";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/admin/login?logout=true";
    }
}
