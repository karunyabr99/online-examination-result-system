package com.examflow.service;

import com.examflow.dto.QuestionFormDto;
import com.examflow.dto.SanitizedQuestionDto;
import com.examflow.model.Question;
import com.examflow.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    public List<Question> getQuestionsByExam(Long examId) {
        return questionRepository.findByExamId(examId);
    }

    /**
     * Produces student-facing question objects with correct answers strictly stripped out.
     */
    public List<SanitizedQuestionDto> getSanitizedQuestionsForExam(Long examId) {
        List<Question> questions = questionRepository.findByExamId(examId);
        List<SanitizedQuestionDto> sanitized = new ArrayList<>(questions.size());

        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            sanitized.add(new SanitizedQuestionDto(
                    q.getId(),
                    q.getExamId(),
                    i + 1,
                    q.getQuestionText(),
                    q.getOptionA(),
                    q.getOptionB(),
                    q.getOptionC(),
                    q.getOptionD(),
                    q.getMarks()
            ));
        }
        return sanitized;
    }

    public Optional<Question> getQuestionById(Long id) {
        return questionRepository.findById(id);
    }

    public Question createQuestion(QuestionFormDto dto) {
        Question q = new Question();
        q.setExamId(dto.getExamId());
        q.setQuestionText(dto.getQuestionText().trim());
        q.setOptionA(dto.getOptionA().trim());
        q.setOptionB(dto.getOptionB().trim());
        q.setOptionC(dto.getOptionC().trim());
        q.setOptionD(dto.getOptionD().trim());
        q.setCorrectOption(dto.getCorrectOption().trim().toUpperCase());
        q.setMarks(dto.getMarks() != null ? dto.getMarks() : 1);
        q.setExplanation(dto.getExplanation() != null ? dto.getExplanation().trim() : null);
        return questionRepository.save(q);
    }

    public void updateQuestion(QuestionFormDto dto) {
        Question q = new Question();
        q.setId(dto.getId());
        q.setExamId(dto.getExamId());
        q.setQuestionText(dto.getQuestionText().trim());
        q.setOptionA(dto.getOptionA().trim());
        q.setOptionB(dto.getOptionB().trim());
        q.setOptionC(dto.getOptionC().trim());
        q.setOptionD(dto.getOptionD().trim());
        q.setCorrectOption(dto.getCorrectOption().trim().toUpperCase());
        q.setMarks(dto.getMarks() != null ? dto.getMarks() : 1);
        q.setExplanation(dto.getExplanation() != null ? dto.getExplanation().trim() : null);
        questionRepository.update(q);
    }

    public boolean deleteQuestion(Long id) {
        return questionRepository.deleteById(id);
    }

    public QuestionFormDto toFormDto(Question q) {
        QuestionFormDto dto = new QuestionFormDto();
        dto.setId(q.getId());
        dto.setExamId(q.getExamId());
        dto.setQuestionText(q.getQuestionText());
        dto.setOptionA(q.getOptionA());
        dto.setOptionB(q.getOptionB());
        dto.setOptionC(q.getOptionC());
        dto.setOptionD(q.getOptionD());
        dto.setCorrectOption(q.getCorrectOption());
        dto.setMarks(q.getMarks());
        dto.setExplanation(q.getExplanation());
        return dto;
    }
}
