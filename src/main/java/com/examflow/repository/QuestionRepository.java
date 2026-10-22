package com.examflow.repository;

import com.examflow.model.Question;
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
public class QuestionRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Question> rowMapper = (rs, rowNum) -> {
        Question q = new Question();
        q.setId(rs.getLong("id"));
        q.setExamId(rs.getLong("exam_id"));
        q.setQuestionText(rs.getString("question_text"));
        q.setOptionA(rs.getString("option_a"));
        q.setOptionB(rs.getString("option_b"));
        q.setOptionC(rs.getString("option_c"));
        q.setOptionD(rs.getString("option_d"));
        q.setCorrectOption(rs.getString("correct_option"));
        q.setMarks(rs.getInt("marks"));
        q.setExplanation(rs.getString("explanation"));
        return q;
    };

    public QuestionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Question> findByExamId(Long examId) {
        String sql = "SELECT * FROM questions WHERE exam_id = ? ORDER BY id ASC";
        return jdbcTemplate.query(sql, rowMapper, examId);
    }

    public List<Question> findAll() {
        return jdbcTemplate.query("SELECT * FROM questions ORDER BY exam_id ASC, id ASC", rowMapper);
    }

    public Optional<Question> findById(Long id) {
        String sql = "SELECT * FROM questions WHERE id = ?";
        try {
            Question q = jdbcTemplate.queryForObject(sql, rowMapper, id);
            return Optional.ofNullable(q);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Question save(Question question) {
        String sql = "INSERT INTO questions (exam_id, question_text, option_a, option_b, option_c, option_d, correct_option, marks, explanation) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, question.getExamId());
            ps.setString(2, question.getQuestionText());
            ps.setString(3, question.getOptionA());
            ps.setString(4, question.getOptionB());
            ps.setString(5, question.getOptionC());
            ps.setString(6, question.getOptionD());
            ps.setString(7, question.getCorrectOption());
            ps.setInt(8, question.getMarks());
            ps.setString(9, question.getExplanation());
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            question.setId(keyHolder.getKey().longValue());
        }
        return question;
    }

    public void update(Question question) {
        String sql = "UPDATE questions SET exam_id = ?, question_text = ?, option_a = ?, option_b = ?, option_c = ?, option_d = ?, correct_option = ?, marks = ?, explanation = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                question.getExamId(),
                question.getQuestionText(),
                question.getOptionA(),
                question.getOptionB(),
                question.getOptionC(),
                question.getOptionD(),
                question.getCorrectOption(),
                question.getMarks(),
                question.getExplanation(),
                question.getId()
        );
    }

    public boolean deleteById(Long id) {
        int affected = jdbcTemplate.update("DELETE FROM questions WHERE id = ?", id);
        return affected > 0;
    }

    public long count() {
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM questions", Long.class);
        return count != null ? count : 0L;
    }

    public long countByExamId(Long examId) {
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM questions WHERE exam_id = ?", Long.class, examId);
        return count != null ? count : 0L;
    }
}
