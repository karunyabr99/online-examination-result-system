package com.examflow.service;

import com.examflow.dto.DashboardMetricsDto;
import com.examflow.dto.QuestionReviewDto;
import com.examflow.dto.ResultDetailDto;
import com.examflow.model.Answer;
import com.examflow.model.Question;
import com.examflow.repository.AnswerRepository;
import com.examflow.repository.QuestionRepository;
import com.examflow.repository.ResultRepository;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ResultService {

    private final ResultRepository resultRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    public ResultService(ResultRepository resultRepository,
                         QuestionRepository questionRepository,
                         AnswerRepository answerRepository) {
        this.resultRepository = resultRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
    }

    public Optional<ResultDetailDto> getResultDetail(String attemptUuid) {
        Optional<ResultDetailDto> detailOpt = resultRepository.findByAttemptUuid(attemptUuid);
        if (detailOpt.isEmpty()) {
            return Optional.empty();
        }

        ResultDetailDto dto = detailOpt.get();
        enrichWithQuestionBreakdown(dto);
        return Optional.of(dto);
    }

    public List<ResultDetailDto> searchAndFilterResults(String search, Long examId, String passFail) {
        List<ResultDetailDto> results = resultRepository.findAllResultDetails(search, examId, passFail);
        for (ResultDetailDto dto : results) {
            enrichWithQuestionCounts(dto);
        }
        return results;
    }

    public DashboardMetricsDto getDashboardMetrics() {
        return resultRepository.getDashboardMetrics();
    }

    private void enrichWithQuestionBreakdown(ResultDetailDto dto) {
        List<Question> questions = questionRepository.findByExamId(dto.getExamId());
        List<Answer> answers = answerRepository.findByAttemptId(dto.getAttemptId());

        Map<Long, Answer> answerMap = new HashMap<>();
        for (Answer a : answers) {
            answerMap.put(a.getQuestionId(), a);
        }

        int answered = 0;
        int correct = 0;
        int incorrect = 0;
        int unattempted = 0;
        List<QuestionReviewDto> reviewList = new ArrayList<>();

        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            Answer a = answerMap.get(q.getId());

            String selectedOption = (a != null) ? a.getSelectedOption() : null;
            boolean isAnsCorrect = (a != null) && a.isCorrect();
            int marks = (a != null) ? a.getMarksAwarded() : 0;

            if (selectedOption != null && !selectedOption.isEmpty()) {
                answered++;
                if (isAnsCorrect) {
                    correct++;
                } else {
                    incorrect++;
                }
            } else {
                unattempted++;
            }

            QuestionReviewDto review = new QuestionReviewDto(
                    q.getId(),
                    i + 1,
                    q.getQuestionText(),
                    q.getOptionA(),
                    q.getOptionB(),
                    q.getOptionC(),
                    q.getOptionD(),
                    selectedOption,
                    q.getCorrectOption(),
                    isAnsCorrect,
                    marks,
                    q.getExplanation()
            );
            reviewList.add(review);
        }

        dto.setTotalQuestions(questions.size());
        dto.setAnsweredQuestions(answered);
        dto.setCorrectCount(correct);
        dto.setIncorrectCount(incorrect);
        dto.setUnattemptedCount(unattempted);
        dto.setQuestions(reviewList);
    }

    private void enrichWithQuestionCounts(ResultDetailDto dto) {
        List<Answer> answers = answerRepository.findByAttemptId(dto.getAttemptId());
        int answered = 0;
        int correct = 0;
        for (Answer a : answers) {
            if (a.getSelectedOption() != null && !a.getSelectedOption().isEmpty()) {
                answered++;
                if (a.isCorrect()) {
                    correct++;
                }
            }
        }
        dto.setAnsweredQuestions(answered);
        dto.setCorrectCount(correct);
    }

    public String exportResultsToCsv(List<ResultDetailDto> results) {
        StringBuilder csv = new StringBuilder();
        // CSV Header
        csv.append("Attempt ID,Student ID,Student Name,Course,Examination,Score,Total Marks,Percentage,Status,Submission Date\n");

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (ResultDetailDto r : results) {
            csv.append(escapeCsv(r.getAttemptUuid())).append(",");
            csv.append(escapeCsv(r.getStudentId())).append(",");
            csv.append(escapeCsv(r.getStudentName())).append(",");
            csv.append(escapeCsv(r.getCourse())).append(",");
            csv.append(escapeCsv(r.getExamTitle())).append(",");
            csv.append(r.getScore()).append(",");
            csv.append(r.getTotalMarks()).append(",");
            csv.append(String.format(Locale.US, "%.2f%%", r.getPercentage())).append(",");
            csv.append(r.getPassFail()).append(",");
            csv.append(r.getSubmittedAt() != null ? r.getSubmittedAt().format(dtf) : "N/A");
            csv.append("\n");
        }
        return csv.toString();
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
