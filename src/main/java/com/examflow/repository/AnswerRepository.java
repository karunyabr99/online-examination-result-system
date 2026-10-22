package com.examflow.repository;

import com.examflow.model.Answer;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
public class AnswerRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Answer> rowMapper = (rs, rowNum) -> {
        Answer answer = new Answer();
        answer.setId(rs.getLong("id"));
        answer.setAttemptId(rs.getLong("attempt_id"));
        answer.setQuestionId(rs.getLong("question_id"));
        answer.setSelectedOption(rs.getString("selected_option"));
        answer.setCorrect(rs.getInt("is_correct") == 1);
        answer.setMarksAwarded(rs.getInt("marks_awarded"));
        return answer;
    };

    public AnswerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveBatch(List<Answer> answers) {
        if (answers == null || answers.isEmpty()) {
            return;
        }

        String sql = "INSERT INTO answers (attempt_id, question_id, selected_option, is_correct, marks_awarded) VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Answer a = answers.get(i);
                ps.setLong(1, a.getAttemptId());
                ps.setLong(2, a.getQuestionId());
                ps.setString(3, a.getSelectedOption());
                ps.setInt(4, a.isCorrect() ? 1 : 0);
                ps.setInt(5, a.getMarksAwarded());
            }

            @Override
            public int getBatchSize() {
                return answers.size();
            }
        });
    }

    public List<Answer> findByAttemptId(Long attemptId) {
        String sql = "SELECT * FROM answers WHERE attempt_id = ? ORDER BY question_id ASC";
        return jdbcTemplate.query(sql, rowMapper, attemptId);
    }
}
