package com.examflow.repository;

import com.examflow.model.Student;
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
public class StudentRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Student> rowMapper = (rs, rowNum) -> {
        Student student = new Student();
        student.setId(rs.getLong("id"));
        student.setStudentId(rs.getString("student_id"));
        student.setFullName(rs.getString("full_name"));
        student.setEmail(rs.getString("email"));
        student.setCourse(rs.getString("course"));
        
        Timestamp timestamp = rs.getTimestamp("created_at");
        if (timestamp != null) {
            student.setCreatedAt(timestamp.toLocalDateTime());
        }
        return student;
    };

    public StudentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Student> findByStudentId(String studentId) {
        String sql = "SELECT * FROM students WHERE UPPER(student_id) = UPPER(?)";
        try {
            Student student = jdbcTemplate.queryForObject(sql, rowMapper, studentId.trim());
            return Optional.ofNullable(student);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Student> findById(Long id) {
        String sql = "SELECT * FROM students WHERE id = ?";
        try {
            Student student = jdbcTemplate.queryForObject(sql, rowMapper, id);
            return Optional.ofNullable(student);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Student saveOrUpdate(Student student) {
        Optional<Student> existing = findByStudentId(student.getStudentId());
        if (existing.isPresent()) {
            Student existingStudent = existing.get();
            String updateSql = "UPDATE students SET full_name = ?, email = ?, course = ? WHERE id = ?";
            jdbcTemplate.update(updateSql, student.getFullName(), student.getEmail(), student.getCourse(), existingStudent.getId());
            student.setId(existingStudent.getId());
            student.setCreatedAt(existingStudent.getCreatedAt());
            return student;
        }

        String insertSql = "INSERT INTO students (student_id, full_name, email, course, created_at) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        LocalDateTime now = LocalDateTime.now();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, student.getStudentId().toUpperCase().trim());
            ps.setString(2, student.getFullName().trim());
            ps.setString(3, student.getEmail());
            ps.setString(4, student.getCourse());
            ps.setTimestamp(5, Timestamp.valueOf(now));
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            student.setId(keyHolder.getKey().longValue());
        }
        student.setCreatedAt(now);
        return student;
    }

    public long count() {
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM students", Long.class);
        return count != null ? count : 0L;
    }
}
