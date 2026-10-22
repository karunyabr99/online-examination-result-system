package com.examflow.repository;

import com.examflow.model.AdminUser;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class AdminUserRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<AdminUser> rowMapper = (rs, rowNum) -> {
        AdminUser user = new AdminUser();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setFullName(rs.getString("full_name"));
        user.setRole(rs.getString("role"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            user.setCreatedAt(ts.toLocalDateTime());
        }
        return user;
    };

    public AdminUserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<AdminUser> findByUsername(String username) {
        String sql = "SELECT * FROM admin_users WHERE UPPER(username) = UPPER(?)";
        try {
            AdminUser user = jdbcTemplate.queryForObject(sql, rowMapper, username.trim());
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void saveOrUpdate(AdminUser user) {
        Optional<AdminUser> existing = findByUsername(user.getUsername());
        if (existing.isPresent()) {
            String updateSql = "UPDATE admin_users SET password_hash = ?, full_name = ?, role = ? WHERE id = ?";
            jdbcTemplate.update(updateSql, user.getPasswordHash(), user.getFullName(), user.getRole(), existing.get().getId());
        } else {
            String insertSql = "INSERT INTO admin_users (username, password_hash, full_name, role, created_at) VALUES (?, ?, ?, ?, ?)";
            jdbcTemplate.update(insertSql,
                    user.getUsername().trim().toLowerCase(),
                    user.getPasswordHash(),
                    user.getFullName(),
                    user.getRole(),
                    Timestamp.valueOf(LocalDateTime.now())
            );
        }
    }

    public long count() {
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM admin_users", Long.class);
        return count != null ? count : 0L;
    }
}
