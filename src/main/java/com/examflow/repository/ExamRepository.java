package com.examflow.repository;

import com.examflow.model.Exam;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class ExamRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Exam> rowMapper = (rs, rowNum) -> {
        Exam exam = new Exam();
        exam.setId(rs.getLong("id"));
        exam.setTitle(rs.getString("title"));
        exam.setSubject(rs.getString("subject"));
        exam.setDescription(rs.getString("description"));
        exam.setDurationMinutes(rs.getInt("duration_minutes"));
        exam.setTotalMarks(rs.getInt("total_marks"));
        exam.setPassingPercentage(rs.getDouble("passing_percentage"));
        exam.setActive(rs.getInt("is_active") == 1);
        return exam;
    };

    public ExamRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Exam> findAll() {
        return jdbcTemplate.query("SELECT * FROM exams ORDER BY id ASC", rowMapper);
    }

    public List<Exam> findActiveExams() {
        return jdbcTemplate.query("SELECT * FROM exams WHERE is_active = 1 ORDER BY id ASC", rowMapper);
    }

    public Optional<Exam> findById(Long id) {
        String sql = "SELECT * FROM exams WHERE id = ?";
        try {
            Exam exam = jdbcTemplate.queryForObject(sql, rowMapper, id);
            return Optional.ofNullable(exam);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Exam save(Exam exam) {
        String sql = "INSERT INTO exams (title, subject, description, duration_minutes, total_marks, passing_percentage, is_active) VALUES (?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, exam.getTitle());
            ps.setString(2, exam.getSubject());
            ps.setString(3, exam.getDescription());
            ps.setInt(4, exam.getDurationMinutes());
            ps.setInt(5, exam.getTotalMarks());
            ps.setDouble(6, exam.getPassingPercentage());
            ps.setInt(7, exam.isActive() ? 1 : 0);
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            exam.setId(keyHolder.getKey().longValue());
        }
        return exam;
    }

    public void update(Exam exam) {
        String sql = "UPDATE exams SET title = ?, subject = ?, description = ?, duration_minutes = ?, total_marks = ?, passing_percentage = ?, is_active = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                exam.getTitle(),
                exam.getSubject(),
                exam.getDescription(),
                exam.getDurationMinutes(),
                exam.getTotalMarks(),
                exam.getPassingPercentage(),
                exam.isActive() ? 1 : 0,
                exam.getId()
        );
    }

    public long count() {
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM exams", Long.class);
        return count != null ? count : 0L;
    }
}
