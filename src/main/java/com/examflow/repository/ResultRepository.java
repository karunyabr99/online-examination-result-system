package com.examflow.repository;

import com.examflow.dto.DashboardMetricsDto;
import com.examflow.dto.ResultDetailDto;
import com.examflow.model.Result;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ResultRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Result> resultRowMapper = (rs, rowNum) -> {
        Result r = new Result();
        r.setId(rs.getLong("id"));
        r.setAttemptId(rs.getLong("attempt_id"));
        r.setScore(rs.getInt("score"));
        r.setTotalMarks(rs.getInt("total_marks"));
        r.setPercentage(rs.getDouble("percentage"));
        r.setPassFail(rs.getString("pass_fail"));
        Timestamp ts = rs.getTimestamp("evaluated_at");
        if (ts != null) {
            r.setEvaluatedAt(ts.toLocalDateTime());
        }
        return r;
    };

    private final RowMapper<ResultDetailDto> detailRowMapper = (rs, rowNum) -> {
        ResultDetailDto dto = new ResultDetailDto();
        dto.setResultId(rs.getLong("result_id"));
        dto.setAttemptId(rs.getLong("attempt_id"));
        dto.setAttemptUuid(rs.getString("attempt_uuid"));
        dto.setStudentId(rs.getString("student_id"));
        dto.setStudentName(rs.getString("student_name"));
        dto.setStudentEmail(rs.getString("student_email"));
        dto.setCourse(rs.getString("course"));

        dto.setExamId(rs.getLong("exam_id"));
        dto.setExamTitle(rs.getString("exam_title"));
        dto.setExamSubject(rs.getString("exam_subject"));
        dto.setDurationMinutes(rs.getInt("duration_minutes"));
        dto.setPassingPercentage(rs.getDouble("passing_percentage"));

        dto.setScore(rs.getInt("score"));
        dto.setTotalMarks(rs.getInt("total_marks"));
        dto.setPercentage(rs.getDouble("percentage"));
        dto.setPassFail(rs.getString("pass_fail"));

        Timestamp started = rs.getTimestamp("started_at");
        if (started != null) {
            dto.setStartedAt(started.toLocalDateTime());
        }
        Timestamp submitted = rs.getTimestamp("submitted_at");
        if (submitted != null) {
            dto.setSubmittedAt(submitted.toLocalDateTime());
        }
        Timestamp evaluated = rs.getTimestamp("evaluated_at");
        if (evaluated != null) {
            dto.setEvaluatedAt(evaluated.toLocalDateTime());
        }

        if (dto.getStartedAt() != null && dto.getSubmittedAt() != null) {
            long seconds = Duration.between(dto.getStartedAt(), dto.getSubmittedAt()).getSeconds();
            dto.setTimeTakenSeconds(Math.max(0, seconds));
        }

        return dto;
    };

    public ResultRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Result save(Result result) {
        String sql = "INSERT INTO results (attempt_id, score, total_marks, percentage, pass_fail, evaluated_at) VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        LocalDateTime now = LocalDateTime.now();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, result.getAttemptId());
            ps.setInt(2, result.getScore());
            ps.setInt(3, result.getTotalMarks());
            ps.setDouble(4, result.getPercentage());
            ps.setString(5, result.getPassFail());
            ps.setTimestamp(6, Timestamp.valueOf(now));
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            result.setId(keyHolder.getKey().longValue());
        }
        result.setEvaluatedAt(now);
        return result;
    }

    public Optional<Result> findByAttemptId(Long attemptId) {
        String sql = "SELECT * FROM results WHERE attempt_id = ?";
        try {
            Result r = jdbcTemplate.queryForObject(sql, resultRowMapper, attemptId);
            return Optional.ofNullable(r);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<ResultDetailDto> findByAttemptUuid(String attemptUuid) {
        String sql = "SELECT r.id AS result_id, r.score, r.total_marks, r.percentage, r.pass_fail, r.evaluated_at, " +
                "ea.id AS attempt_id, ea.attempt_uuid, ea.started_at, ea.submitted_at, ea.status, " +
                "s.student_id, s.full_name AS student_name, s.email AS student_email, s.course, " +
                "e.id AS exam_id, e.title AS exam_title, e.subject AS exam_subject, e.duration_minutes, e.passing_percentage " +
                "FROM results r " +
                "JOIN exam_attempts ea ON r.attempt_id = ea.id " +
                "JOIN students s ON ea.student_id = s.id " +
                "JOIN exams e ON ea.exam_id = e.id " +
                "WHERE ea.attempt_uuid = ?";
        try {
            ResultDetailDto dto = jdbcTemplate.queryForObject(sql, detailRowMapper, attemptUuid);
            return Optional.ofNullable(dto);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<ResultDetailDto> findAllResultDetails(String search, Long examId, String passFail) {
        StringBuilder sql = new StringBuilder(
                "SELECT r.id AS result_id, r.score, r.total_marks, r.percentage, r.pass_fail, r.evaluated_at, " +
                "ea.id AS attempt_id, ea.attempt_uuid, ea.started_at, ea.submitted_at, ea.status, " +
                "s.student_id, s.full_name AS student_name, s.email AS student_email, s.course, " +
                "e.id AS exam_id, e.title AS exam_title, e.subject AS exam_subject, e.duration_minutes, e.passing_percentage " +
                "FROM results r " +
                "JOIN exam_attempts ea ON r.attempt_id = ea.id " +
                "JOIN students s ON ea.student_id = s.id " +
                "JOIN exams e ON ea.exam_id = e.id " +
                "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        if (search != null && !search.trim().isEmpty()) {
            String pattern = "%" + search.trim() + "%";
            sql.append("AND (UPPER(s.full_name) LIKE UPPER(?) OR UPPER(s.student_id) LIKE UPPER(?)) ");
            params.add(pattern);
            params.add(pattern);
        }

        if (examId != null && examId > 0) {
            sql.append("AND e.id = ? ");
            params.add(examId);
        }

        if (passFail != null && !passFail.trim().isEmpty() && !passFail.equalsIgnoreCase("ALL")) {
            sql.append("AND UPPER(r.pass_fail) = UPPER(?) ");
            params.add(passFail.trim());
        }

        sql.append("ORDER BY r.evaluated_at DESC, r.id DESC");

        return jdbcTemplate.query(sql.toString(), detailRowMapper, params.toArray());
    }

    public DashboardMetricsDto getDashboardMetrics() {
        DashboardMetricsDto metrics = new DashboardMetricsDto();

        Long totalStudents = jdbcTemplate.queryForObject("SELECT COUNT(DISTINCT student_id) FROM exam_attempts WHERE status = 'COMPLETED'", Long.class);
        metrics.setTotalStudents(totalStudents != null ? totalStudents : 0L);

        Long totalAttempts = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM results", Long.class);
        metrics.setTotalAttempts(totalAttempts != null ? totalAttempts : 0L);

        Long totalPassed = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM results WHERE UPPER(pass_fail) = 'PASS'", Long.class);
        metrics.setTotalPassed(totalPassed != null ? totalPassed : 0L);

        Long totalFailed = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM results WHERE UPPER(pass_fail) = 'FAIL'", Long.class);
        metrics.setTotalFailed(totalFailed != null ? totalFailed : 0L);

        if (metrics.getTotalAttempts() > 0) {
            double passRate = (double) metrics.getTotalPassed() / metrics.getTotalAttempts() * 100.0;
            metrics.setPassRate(Math.round(passRate * 100.0) / 100.0);

            Double avgPct = jdbcTemplate.queryForObject("SELECT AVG(percentage) FROM results", Double.class);
            metrics.setAverageScorePercentage(avgPct != null ? Math.round(avgPct * 100.0) / 100.0 : 0.0);
        } else {
            metrics.setPassRate(0.0);
            metrics.setAverageScorePercentage(0.0);
        }

        Long totalExams = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM exams WHERE is_active = 1", Long.class);
        metrics.setTotalExams(totalExams != null ? totalExams : 0L);

        Long totalQuestions = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM questions", Long.class);
        metrics.setTotalQuestions(totalQuestions != null ? totalQuestions : 0L);

        return metrics;
    }
}
