package com.quizforge;

import java.sql.Timestamp;

public class QuizHistory {

    private int id;
    private String userEmail;
    private String subject;
    private int score;
    private int totalQuestions;
    private double percentage;
    private Timestamp quizDate;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }


    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }


    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }


    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }


    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }


    public Timestamp getQuizDate() {
        return quizDate;
    }

    public void setQuizDate(Timestamp quizDate) {
        this.quizDate = quizDate;
    }
}