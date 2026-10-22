package com.examflow.service;

import com.examflow.model.AdminUser;
import com.examflow.repository.AdminUserRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${examflow.admin.username:admin}")
    private String defaultAdminUsername;

    @Value("${examflow.admin.default-password:admin123}")
    private String defaultAdminPassword;

    public AuthService(AdminUserRepository adminUserRepository, PasswordEncoder passwordEncoder) {
        this.adminUserRepository = adminUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void initDefaultAdmin() {
        Optional<AdminUser> existing = adminUserRepository.findByUsername(defaultAdminUsername);
        if (existing.isEmpty()) {
            logger.info("Initializing default administrator account: {}", defaultAdminUsername);
            AdminUser admin = new AdminUser();
            admin.setUsername(defaultAdminUsername);
            admin.setPasswordHash(passwordEncoder.encode(defaultAdminPassword));
            admin.setFullName("System Administrator");
            admin.setRole("ADMIN");
            adminUserRepository.saveOrUpdate(admin);
            logger.info("Default administrator account initialized successfully.");
        }
    }

    public Optional<AdminUser> authenticate(String username, String rawPassword) {
        if (username == null || rawPassword == null) {
            return Optional.empty();
        }

        Optional<AdminUser> userOpt = adminUserRepository.findByUsername(username.trim());
        if (userOpt.isPresent()) {
            AdminUser user = userOpt.get();
            if (passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }
}
