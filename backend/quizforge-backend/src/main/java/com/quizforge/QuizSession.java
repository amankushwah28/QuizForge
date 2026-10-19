package com.quizforge;

import java.util.List;

public class QuizSession {

    private List<Question> questions;
    private String subject;
    private String difficulty;

    public QuizSession(
            List<Question> questions,
            String subject,
            String difficulty) {

        this.questions = questions;
        this.subject = subject;
        this.difficulty = difficulty;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public String getSubject() {
        return subject;
    }

    public String getDifficulty() {
        return difficulty;
    }
}
