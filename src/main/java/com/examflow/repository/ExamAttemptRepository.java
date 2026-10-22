package com.examflow.repository;

import com.examflow.model.ExamAttempt;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class ExamAttemptRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<ExamAttempt> rowMapper = (rs, rowNum) -> {
        ExamAttempt attempt = new ExamAttempt();
        attempt.setId(rs.getLong("id"));
        attempt.setAttemptUuid(rs.getString("attempt_uuid"));
        attempt.setStudentId(rs.getLong("student_id"));
        attempt.setExamId(rs.getLong("exam_id"));
        
        Timestamp startedAt = rs.getTimestamp("started_at");
        if (startedAt != null) {
            attempt.setStartedAt(startedAt.toLocalDateTime());
        }
        Timestamp submittedAt = rs.getTimestamp("submitted_at");
        if (submittedAt != null) {
            attempt.setSubmittedAt(submittedAt.toLocalDateTime());
        }
        attempt.setStatus(rs.getString("status"));
        return attempt;
    };

    public ExamAttemptRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public ExamAttempt createAttempt(ExamAttempt attempt) {
        String sql = "INSERT INTO exam_attempts (attempt_uuid, student_id, exam_id, started_at, status) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, attempt.getAttemptUuid());
            ps.setLong(2, attempt.getStudentId());
            ps.setLong(3, attempt.getExamId());
            ps.setTimestamp(4, Timestamp.valueOf(attempt.getStartedAt()));
            ps.setString(5, attempt.getStatus());
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            attempt.setId(keyHolder.getKey().longValue());
        }
        return attempt;
    }

    public Optional<ExamAttempt> findByUuid(String attemptUuid) {
        String sql = "SELECT * FROM exam_attempts WHERE attempt_uuid = ?";
        try {
            ExamAttempt attempt = jdbcTemplate.queryForObject(sql, rowMapper, attemptUuid);
            return Optional.ofNullable(attempt);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<ExamAttempt> findById(Long id) {
        String sql = "SELECT * FROM exam_attempts WHERE id = ?";
        try {
            ExamAttempt attempt = jdbcTemplate.queryForObject(sql, rowMapper, id);
            return Optional.ofNullable(attempt);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void updateStatus(Long id, String status, LocalDateTime submittedAt) {
        String sql = "UPDATE exam_attempts SET status = ?, submitted_at = ? WHERE id = ?";
        Timestamp ts = (submittedAt != null) ? Timestamp.valueOf(submittedAt) : null;
        jdbcTemplate.update(sql, status, ts, id);
    }

    public long count() {
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM exam_attempts", Long.class);
        return count != null ? count : 0L;
    }

    public long countCompleted() {
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM exam_attempts WHERE status = 'COMPLETED'", Long.class);
        return count != null ? count : 0L;
    }
}
