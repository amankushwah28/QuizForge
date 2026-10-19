package com.quizforge;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class QuestionDAO {

    public List<Question> getQuestions(
            String subject,
            String difficulty,
            int limit) {

        List<Question> questions = new ArrayList<>();

        String sql =
            "SELECT * FROM questions " +
            "WHERE subject = ? AND difficulty = ? " +
            "ORDER BY RAND() LIMIT ?";

        try (
            Connection connection =
                DatabaseConnection.getConnection();

            PreparedStatement statement =
                connection.prepareStatement(sql)
        ) {

            statement.setString(1, subject);
            statement.setString(2, difficulty);
            statement.setInt(3, limit);

            ResultSet resultSet =
                statement.executeQuery();

            while (resultSet.next()) {

                Question question = new Question();

                question.setId(
                    resultSet.getInt("id"));

                question.setSubject(
                    resultSet.getString("subject"));

                question.setTopic(
                    resultSet.getString("topic"));

                question.setDifficulty(
                    resultSet.getString("difficulty"));

                question.setQuestionText(
                    resultSet.getString("question_text"));

                question.setOptionA(
                    resultSet.getString("option_a"));

                question.setOptionB(
                    resultSet.getString("option_b"));

                question.setOptionC(
                    resultSet.getString("option_c"));

                question.setOptionD(
                    resultSet.getString("option_d"));

                question.setCorrectAnswer(
                    resultSet.getString("correct_answer"));

                questions.add(question);
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return questions;
    }
}